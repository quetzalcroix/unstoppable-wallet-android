package io.vextabit.wallet.modules.walletconnect.request.sendtransaction

import io.vextabit.wallet.modules.walletconnect.WalletConnectSendEthereumTransactionRequest
import io.vextabit.wallet.modules.walletconnect.WalletConnectService
import io.horizontalsystems.ethereumkit.models.TransactionData

class WalletConnectSendEthereumTransactionRequestService(
        private val request: WalletConnectSendEthereumTransactionRequest,
        private val baseService: WalletConnectService
) {
    private val transaction = request.transaction

    val transactionData = TransactionData(transaction.to, transaction.value, transaction.data)
    val gasPrice: Long? = transaction.gasPrice

    fun approve(transactionHash: ByteArray) {
        baseService.approveRequest(request.id, transactionHash)
    }

    fun reject() {
        baseService.rejectRequest(request.id)
    }

}
