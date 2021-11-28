package io.vextabit.wallet.modules.sendevm.confirmation

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
import io.horizontalsystems.coinkit.models.CoinType
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.core.EthereumKit.NetworkType

object SendEvmConfirmationModule {

    class Factory(
            private val evmKit: EthereumKit,
            private val sendEvmData: SendEvmData
    ) : ViewModelProvider.Factory {

        private val feeCoin by lazy {
            when (evmKit.networkType) {
                NetworkType.EthMainNet,
                NetworkType.EthRopsten,
                NetworkType.EthKovan,
                NetworkType.EthGoerli,
                NetworkType.EthRinkeby -> io.vextabit.wallet.core.App.coinKit.getCoin(CoinType.Ethereum)!!
                NetworkType.BscMainNet -> io.vextabit.wallet.core.App.coinKit.getCoin(CoinType.BinanceSmartChain)!!
            }
        }
        private val transactionService by lazy {
            val feeRateProvider = FeeRateProviderFactory.provider(feeCoin)!!
            EvmTransactionService(evmKit, feeRateProvider, 20)
        }
        private val coinServiceFactory by lazy { EvmCoinServiceFactory(feeCoin, io.vextabit.wallet.core.App.coinKit, io.vextabit.wallet.core.App.currencyManager, io.vextabit.wallet.core.App.xRateManager) }
        private val sendService by lazy { SendEvmTransactionService(sendEvmData, evmKit, transactionService, io.vextabit.wallet.core.App.activateCoinManager) }

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

    fun start(fragment: Fragment, navigateTo: Int, navOptions: NavOptions, sendData: SendEvmData) {
        val arguments = bundleOf(
                SendEvmModule.transactionDataKey to SendEvmModule.TransactionDataParcelable(sendData.transactionData),
                SendEvmModule.additionalInfoKey to sendData.additionalInfo
        )
        fragment.findNavController().navigate(navigateTo, arguments, navOptions)
    }

}
