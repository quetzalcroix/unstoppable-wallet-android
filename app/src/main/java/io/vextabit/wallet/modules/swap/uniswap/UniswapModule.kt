package io.vextabit.wallet.modules.swap.uniswap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.modules.swap.SwapMainModule
import io.vextabit.wallet.modules.swap.SwapViewItemHelper
import io.vextabit.wallet.modules.swap.allowance.SwapAllowanceService
import io.vextabit.wallet.modules.swap.allowance.SwapAllowanceViewModel
import io.vextabit.wallet.modules.swap.allowance.SwapPendingAllowanceService
import io.vextabit.wallet.modules.swap.providers.UniswapProvider
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.uniswapkit.UniswapKit

object UniswapModule {

    data class GuaranteedAmountViewItem(val title: String, val value: String)

    data class PriceImpactViewItem(val level: UniswapTradeService.PriceImpactLevel, val value: String)

    class AllowanceViewModelFactory(
            private val service: UniswapService
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SwapAllowanceViewModel::class.java -> {
                    SwapAllowanceViewModel(service, service.allowanceService, service.pendingAllowanceService, SwapViewItemHelper(io.vextabit.wallet.core.App.numberFormatter)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    class Factory(
            dex: SwapMainModule.Dex
    ) : ViewModelProvider.Factory {

        private val evmKit: EthereumKit by lazy { dex.blockchain.evmKit!! }
        private val uniswapKit by lazy { UniswapKit.getInstance(evmKit) }
        private val uniswapProvider by lazy { UniswapProvider(uniswapKit) }
        private val allowanceService by lazy { SwapAllowanceService(uniswapProvider.routerAddress, io.vextabit.wallet.core.App.adapterManager, evmKit) }
        private val pendingAllowanceService by lazy { SwapPendingAllowanceService(io.vextabit.wallet.core.App.adapterManager, allowanceService) }
        private val service by lazy {
            UniswapService(
                    dex,
                    tradeService,
                    allowanceService,
                    pendingAllowanceService,
                    io.vextabit.wallet.core.App.adapterManager
            )
        }
        private val tradeService by lazy {
            UniswapTradeService(evmKit, uniswapProvider)
        }
        private val formatter by lazy {
            SwapViewItemHelper(io.vextabit.wallet.core.App.numberFormatter)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            return when (modelClass) {
                UniswapViewModel::class.java -> {
                    UniswapViewModel(service, tradeService, pendingAllowanceService, formatter) as T
                }
                SwapAllowanceViewModel::class.java -> {
                    SwapAllowanceViewModel(service, allowanceService, pendingAllowanceService, formatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

}
