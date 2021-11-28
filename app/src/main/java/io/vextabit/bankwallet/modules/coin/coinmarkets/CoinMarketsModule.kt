package io.vextabit.bankwallet.modules.coin.coinmarkets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.coinkit.models.CoinType

object CoinMarketsModule {
    class Factory(private val coinCode: String, private val coinType: CoinType) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CoinMarketsViewModel(coinCode, coinType, io.vextabit.bankwallet.core.App.currencyManager, io.vextabit.bankwallet.core.App.xRateManager, io.vextabit.bankwallet.core.App.numberFormatter) as T
        }
    }
}
