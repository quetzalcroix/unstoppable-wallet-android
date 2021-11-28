package io.vextabit.wallet.modules.tor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.managers.TorStatus

object TorStatusModule {

    interface View {
        fun updateConnectionStatus(connectionStatus: TorStatus)
    }

    interface ViewDelegate {
        fun viewDidLoad()
        fun restartTor()
        fun disableTor()
    }

    interface Interactor {
        fun subscribeToEvents()
        fun clear()
        fun restartTor()
        fun disableTor()
    }

    interface InteractorDelegate {
        fun updateConnectionStatus(connectionStatus: TorStatus)
        fun didStopTor()
    }

    interface Router{
        fun closeView()
        fun restartApp()
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            val view = TorStatusView()
            val router = TorStatusRouter()
            val interactor = TorStatusInteractor(io.vextabit.wallet.core.App.torKitManager)
            val presenter = TorStatusPresenter(view, router, interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}