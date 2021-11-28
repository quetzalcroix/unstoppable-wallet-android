package io.vextabit.wallet.modules.coin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.horizontalsystems.coinkit.models.CoinType

object CoinModule {

    class Factory(private val coinTitle: String, private val coinType: CoinType, private val coinCode: String) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val currency = io.vextabit.wallet.core.App.currencyManager.baseCurrency
            val service = CoinService(
                    coinType,
                    currency,
                    io.vextabit.wallet.core.App.xRateManager,
                    io.vextabit.wallet.core.App.chartTypeStorage,
                    io.vextabit.wallet.core.App.priceAlertManager,
                    io.vextabit.wallet.core.App.notificationManager,
                    io.vextabit.wallet.core.App.marketFavoritesManager,
                    io.vextabit.wallet.core.App.appConfigProvider.guidesUrl
            )
            return CoinViewModel(service, coinCode, coinTitle, CoinViewFactory(currency, io.vextabit.wallet.core.App.numberFormatter), listOf(service)) as T
        }

    }
}
