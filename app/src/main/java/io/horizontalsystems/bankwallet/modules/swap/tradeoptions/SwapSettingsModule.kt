package io.horizontalsystems.bankwallet.modules.swap.tradeoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.uniswap.UniswapSettingsService
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.uniswap.UniswapSettingsViewModel
import io.horizontalsystems.bankwallet.modules.swap.uniswap.UniswapTradeService
import io.horizontalsystems.coinkit.models.CoinType

object SwapSettingsModule {

    class Factory(private val tradeService: UniswapTradeService) : ViewModelProvider.Factory {

        private val service by lazy { UniswapSettingsService(tradeService.tradeOptions) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val ethereumCoin = App.coinManager.getCoin(CoinType.Ethereum)
                    ?: throw IllegalArgumentException()

            return when (modelClass) {
                UniswapSettingsViewModel::class.java -> UniswapSettingsViewModel(service, tradeService) as T
                SwapDeadlineViewModel::class.java -> SwapDeadlineViewModel(service) as T
                SwapSlippageViewModel::class.java -> SwapSlippageViewModel(service) as T
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
