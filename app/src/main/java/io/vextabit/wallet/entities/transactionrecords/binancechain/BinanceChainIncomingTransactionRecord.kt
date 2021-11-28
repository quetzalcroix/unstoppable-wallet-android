package io.vextabit.wallet.entities.transactionrecords.binancechain

import io.vextabit.wallet.entities.CoinValue
import io.horizontalsystems.binancechainkit.models.TransactionInfo
import io.vextabit.coinkit.models.Coin

class BinanceChainIncomingTransactionRecord(
    transaction: TransactionInfo,
    feeCoin: Coin,
    coin: Coin
) : BinanceChainTransactionRecord(transaction, feeCoin) {
    val value = CoinValue(coin, transaction.amount.toBigDecimal())
    val from = transaction.from

    override val mainValue: CoinValue = value

}
