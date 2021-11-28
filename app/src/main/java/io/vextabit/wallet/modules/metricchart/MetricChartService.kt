package io.vextabit.wallet.modules.metricchart

import io.vextabit.wallet.core.Clearable
import io.vextabit.wallet.core.subscribeIO
import io.vextabit.wallet.entities.DataState
import io.horizontalsystems.chartview.ChartView
import io.horizontalsystems.core.entities.Currency
import io.vextabit.xrateskit.entities.TimePeriod
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MetricChartService(
        val currency: Currency,
        private val fetcher: MetricChartModule.IMetricChartFetcher,
): Clearable {

    val stateObservable: BehaviorSubject<DataState<List<MetricChartModule.Item>>> = BehaviorSubject.createDefault(DataState.Loading)

    private var chartInfoDisposable: Disposable? = null

    override fun clear() {
       chartInfoDisposable?.dispose()
    }

    fun updateChartInfo(chartType: ChartView.ChartType) {
        chartInfoDisposable?.dispose()

        stateObservable.onNext(DataState.Loading)

        val timePeriod = getTimePeriod(chartType)

        fetcher.fetchSingle(currency.code, timePeriod)
                .subscribeIO({
                    stateObservable.onNext(DataState.Success(it))
                }, {
                    stateObservable.onNext(DataState.Error(it))
                }).let {
                    chartInfoDisposable = it
                }
    }

    private fun getTimePeriod(chartType: ChartView.ChartType): TimePeriod{
        return when(chartType){
            ChartView.ChartType.DAILY -> TimePeriod.HOUR_24
            ChartView.ChartType.WEEKLY -> TimePeriod.DAY_7
            ChartView.ChartType.MONTHLY -> TimePeriod.DAY_30
            else -> throw IllegalArgumentException("Wrong ChartType")
        }
    }
}
