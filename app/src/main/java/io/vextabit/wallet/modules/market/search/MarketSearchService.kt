package io.vextabit.wallet.modules.market.search

import io.vextabit.wallet.core.Clearable
import io.vextabit.wallet.core.IRateManager
import io.vextabit.xrateskit.entities.CoinData
import io.reactivex.subjects.BehaviorSubject
import java.util.*

class MarketSearchService(private val xRateManager: IRateManager) : Clearable {

    var query: String = ""
        set(value) {
            field = value

            fetch()
        }

    val itemsAsync = BehaviorSubject.createDefault(Optional.empty<List<CoinData>>())

    private fun fetch() {
        val queryTrimmed = query.trim()

        if (queryTrimmed.count() < 2) {
            itemsAsync.onNext(Optional.empty())
        } else {
            itemsAsync.onNext(Optional.of(xRateManager.searchCoins(queryTrimmed)))
        }
    }

    override fun clear() = Unit
}