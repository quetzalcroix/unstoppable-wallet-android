package io.horizontalsystems.bankwallet.modules.swap.oneinch

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.fiat.AmountTypeSwitchService
import io.horizontalsystems.bankwallet.core.fiat.FiatService
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule
import io.horizontalsystems.bankwallet.modules.swap.SwapViewItemHelper
import io.horizontalsystems.bankwallet.modules.swap.allowance.SwapAllowanceService
import io.horizontalsystems.bankwallet.modules.swap.allowance.SwapAllowanceViewModel
import io.horizontalsystems.bankwallet.modules.swap.allowance.SwapPendingAllowanceService
import io.horizontalsystems.bankwallet.modules.swap.coincard.*
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.models.Address

object OneInchModule {
    class Factory(
            owner: SavedStateRegistryOwner,
            private val dex: SwapMainModule.Dex
    ) : AbstractSavedStateViewModelFactory(owner, null) {

        private val evmKit: EthereumKit by lazy { dex.evmKit!! }
        private val allowanceService by lazy { SwapAllowanceService(Address("0x11111112542d85b3ef69ae05771c2dccff4faa26"), App.adapterManager, evmKit) }
        private val pendingAllowanceService by lazy { SwapPendingAllowanceService(App.adapterManager, allowanceService) }
        private val service by lazy {
            OneInchSwapService(
                    dex,
                    tradeService,
                    allowanceService,
                    pendingAllowanceService,
                    App.adapterManager
            )
        }
        private val tradeService by lazy {
            OneInchTradeService(evmKit, coinFrom = dex.swapInputs?.coinFrom)
        }
        private val formatter by lazy {
            SwapViewItemHelper(App.numberFormatter)
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
        private val switchService by lazy {
            AmountTypeSwitchService()
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {

            return when (modelClass) {
                OneInchSwapViewModel::class.java -> {
                    OneInchSwapViewModel(service, tradeService, pendingAllowanceService, formatter) as T
                }
                SwapCoinCardViewModel::class.java -> {
                    val fiatService = FiatService(switchService, App.currencyManager, App.xRateManager)
                    val coinCardService: ISwapCoinCardService
                    var maxButtonEnabled = false

                    if (key == SwapMainModule.coinCardTypeFrom) {
                        coinCardService = fromCoinCardService
                        switchService.fromListener = fiatService
                        maxButtonEnabled = true
                    } else {
                        coinCardService = toCoinCardService
                        switchService.toListener = fiatService
                    }
                    SwapCoinCardViewModel(coinCardService, fiatService, switchService, maxButtonEnabled, formatter) as T
                }
                SwapAllowanceViewModel::class.java -> {
                    SwapAllowanceViewModel(service, allowanceService, pendingAllowanceService, formatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

}
