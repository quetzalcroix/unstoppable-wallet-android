package io.vextabit.wallet.modules.receive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.entities.Wallet
import io.vextabit.wallet.modules.balance.NetworkTypeChecker

object ReceiveModule {

    class Factory(private val wallet: Wallet) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReceiveViewModel(wallet, io.vextabit.wallet.core.App.adapterManager, NetworkTypeChecker(io.vextabit.wallet.core.App.accountSettingManager)) as T
        }
    }

}
