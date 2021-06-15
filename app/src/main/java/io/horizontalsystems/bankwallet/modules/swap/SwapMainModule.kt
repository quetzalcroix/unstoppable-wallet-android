package io.horizontalsystems.bankwallet.modules.swap

import android.os.Parcelable
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.fiat.AmountTypeSwitchService
import io.horizontalsystems.bankwallet.core.fiat.FiatService
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.bankwallet.modules.swap.coincard.*
import io.horizontalsystems.coinkit.models.Coin
import io.horizontalsystems.coinkit.models.CoinType
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

object SwapMainModule {

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

        fun restoreState(swapInputs: SwapInputs)

        fun switchCoins()
    }

    interface ISwapService {
        val balanceFrom: BigDecimal?
        val balanceFromObservable: Observable<Optional<BigDecimal>>

        val balanceTo: BigDecimal?
        val balanceToObservable: Observable<Optional<BigDecimal>>

        val errors: List<Throwable>
        val errorsObservable: Observable<List<Throwable>>
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

    @Parcelize
    data class SwapInputs(
            val coinFrom: Coin? = null,
            val coinTo: Coin? = null,
            val amountFrom: BigDecimal? = null,
            val amountTo: BigDecimal? = null,
            val amountType: AmountType = AmountType.ExactFrom
    ) : Parcelable

    @Parcelize
    class Dex(val blockchain: Blockchain, val provider: Provider, val swapInputs: SwapInputs? = null) : Parcelable {

//        var blockchain: Blockchain = blockchain
//            set(blockchain) {
//                field = blockchain
//                if (!blockchain.supportedProviders.contains(provider)) {
//                    provider = blockchain.supportedProviders.first()
//                }
//            }
//
//        var provider: Provider = provider
//            set(value) {
//                field = value
//                if (!provider.supportedBlockchains.contains(blockchain)) {
//                    blockchain = provider.supportedBlockchains.first()
//                }
//            }

        val evmKit: EthereumKit?
            get() = when (blockchain) {
                Blockchain.Ethereum -> App.ethereumKitManager.evmKit
                Blockchain.BinanceSmartChain -> App.binanceSmartChainKitManager.evmKit
            }

        val coin: Coin?
            get() = when (blockchain) {
                Blockchain.Ethereum -> App.coinKit.getCoin(CoinType.Ethereum)
                Blockchain.BinanceSmartChain -> App.coinKit.getCoin(CoinType.BinanceSmartChain)
            }

        @Parcelize
        enum class Blockchain(val id: String) : Parcelable {
            Ethereum("ethereum"), BinanceSmartChain("binanceSmartChain");

            val supportedProviders: List<Provider>
                get() = when (this) {
                    Ethereum -> listOf(Provider.Uniswap, Provider.OneInch)
                    BinanceSmartChain -> listOf(Provider.PancakeSwap, Provider.OneInch)
                }
        }

        @Parcelize
        enum class Provider(val id: String) : Parcelable {
            Uniswap("uniswap"), OneInch("1inch"), PancakeSwap("pancake");

            val supportedBlockchains: List<Blockchain>
                get() = when (this) {
                    Uniswap -> listOf(Blockchain.Ethereum)
                    PancakeSwap -> listOf(Blockchain.BinanceSmartChain)
                    OneInch -> listOf(Blockchain.Ethereum, Blockchain.BinanceSmartChain)
                }

            companion object {
                private val map = values().associateBy(Provider::id)

                fun fromId(id: String?): Provider? = map[id]
            }
        }
    }

    class Factory(
            owner: SavedStateRegistryOwner,
            private val dex: Dex
    ) : AbstractSavedStateViewModelFactory(owner, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {

            return when (modelClass) {
                SwapMainViewModel::class.java -> {
                    SwapMainViewModel(DexManager(dex)) as T
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
            SwapCoinProvider(dex, App.coinManager, App.walletManager, App.adapterManager, App.currencyManager, App.xRateManager)
        }
        private val fromCoinCardService by lazy {
            SwapFromCoinCardService(service, tradeService, coinProvider)
        }
        private val toCoinCardService by lazy {
            SwapToCoinCardService(service, tradeService, coinProvider)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return when (modelClass) {
                SwapCoinCardViewModel::class.java -> {
                    val fiatService = FiatService(switchService, App.currencyManager, App.xRateManager)
                    val coinCardService: ISwapCoinCardService
                    var maxButtonEnabled = false

                    if (key == coinCardTypeFrom) {
                        coinCardService = fromCoinCardService
                        switchService.fromListener = fiatService
                        maxButtonEnabled = true
                    } else {
                        coinCardService = toCoinCardService
                        switchService.toListener = fiatService
                    }
                    val formatter = SwapViewItemHelper(App.numberFormatter)
                    SwapCoinCardViewModel(coinCardService, fiatService, switchService, maxButtonEnabled, formatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

    }

    const val coinCardTypeFrom = "coinCardTypeFrom"
    const val coinCardTypeTo = "coinCardTypeTo"

}
