package io.vextabit.wallet.modules.transactions

import io.vextabit.wallet.entities.CurrencyValue
import io.vextabit.wallet.entities.LastBlockInfo
import io.vextabit.coinkit.models.Coin
import io.horizontalsystems.core.entities.Currency
import java.math.BigDecimal

class TransactionMetadataDataSource {

    private val lastBlockInfos = mutableMapOf<TransactionSource, LastBlockInfo>()
    private val rates = mutableMapOf<Coin, MutableMap<Long, CurrencyValue>>()

    fun setLastBlockInfo(lastBlockInfo: LastBlockInfo, source: TransactionSource) {
        lastBlockInfos[source] = lastBlockInfo
    }

    fun getLastBlockInfo(source: TransactionSource): LastBlockInfo? {
        return lastBlockInfos[source]
    }

    fun setRate(rateValue: BigDecimal, coin: Coin, currency: Currency, timestamp: Long) {
        if (!rates.containsKey(coin)) {
            rates[coin] = mutableMapOf()
        }

        rates[coin]?.set(timestamp, CurrencyValue(currency, rateValue))
    }

    fun getRate(coin: Coin, timestamp: Long): CurrencyValue? {
        return rates[coin]?.get(timestamp)
    }

    fun clearRates() {
        rates.clear()
    }

}
