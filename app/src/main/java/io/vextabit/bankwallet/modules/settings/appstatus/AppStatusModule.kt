package io.vextabit.bankwallet.modules.settings.appstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.ui.helpers.TextHelper

object AppStatusModule {

    interface IView {
        fun setAppStatus(status: Map<String, Any>)
        fun showCopied()
    }

    interface IViewDelegate {
        fun viewDidLoad()
        fun didTapCopy(text: String)
    }

    interface IInteractor {
        val status: Map<String, Any>

        fun copyToClipboard(text: String)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val view = AppStatusView()
            val appStatusService = AppStatusService(
                    io.vextabit.bankwallet.core.App.systemInfoManager,
                    io.vextabit.bankwallet.core.App.localStorage,
                    io.vextabit.bankwallet.core.App.accountManager,
                    io.vextabit.bankwallet.core.App.walletManager,
                    io.vextabit.bankwallet.core.App.adapterManager,
                    io.vextabit.bankwallet.core.App.ethereumKitManager,
                    io.vextabit.bankwallet.core.App.binanceSmartChainKitManager,
                    io.vextabit.bankwallet.core.App.binanceKitManager
            )
            val interactor = AppStatusInteractor(appStatusService, TextHelper)
            val presenter = AppStatusPresenter(view, interactor)

            return presenter as T
        }
    }
}
