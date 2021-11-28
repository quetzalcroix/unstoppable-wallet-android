package io.vextabit.wallet.modules.market.metrics

import io.vextabit.wallet.core.Clearable
import io.vextabit.wallet.core.IRateManager
import io.vextabit.wallet.core.subscribeIO
import io.vextabit.wallet.entities.DataState
import io.horizontalsystems.core.BackgroundManager
import io.horizontalsystems.core.ICurrencyManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MarketMetricsService(
        private val xRateManager: IRateManager,
        private val backgroundManager: BackgroundManager,
        private val currencyManager: ICurrencyManager
) : Clearable, BackgroundManager.Listener {

    val marketMetricsObservable: BehaviorSubject<DataState<MarketMetricsItem>> = BehaviorSubject.createDefault(DataState.Loading)

    private val disposables = CompositeDisposable()

    init {
        fetchMarketMetrics()
        backgroundManager.registerListener(this)
        currencyManager.baseCurrencyUpdatedSignal
                .subscribeIO {
                    fetchMarketMetrics()
                }
                .let {
                    disposables.add(it)
                }
    }

    override fun willEnterForeground() {
        fetchMarketMetrics()
    }

    fun refresh() {
        fetchMarketMetrics()
    }

    private fun fetchMarketMetrics() {
        marketMetricsObservable.onNext(DataState.Loading)

        xRateManager.getGlobalMarketInfoAsync(currencyManager.baseCurrency.code)
                .subscribeIO({
                    val marketMetricsItem = MarketMetricsItem.createFromGlobalCoinMarket(it, currencyManager.baseCurrency)
                    marketMetricsObservable.onNext(DataState.Success(marketMetricsItem))
                }, {
                    marketMetricsObservable.onNext(DataState.Error(it))
                })
                .let {
                    disposables.add(it)
                }
    }

    override fun clear() {
        disposables.clear()
        backgroundManager.unregisterListener(this)
    }
}
