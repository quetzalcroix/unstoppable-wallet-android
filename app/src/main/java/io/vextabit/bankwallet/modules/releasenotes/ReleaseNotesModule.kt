package io.vextabit.bankwallet.modules.releasenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App

object ReleaseNotesModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReleaseNotesViewModel(io.vextabit.bankwallet.core.App.appConfigProvider, io.vextabit.bankwallet.core.App.releaseNotesManager) as T
        }
    }
}
