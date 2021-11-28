package io.vextabit.wallet.modules.settings.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.ui.helpers.TextHelper

object AboutModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AboutViewModel(io.vextabit.wallet.core.App.appConfigProvider, TextHelper, io.vextabit.wallet.core.App.termsManager, io.vextabit.wallet.core.App.systemInfoManager) as T
        }
    }
}
