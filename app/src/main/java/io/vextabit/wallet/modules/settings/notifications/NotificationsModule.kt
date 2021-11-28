package io.vextabit.wallet.modules.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App

object NotificationsModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val viewModel = NotificationsViewModel(io.vextabit.wallet.core.App.priceAlertManager, io.vextabit.wallet.core.App.walletManager, io.vextabit.wallet.core.App.notificationManager, io.vextabit.wallet.core.App.localStorage)
            return viewModel as T
        }
    }
}
