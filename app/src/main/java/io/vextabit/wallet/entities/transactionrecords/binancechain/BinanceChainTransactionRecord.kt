package io.vextabit.wallet.entities.transactionrecords.binancechain

import io.vextabit.wallet.core.adapters.BinanceAdapter
import io.vextabit.wallet.entities.CoinValue
import io.vextabit.wallet.entities.transactionrecords.TransactionRecord
import io.horizontalsystems.binancechainkit.models.TransactionInfo
import io.horizontalsystems.coinkit.models.Coin

abstract class BinanceChainTransactionRecord(
    transaction: TransactionInfo,
    feeCoin: Coin
) : TransactionRecord(
    uid = transaction.hash,
    transactionHash = transaction.hash,
    transactionIndex = 0,
    blockHeight = transaction.blockNumber,
    confirmationsThreshold = BinanceAdapter.confirmationsThreshold,
    timestamp = transaction.date.time / 1000,
    failed = false
) {

    val fee = CoinValue(feeCoin, BinanceAdapter.transferFee)
    val memo = transaction.memo

}
