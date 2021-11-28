package io.vextabit.wallet.modules.releasenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App

object ReleaseNotesModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReleaseNotesViewModel(io.vextabit.wallet.core.App.appConfigProvider, io.vextabit.wallet.core.App.releaseNotesManager) as T
        }
    }
}
