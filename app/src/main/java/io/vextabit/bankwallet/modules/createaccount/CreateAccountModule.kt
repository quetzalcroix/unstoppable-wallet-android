package io.vextabit.bankwallet.modules.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.R
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.managers.PassphraseValidator
import io.horizontalsystems.bankwallet.core.providers.Translator

object CreateAccountModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = CreateAccountService(
                    io.vextabit.bankwallet.core.App.accountFactory,
                    io.vextabit.bankwallet.core.App.wordsManager,
                    io.vextabit.bankwallet.core.App.accountManager,
                    io.vextabit.bankwallet.core.App.walletManager,
                    PassphraseValidator(),
                    io.vextabit.bankwallet.core.App.coinKit
            )

            return CreateAccountViewModel(service, listOf(service)) as T
        }
    }

    enum class Kind(val title: String) {
        Mnemonic12(Translator.getString(R.string.CreateWallet_N_Words, 12)),
        Mnemonic24(Translator.getString(R.string.CreateWallet_N_Words, 24)),
    }
}
