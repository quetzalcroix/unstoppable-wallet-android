package io.vextabit.wallet.modules.swap.confirmation.uniswap

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.ethereum.EthereumFeeViewModel
import io.vextabit.wallet.core.ethereum.EvmCoinServiceFactory
import io.vextabit.wallet.core.ethereum.EvmTransactionService
import io.vextabit.wallet.core.factories.FeeRateProviderFactory
import io.vextabit.wallet.modules.sendevm.SendEvmData
import io.vextabit.wallet.modules.sendevm.SendEvmModule
import io.vextabit.wallet.modules.sendevmtransaction.SendEvmTransactionService
import io.vextabit.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.vextabit.wallet.modules.swap.SwapMainModule
import io.horizontalsystems.core.findNavController

object UniswapConfirmationModule {

    class Factory(
        private val blockchain: SwapMainModule.Blockchain,
        private val sendEvmData: SendEvmData
    ) : ViewModelProvider.Factory {

        private val evmKit by lazy { blockchain.evmKit!! }
        private val coin by lazy { blockchain.coin!! }
        private val transactionService by lazy {
            val feeRateProvider = FeeRateProviderFactory.provider(coin)!!
            EvmTransactionService(evmKit, feeRateProvider, 20)
        }
        private val coinServiceFactory by lazy {
            EvmCoinServiceFactory(
                coin,
                io.vextabit.wallet.core.App.coinKit,
                io.vextabit.wallet.core.App.currencyManager,
                io.vextabit.wallet.core.App.xRateManager
            )
        }
        private val sendService by lazy {
            SendEvmTransactionService(
                sendEvmData,
                evmKit,
                transactionService,
                io.vextabit.wallet.core.App.activateCoinManager
            )
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(sendService, coinServiceFactory) as T
                }
                EthereumFeeViewModel::class.java -> {
                    EthereumFeeViewModel(
                        transactionService,
                        coinServiceFactory.baseCoinService
                    ) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    fun start(
        fragment: Fragment,
        navigateTo: Int,
        navOptions: NavOptions,
        sendEvmData: SendEvmData
    ) {
        val arguments = bundleOf(
            SendEvmModule.transactionDataKey to SendEvmModule.TransactionDataParcelable(sendEvmData.transactionData),
            SendEvmModule.additionalInfoKey to sendEvmData.additionalInfo
        )
        fragment.findNavController().navigate(navigateTo, arguments, navOptions)
    }

}