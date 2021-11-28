package io.vextabit.wallet.modules.walletconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App

object WalletConnectModule {

    class Factory(private val remotePeerId: String?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = WalletConnectService(remotePeerId, io.vextabit.wallet.core.App.walletConnectManager, io.vextabit.wallet.core.App.walletConnectSessionManager, io.vextabit.wallet.core.App.walletConnectRequestManager, io.vextabit.wallet.core.App.connectivityManager)

            return WalletConnectViewModel(service, listOf(service)) as T
        }
    }

}
