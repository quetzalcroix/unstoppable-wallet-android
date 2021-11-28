package io.vextabit.wallet.entities.transactionrecords.evm

import io.vextabit.wallet.core.adapters.BaseEvmAdapter
import io.vextabit.wallet.core.adapters.EvmAdapter
import io.vextabit.wallet.entities.CoinValue
import io.vextabit.wallet.entities.transactionrecords.TransactionRecord
import io.vextabit.coinkit.models.Coin
import io.horizontalsystems.core.toHexString
import io.horizontalsystems.ethereumkit.models.FullTransaction

abstract class EvmTransactionRecord(fullTransaction: FullTransaction, baseCoin: Coin) :
    TransactionRecord(
        uid = fullTransaction.transaction.hash.toHexString(),
        transactionHash = fullTransaction.transaction.hash.toHexString(),
        transactionIndex = fullTransaction.receiptWithLogs?.receipt?.transactionIndex ?: 0,
        blockHeight = fullTransaction.receiptWithLogs?.receipt?.blockNumber?.toInt(),
        confirmationsThreshold = BaseEvmAdapter.confirmationsThreshold,
        timestamp = fullTransaction.transaction.timestamp,
        failed = fullTransaction.isFailed()
    ) {

    val fee: CoinValue
    open val foreignTransaction: Boolean = false

    init {
        val feeAmount: Long = fullTransaction.receiptWithLogs?.receipt?.gasUsed
            ?: fullTransaction.transaction.gasLimit

        val feeDecimal = feeAmount.toBigDecimal()
            .multiply(fullTransaction.transaction.gasPrice.toBigDecimal())
            .movePointLeft(EvmAdapter.decimal).stripTrailingZeros()

        fee = CoinValue(baseCoin, feeDecimal)
    }

}
