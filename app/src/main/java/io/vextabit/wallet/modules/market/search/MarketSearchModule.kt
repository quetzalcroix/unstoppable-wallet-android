package io.vextabit.wallet.modules.market.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.coinkit.models.CoinType

object MarketSearchModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = MarketSearchService(io.vextabit.wallet.core.App.xRateManager)
            return MarketSearchViewModel(service, listOf(service)) as T
        }
    }

}

data class CoinDataViewItem(val code: String, val name: String, val type: CoinType)
