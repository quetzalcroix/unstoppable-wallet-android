package io.vextabit.wallet.modules.restoremnemonic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.managers.PassphraseValidator

object RestoreMnemonicModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = RestoreMnemonicService(io.vextabit.wallet.core.App.wordsManager, PassphraseValidator())

            return RestoreMnemonicViewModel(service, listOf(service)) as T
        }
    }

}
