package io.vextabit.wallet.modules.restoreprivatekey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.managers.PassphraseValidator
import io.vextabit.wallet.modules.restoremnemonic.RestoreMnemonicService
import io.vextabit.wallet.modules.restoremnemonic.RestoreMnemonicViewModel

object RestorePrivateKeyModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = RestoreMnemonicService(App.wordsManager, PassphraseValidator())

            return RestoreMnemonicViewModel(service, listOf(service)) as T
        }
    }

}
