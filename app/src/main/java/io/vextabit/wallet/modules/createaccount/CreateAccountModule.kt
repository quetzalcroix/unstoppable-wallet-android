package io.vextabit.wallet.modules.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.R
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.managers.PassphraseValidator
import io.vextabit.wallet.core.providers.Translator

object CreateAccountModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = CreateAccountService(
                    io.vextabit.wallet.core.App.accountFactory,
                    io.vextabit.wallet.core.App.wordsManager,
                    io.vextabit.wallet.core.App.accountManager,
                    io.vextabit.wallet.core.App.walletManager,
                    PassphraseValidator(),
                    io.vextabit.wallet.core.App.coinKit
            )

            return CreateAccountViewModel(service, listOf(service)) as T
        }
    }

    enum class Kind(val title: String) {
        Mnemonic12(Translator.getString(R.string.CreateWallet_N_Words, 12)),
        Mnemonic24(Translator.getString(R.string.CreateWallet_N_Words, 24)),
    }
}
