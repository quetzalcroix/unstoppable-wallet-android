package io.horizontalsystems.bankwallet.modules.swap.tradeoptions.oneinch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.modules.swap.oneinch.OneInchTradeService
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.AddressResolutionService
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.RecipientAddressViewModel
import io.horizontalsystems.coinkit.models.CoinType

object OneInchSwapSettingsModule {

    class Factory(private val tradeService: OneInchTradeService) : ViewModelProvider.Factory {

        private val service by lazy { OneInchSettingsService(tradeService.tradeOptions) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val ethereumCoin = App.coinManager.getCoin(CoinType.Ethereum)
                    ?: throw IllegalArgumentException()

            return when (modelClass) {
                OneInchSettingsViewModel::class.java -> OneInchSettingsViewModel(service, tradeService) as T
                RecipientAddressViewModel::class.java -> {
                    val addressParser = App.addressParserFactory.parser(ethereumCoin)
                    val resolutionService = AddressResolutionService(ethereumCoin.code, true)
                    val placeholder = Translator.getString(R.string.SwapSettings_RecipientPlaceholder)
                    RecipientAddressViewModel(service, resolutionService, addressParser, placeholder, listOf(service, resolutionService)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
