package io.vextabit.bankwallet.modules.market.advancedsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.modules.market.list.IMarketListFetcher
import io.horizontalsystems.bankwallet.modules.market.list.MarketListService
import io.horizontalsystems.bankwallet.modules.market.list.MarketListViewModel

object MarketAdvancedSearchResultsModule {
    class Factory(val service: IMarketListFetcher) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val listService = MarketListService(service, io.vextabit.bankwallet.core.App.currencyManager)
            return MarketListViewModel(listService, io.vextabit.bankwallet.core.App.connectivityManager, listOf(listService)) as T
        }

    }
}
