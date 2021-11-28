package io.vextabit.wallet.modules.swap.info

import androidx.lifecycle.ViewModel
import io.vextabit.wallet.R
import io.vextabit.wallet.core.providers.Translator
import io.vextabit.wallet.modules.swap.SwapMainModule

class SwapInfoViewModel(
        dex: SwapMainModule.Dex
) : ViewModel() {

    private val dexName = dex.provider.title

    private val blockchain = dex.blockchain.title

    val title = dex.provider.title

    val dexUrl = dex.provider.url

    val description = Translator.getString(R.string.SwapInfo_Description, dexName, blockchain, dexName)

    val dexRelated = Translator.getString(R.string.SwapInfo_DexRelated, dexName)

    val transactionFeeDescription = Translator.getString(R.string.SwapInfo_TransactionFeeDescription, blockchain, dexName)

    val linkText = Translator.getString(R.string.SwapInfo_Site, dexName)

}
