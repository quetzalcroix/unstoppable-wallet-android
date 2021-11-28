package io.vextabit.wallet.modules.tor

import androidx.lifecycle.ViewModel
import io.vextabit.wallet.core.managers.TorStatus

class TorStatusPresenter(
        val view: TorStatusView,
        val router: TorStatusRouter,
        private val interactor: TorStatusInteractor) : ViewModel(), TorStatusModule.ViewDelegate, TorStatusModule.InteractorDelegate {

    override fun viewDidLoad() {
        interactor.subscribeToEvents()
    }

    override fun updateConnectionStatus(connectionStatus: TorStatus) {
        if (connectionStatus == TorStatus.Connected){
            router.closeView()
        } else {
            view.updateConnectionStatus(connectionStatus)
        }
    }

    override fun restartTor() {
        interactor.restartTor()
    }

    override fun disableTor() {
        interactor.disableTor()
    }

    override fun didStopTor() {
        router.restartApp()
    }

    override fun onCleared() {
        super.onCleared()
        interactor.clear()
    }
}
