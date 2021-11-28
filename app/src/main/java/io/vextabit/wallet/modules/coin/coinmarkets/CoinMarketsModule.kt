package io.vextabit.wallet.modules.coin.coinmarkets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.horizontalsystems.coinkit.models.CoinType

object CoinMarketsModule {
    class Factory(private val coinCode: String, private val coinType: CoinType) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CoinMarketsViewModel(coinCode, coinType, io.vextabit.wallet.core.App.currencyManager, io.vextabit.wallet.core.App.xRateManager, io.vextabit.wallet.core.App.numberFormatter) as T
        }
    }
}
