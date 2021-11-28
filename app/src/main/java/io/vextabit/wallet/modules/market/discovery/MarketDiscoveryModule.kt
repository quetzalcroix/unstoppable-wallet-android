package io.vextabit.wallet.modules.market.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.modules.market.list.MarketListService
import io.vextabit.wallet.modules.market.list.MarketListViewModel

object MarketDiscoveryModule {

    class Factory : ViewModelProvider.Factory {
        val service by lazy { MarketDiscoveryService(io.vextabit.wallet.core.App.xRateManager, io.vextabit.wallet.core.App.backgroundManager) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>) = when (modelClass) {
            MarketDiscoveryViewModel::class.java -> {
                MarketDiscoveryViewModel(service, listOf(service)) as T
            }
            MarketListViewModel::class.java -> {
                val listService = MarketListService(service, io.vextabit.wallet.core.App.currencyManager)
                MarketListViewModel(listService, io.vextabit.wallet.core.App.connectivityManager, listOf(listService)) as T
            }
            else -> throw IllegalArgumentException()
        }
    }
}
