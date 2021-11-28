package io.vextabit.wallet.modules.swap

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.savedstate.SavedStateRegistryOwner
import io.vextabit.wallet.R
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.fiat.AmountTypeSwitchService
import io.vextabit.wallet.core.fiat.FiatService
import io.vextabit.wallet.entities.CurrencyValue
import io.vextabit.wallet.modules.swap.coincard.*
import io.vextabit.wallet.modules.swap.oneinch.OneInchFragment
import io.vextabit.wallet.modules.swap.settings.SwapSettingsBaseFragment
import io.vextabit.wallet.modules.swap.settings.oneinch.OneInchSettingsFragment
import io.vextabit.wallet.modules.swap.settings.uniswap.UniswapSettingsFragment
import io.vextabit.wallet.modules.swap.uniswap.UniswapFragment
import io.vextabit.coinkit.models.Coin
import io.vextabit.coinkit.models.CoinType
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

object SwapMainModule {

    const val coinCardTypeFrom = "coinCardTypeFrom"
    const val coinCardTypeTo = "coinCardTypeTo"

    private const val coinFromKey = "coinFromKey"

    fun start(fragment: Fragment, navOptions: NavOptions, coinFrom: Coin) {
        fragment.findNavController().navigate(
            R.id.mainFragment_to_swapFragment,
            bundleOf(coinFromKey to coinFrom),
            navOptions
        )
    }

    interface ISwapProvider : Parcelable {
        val id: String
        val title: String
        val url: String
        val fragment: SwapBaseFragment
        val settingsFragment: SwapSettingsBaseFragment

        fun supports(blockchain: Blockchain): Boolean
    }

    @Parcelize
    object UniswapProvider : ISwapProvider {
        override val id = "uniswap"
        override val title = "Uniswap"
        override val url = "https://uniswap.org/"
        override val fragment: SwapBaseFragment
            get() = UniswapFragment()
        override val settingsFragment: SwapSettingsBaseFragment
            get() = UniswapSettingsFragment()

        override fun supports(blockchain: Blockchain): Boolean {
            return blockchain == Blockchain.Ethereum
        }
    }

    @Parcelize
    object PancakeSwapProvider : ISwapProvider {
        override val id = "pancake"
        override val title = "PancakeSwap"
        override val url = "https://pancakeswap.finance/"
        override val fragment: SwapBaseFragment
            get() = UniswapFragment()
        override val settingsFragment: SwapSettingsBaseFragment
            get() = UniswapSettingsFragment()

        override fun supports(blockchain: Blockchain): Boolean {
            return blockchain == Blockchain.BinanceSmartChain
        }
    }

    @Parcelize
    object OneInchProvider : ISwapProvider {
        override val id = "oneinch"
        override val title = "1inch"
        override val url = "https://app.1inch.io/"
        override val fragment: SwapBaseFragment
            get() = OneInchFragment()
        override val settingsFragment: SwapSettingsBaseFragment
            get() = OneInchSettingsFragment()

        override fun supports(blockchain: Blockchain): Boolean {
            return blockchain.mainNet && (blockchain == Blockchain.Ethereum || blockchain == Blockchain.BinanceSmartChain)
        }
    }

    @Parcelize
    enum class Blockchain(val id: String, val title: String) : Parcelable {
        Ethereum("ethereum", "Ethereum"),
        BinanceSmartChain("binanceSmartChain", "Binance Smart Chain");

        val evmKit: EthereumKit?
            get() = when (this) {
                Ethereum -> io.vextabit.wallet.core.App.ethereumKitManager.evmKit
                BinanceSmartChain -> io.vextabit.wallet.core.App.binanceSmartChainKitManager.evmKit
            }

        val mainNet: Boolean
            get() = evmKit?.networkType?.isMainNet ?: false

        val coin: Coin?
            get() = when (this) {
                Ethereum -> io.vextabit.wallet.core.App.coinKit.getCoin(CoinType.Ethereum)
                BinanceSmartChain -> io.vextabit.wallet.core.App.coinKit.getCoin(CoinType.BinanceSmartChain)
            }
    }

    @Parcelize
    class Dex(val blockchain: Blockchain, val provider: ISwapProvider) : Parcelable

    @Parcelize
    enum class AmountType : Parcelable {
        ExactFrom, ExactTo
    }

