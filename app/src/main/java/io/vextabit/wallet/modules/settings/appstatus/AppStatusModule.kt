package io.vextabit.wallet.modules.settings.appstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.ui.helpers.TextHelper

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
                    io.vextabit.wallet.core.App.systemInfoManager,
                    io.vextabit.wallet.core.App.localStorage,
                    io.vextabit.wallet.core.App.accountManager,
                    io.vextabit.wallet.core.App.walletManager,
                    io.vextabit.wallet.core.App.adapterManager,
                    io.vextabit.wallet.core.App.ethereumKitManager,
                    io.vextabit.wallet.core.App.binanceSmartChainKitManager,
                    io.vextabit.wallet.core.App.binanceKitManager
            )
            val interactor = AppStatusInteractor(appStatusService, TextHelper)
            val presenter = AppStatusPresenter(view, interactor)

            return presenter as T
        }
    }
}
