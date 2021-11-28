package io.vextabit.bankwallet.modules.coin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.coinkit.models.CoinType

object CoinModule {

    class Factory(private val coinTitle: String, private val coinType: CoinType, private val coinCode: String) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val currency = io.vextabit.bankwallet.core.App.currencyManager.baseCurrency
            val service = CoinService(
                    coinType,
                    currency,
                    io.vextabit.bankwallet.core.App.xRateManager,
                    io.vextabit.bankwallet.core.App.chartTypeStorage,
                    io.vextabit.bankwallet.core.App.priceAlertManager,
                    io.vextabit.bankwallet.core.App.notificationManager,
                    io.vextabit.bankwallet.core.App.marketFavoritesManager,
                    io.vextabit.bankwallet.core.App.appConfigProvider.guidesUrl
            )
            return CoinViewModel(service, coinCode, coinTitle, CoinViewFactory(currency, io.vextabit.bankwallet.core.App.numberFormatter), listOf(service)) as T
        }

    }
}
