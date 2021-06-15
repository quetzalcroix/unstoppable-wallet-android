package io.horizontalsystems.bankwallet.modules.swap.info

import androidx.lifecycle.ViewModel
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule

class SwapInfoViewModel(
        dex: SwapMainModule.Dex
) : ViewModel() {

    private val dexName = when (dex.provider) {
        SwapMainModule.Dex.Provider.Uniswap -> "Uniswap"
        SwapMainModule.Dex.Provider.PancakeSwap -> "PancakeSwap"
        SwapMainModule.Dex.Provider.OneInch -> "1inch Network"
    }

    private val blockchain = when (dex.blockchain) {
        SwapMainModule.Dex.Blockchain.Ethereum -> "Ethereum"
        SwapMainModule.Dex.Blockchain.BinanceSmartChain -> "Binance Smart Chain"
    }

    val dexUrl = when (dex.provider) {
        SwapMainModule.Dex.Provider.Uniswap -> "https://uniswap.org/"
        SwapMainModule.Dex.Provider.PancakeSwap -> "https://pancakeswap.finance/"
        SwapMainModule.Dex.Provider.OneInch -> "https://app.1inch.io/"
    }

    val title: String = when (dex.provider) {
        SwapMainModule.Dex.Provider.Uniswap -> "Uniswap"
        SwapMainModule.Dex.Provider.PancakeSwap -> "PancakeSwap"
        SwapMainModule.Dex.Provider.OneInch -> "1inch Network"
    }

    val description = Translator.getString(R.string.SwapInfo_Description, dexName, blockchain, dexName)

    val dexRelated = Translator.getString(R.string.SwapInfo_DexRelated, dexName)

    val transactionFeeDescription = Translator.getString(R.string.SwapInfo_TransactionFeeDescription, blockchain, dexName)

    val linkText = Translator.getString(R.string.SwapInfo_Site, dexName)

}
