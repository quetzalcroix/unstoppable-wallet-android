package io.vextabit.bankwallet.modules.swap.confirmation.oneinch

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.ethereum.EthereumFeeViewModel
import io.horizontalsystems.bankwallet.core.ethereum.EvmCoinServiceFactory
import io.horizontalsystems.bankwallet.core.factories.FeeRateProviderFactory
import io.horizontalsystems.bankwallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule
import io.horizontalsystems.bankwallet.modules.swap.oneinch.OneInchKitHelper
import io.horizontalsystems.core.findNavController

object OneInchConfirmationModule {
    private const val oneInchSwapParametersKey = "oneInchSwapParametersKey"

    class Factory(
            private val blockchain: SwapMainModule.Blockchain,
            private val arguments: Bundle
    ) : ViewModelProvider.Factory {

        private val oneInchSwapParameters by lazy { arguments.getParcelable<OneInchSwapParameters>(oneInchSwapParametersKey)!! }
        private val evmKit by lazy { blockchain.evmKit!! }
        private val oneInchKitHelper by lazy { OneInchKitHelper(evmKit) }
        private val coin by lazy { blockchain.coin!! }
        private val transactionService by lazy {
            val feeRateProvider = FeeRateProviderFactory.provider(coin)!!
            OneInchTransactionFeeService(oneInchKitHelper, oneInchSwapParameters, feeRateProvider)
        }
        private val coinServiceFactory by lazy { EvmCoinServiceFactory(coin, io.vextabit.bankwallet.core.App.coinKit, io.vextabit.bankwallet.core.App.currencyManager, io.vextabit.bankwallet.core.App.xRateManager) }
        private val sendService by lazy { OneInchSendEvmTransactionService(evmKit, transactionService, io.vextabit.bankwallet.core.App.activateCoinManager) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(sendService, coinServiceFactory) as T
                }
                EthereumFeeViewModel::class.java -> {
                    EthereumFeeViewModel(transactionService, coinServiceFactory.baseCoinService) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    fun start(fragment: Fragment, navigateTo: Int, navOptions: NavOptions, oneInchSwapParameters: OneInchSwapParameters) {
        fragment.findNavController().navigate(navigateTo, bundleOf(oneInchSwapParametersKey to oneInchSwapParameters), navOptions)
    }

}
