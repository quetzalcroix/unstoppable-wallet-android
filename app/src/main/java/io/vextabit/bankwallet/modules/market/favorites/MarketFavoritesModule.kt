package io.vextabit.bankwallet.modules.market.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.modules.market.list.MarketListService
import io.horizontalsystems.bankwallet.modules.market.list.MarketListViewModel

object MarketFavoritesModule {

    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = MarketFavoritesService(io.vextabit.bankwallet.core.App.xRateManager, io.vextabit.bankwallet.core.App.marketFavoritesManager, io.vextabit.bankwallet.core.App.backgroundManager)
            val listService = MarketListService(service, io.vextabit.bankwallet.core.App.currencyManager)
            return MarketListViewModel(listService, io.vextabit.bankwallet.core.App.connectivityManager, listOf(listService, service)) as T
        }

    }

}

