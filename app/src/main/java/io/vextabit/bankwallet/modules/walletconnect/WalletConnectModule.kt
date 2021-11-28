package io.vextabit.bankwallet.modules.walletconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App

object WalletConnectModule {

    class Factory(private val remotePeerId: String?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = WalletConnectService(remotePeerId, io.vextabit.bankwallet.core.App.walletConnectManager, io.vextabit.bankwallet.core.App.walletConnectSessionManager, io.vextabit.bankwallet.core.App.walletConnectRequestManager, io.vextabit.bankwallet.core.App.connectivityManager)

            return WalletConnectViewModel(service, listOf(service)) as T
        }
    }

}
