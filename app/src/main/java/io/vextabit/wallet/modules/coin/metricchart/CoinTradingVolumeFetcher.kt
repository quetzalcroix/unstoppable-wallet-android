package io.vextabit.wallet.modules.coin.metricchart

import io.vextabit.wallet.R
import io.vextabit.wallet.core.IRateManager
import io.vextabit.wallet.modules.metricchart.MetricChartModule
import io.vextabit.coinkit.models.CoinType
import io.vextabit.xrateskit.entities.TimePeriod
import io.reactivex.Single

class CoinTradingVolumeFetcher(
        private val rateManager: IRateManager,
        private val coinType: CoinType
):MetricChartModule.IMetricChartFetcher, MetricChartModule.IMetricChartConfiguration {

    override val title = R.string.CoinPage_TradingVolume

    override val description = R.string.CoinPage_TvlDescription

    override val valueType: MetricChartModule.ValueType
        get() = MetricChartModule.ValueType.CompactCurrencyValue

    override fun fetchSingle(currencyCode: String, timePeriod: TimePeriod): Single<List<MetricChartModule.Item>> {
        return rateManager.getCoinMarketVolumePointsAsync(coinType, currencyCode, timePeriod)
                .map { points ->
                    points.map {
                        MetricChartModule.Item(it.volume24h, it.timestamp)
                    }
                }
    }
}
