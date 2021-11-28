package io.vextabit.bankwallet.modules.market.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.coinkit.models.CoinType

object MarketSearchModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = MarketSearchService(io.vextabit.bankwallet.core.App.xRateManager)
            return MarketSearchViewModel(service, listOf(service)) as T
        }
    }

}

data class CoinDataViewItem(val code: String, val name: String, val type: CoinType)
