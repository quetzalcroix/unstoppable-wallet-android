package io.vextabit.bankwallet.modules.settings.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.ui.helpers.TextHelper

object AboutModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AboutViewModel(io.vextabit.bankwallet.core.App.appConfigProvider, TextHelper, io.vextabit.bankwallet.core.App.termsManager, io.vextabit.bankwallet.core.App.systemInfoManager) as T
        }
    }
}
