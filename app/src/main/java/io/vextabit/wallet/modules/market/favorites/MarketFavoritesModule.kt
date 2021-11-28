package io.vextabit.wallet.modules.market.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.modules.market.list.MarketListService
import io.vextabit.wallet.modules.market.list.MarketListViewModel

object MarketFavoritesModule {

    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = MarketFavoritesService(io.vextabit.wallet.core.App.xRateManager, io.vextabit.wallet.core.App.marketFavoritesManager, io.vextabit.wallet.core.App.backgroundManager)
            val listService = MarketListService(service, io.vextabit.wallet.core.App.currencyManager)
            return MarketListViewModel(listService, io.vextabit.wallet.core.App.connectivityManager, listOf(listService, service)) as T
        }

    }

}

