package io.vextabit.wallet.modules.swap.oneinch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.modules.swap.SwapMainModule
import io.vextabit.wallet.modules.swap.SwapViewItemHelper
import io.vextabit.wallet.modules.swap.allowance.SwapAllowanceService
import io.vextabit.wallet.modules.swap.allowance.SwapAllowanceViewModel
import io.vextabit.wallet.modules.swap.allowance.SwapPendingAllowanceService
import io.horizontalsystems.ethereumkit.core.EthereumKit

object OneInchModule {

    class AllowanceViewModelFactory(
            private val service: OneInchSwapService
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

    class Factory(dex: SwapMainModule.Dex) : ViewModelProvider.Factory {
        private val evmKit: EthereumKit by lazy { dex.blockchain.evmKit!! }
        private val oneIncKitHelper by lazy { OneInchKitHelper(evmKit) }
        private val allowanceService by lazy { SwapAllowanceService(oneIncKitHelper.smartContractAddress, io.vextabit.wallet.core.App.adapterManager, evmKit) }
        private val pendingAllowanceService by lazy { SwapPendingAllowanceService(io.vextabit.wallet.core.App.adapterManager, allowanceService) }
        private val service by lazy {
            OneInchSwapService(
                    dex,
                    tradeService,
                    allowanceService,
                    pendingAllowanceService,
                    io.vextabit.wallet.core.App.adapterManager
            )
        }
        private val tradeService by lazy {
            OneInchTradeService(evmKit, oneIncKitHelper)
        }
        private val formatter by lazy {
            SwapViewItemHelper(io.vextabit.wallet.core.App.numberFormatter)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                OneInchSwapViewModel::class.java -> {
                    OneInchSwapViewModel(service, tradeService, pendingAllowanceService) as T
                }
                SwapAllowanceViewModel::class.java -> {
                    SwapAllowanceViewModel(service, allowanceService, pendingAllowanceService, formatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

}
