package io.vextabit.wallet.modules.market.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.modules.market.posts.MarketPostService

object MarketOverviewModule {

    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = MarketOverviewService(io.vextabit.wallet.core.App.xRateManager, io.vextabit.wallet.core.App.backgroundManager, io.vextabit.wallet.core.App.currencyManager)
            val postsService = MarketPostService(io.vextabit.wallet.core.App.xRateManager, io.vextabit.wallet.core.App.backgroundManager)
            return MarketOverviewViewModel(service, postsService, listOf(service)) as T
        }

    }

    data class PostViewItem(
            val timeAgo: String,
            val imageUrl: String?,
            val source: String,
            val title: String,
            val url: String,
            val body: String
    )

}
