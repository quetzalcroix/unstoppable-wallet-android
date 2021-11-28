package io.vextabit.wallet.modules.balanceonboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App

object BalanceOnboardingModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BalanceOnboardingViewModel(io.vextabit.wallet.core.App.accountManager, io.vextabit.wallet.core.App.walletManager) as T
        }
    }
}