    interface ISwapTradeService {
        val coinFrom: Coin?
        val coinFromObservable: Observable<Optional<Coin>>
        val amountFrom: BigDecimal?
        val amountFromObservable: Observable<Optional<BigDecimal>>

        val coinTo: Coin?
        val coinToObservable: Observable<Optional<Coin>>
        val amountTo: BigDecimal?
        val amountToObservable: Observable<Optional<BigDecimal>>

        val amountType: AmountType
        val amountTypeObservable: Observable<AmountType>

        fun enterCoinFrom(coin: Coin?)
        fun enterAmountFrom(amount: BigDecimal?)

        fun enterCoinTo(coin: Coin?)
        fun enterAmountTo(amount: BigDecimal?)

        fun restoreState(swapProviderState: SwapProviderState)

        fun switchCoins()
    }

    interface ISwapService {
        val balanceFrom: BigDecimal?
        val balanceFromObservable: Observable<Optional<BigDecimal>>

        val balanceTo: BigDecimal?
        val balanceToObservable: Observable<Optional<BigDecimal>>

        val errors: List<Throwable>
        val errorsObservable: Observable<List<Throwable>>

        fun start()
        fun stop()
    }

    sealed class SwapError : Throwable() {
        object InsufficientBalanceFrom : SwapError()
        object InsufficientAllowance : SwapError()
        object ForbiddenPriceImpactLevel : SwapError()
    }

    @Parcelize
    data class CoinBalanceItem(
        val coin: Coin,
        val balance: BigDecimal?,
        val fiatBalanceValue: CurrencyValue?,
    ) : Parcelable

    enum class ApproveStep {
        NA, ApproveRequired, Approving, Approved
    }

    @Parcelize
    data class SwapProviderState(
        val coinFrom: Coin? = null,
        val coinTo: Coin? = null,
        val amountFrom: BigDecimal? = null,
        val amountTo: BigDecimal? = null,
        val amountType: AmountType = AmountType.ExactFrom
    ) : Parcelable

    class Factory(arguments: Bundle) : ViewModelProvider.Factory {
        private val coinFrom: Coin? = arguments.getParcelable(coinFromKey)
        private val swapProviders: List<ISwapProvider> =
            listOf(UniswapProvider, PancakeSwapProvider, OneInchProvider)

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return when (modelClass) {
                SwapMainViewModel::class.java -> {
                    SwapMainViewModel(
                        SwapMainService(
                            coinFrom,
                            swapProviders,
                            io.vextabit.wallet.core.App.localStorage
                        )
                    ) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

    }

    class CoinCardViewModelFactory(
        owner: SavedStateRegistryOwner,
        private val dex: Dex,
        private val service: ISwapService,
        private val tradeService: ISwapTradeService
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        private val switchService by lazy {
            AmountTypeSwitchService()
        }
        private val coinProvider by lazy {
            SwapCoinProvider(
                dex,
                io.vextabit.wallet.core.App.coinManager,
                io.vextabit.wallet.core.App.walletManager,
                io.vextabit.wallet.core.App.adapterManager,
                io.vextabit.wallet.core.App.currencyManager,
                io.vextabit.wallet.core.App.xRateManager
            )
        }
        private val fromCoinCardService by lazy {
            SwapFromCoinCardService(service, tradeService, coinProvider)
        }
        private val toCoinCardService by lazy {
            SwapToCoinCardService(service, tradeService, coinProvider)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return when (modelClass) {
                SwapCoinCardViewModel::class.java -> {
                    val fiatService =
                        FiatService(switchService, io.vextabit.wallet.core.App.currencyManager, io.vextabit.wallet.core.App.xRateManager)
                    val coinCardService: ISwapCoinCardService
                    var maxButtonEnabled = false
                    val resetAmountOnCoinSelect: Boolean

                    if (key == coinCardTypeFrom) {
                        coinCardService = fromCoinCardService
                        switchService.fromListener = fiatService
                        maxButtonEnabled = true
                        resetAmountOnCoinSelect = true
                    } else {
                        coinCardService = toCoinCardService
                        switchService.toListener = fiatService
                        resetAmountOnCoinSelect = false
                    }
                    val formatter = SwapViewItemHelper(io.vextabit.wallet.core.App.numberFormatter)
                    SwapCoinCardViewModel(
                        coinCardService,
                        fiatService,
                        switchService,
                        maxButtonEnabled,
                        formatter,
                        resetAmountOnCoinSelect
                    ) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

    }


}
