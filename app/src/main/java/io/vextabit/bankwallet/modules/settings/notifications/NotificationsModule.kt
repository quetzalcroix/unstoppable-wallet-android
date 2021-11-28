package io.vextabit.bankwallet.modules.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App

object NotificationsModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val viewModel = NotificationsViewModel(io.vextabit.bankwallet.core.App.priceAlertManager, io.vextabit.bankwallet.core.App.walletManager, io.vextabit.bankwallet.core.App.notificationManager, io.vextabit.bankwallet.core.App.localStorage)
            return viewModel as T
        }
    }
}
