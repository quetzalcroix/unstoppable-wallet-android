package io.vextabit.wallet.modules.market.advancedsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.modules.market.list.IMarketListFetcher
import io.vextabit.wallet.modules.market.list.MarketListService
import io.vextabit.wallet.modules.market.list.MarketListViewModel

object MarketAdvancedSearchResultsModule {
    class Factory(val service: IMarketListFetcher) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val listService = MarketListService(service, io.vextabit.wallet.core.App.currencyManager)
            return MarketListViewModel(listService, io.vextabit.wallet.core.App.connectivityManager, listOf(listService)) as T
        }

    }
}
