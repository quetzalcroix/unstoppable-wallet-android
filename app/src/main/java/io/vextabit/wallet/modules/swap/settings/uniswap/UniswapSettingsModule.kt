package io.vextabit.wallet.modules.swap.settings.uniswap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.R
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.providers.Translator
import io.vextabit.wallet.modules.swap.SwapMainModule
import io.vextabit.wallet.modules.swap.settings.AddressResolutionService
import io.vextabit.wallet.modules.swap.settings.RecipientAddressViewModel
import io.vextabit.wallet.modules.swap.settings.SwapDeadlineViewModel
import io.vextabit.wallet.modules.swap.settings.SwapSlippageViewModel
import io.vextabit.wallet.modules.swap.uniswap.UniswapTradeService

object UniswapSettingsModule {

    sealed class State {
        class Valid(val tradeOptions: SwapTradeOptions) : State()
        object Invalid : State()
    }

    class Factory(
            private val tradeService: UniswapTradeService,
            private val blockchain: SwapMainModule.Blockchain
    ) : ViewModelProvider.Factory {

        private val service by lazy { UniswapSettingsService(tradeService.tradeOptions) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val evmCoin = blockchain.coin ?: throw IllegalArgumentException()

            return when (modelClass) {
                UniswapSettingsViewModel::class.java -> UniswapSettingsViewModel(service, tradeService) as T
                SwapDeadlineViewModel::class.java -> SwapDeadlineViewModel(service) as T
                SwapSlippageViewModel::class.java -> SwapSlippageViewModel(service) as T
                RecipientAddressViewModel::class.java -> {
                    val addressParser = io.vextabit.wallet.core.App.addressParserFactory.parser(evmCoin)
                    val resolutionService = AddressResolutionService(evmCoin.code, true)
                    val placeholder = Translator.getString(R.string.SwapSettings_RecipientPlaceholder)
                    RecipientAddressViewModel(service, resolutionService, addressParser, placeholder, listOf(service, resolutionService)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
