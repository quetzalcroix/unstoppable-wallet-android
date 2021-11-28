package io.vextabit.wallet.modules.launcher

import android.content.Context
import android.content.Intent
import io.vextabit.wallet.core.App
import io.horizontalsystems.core.security.KeyStoreValidationResult

object LaunchModule {

    interface IView

    interface IViewDelegate {
        fun viewDidLoad()
        fun didUnlock()
        fun didCancelUnlock()
    }

    interface IInteractor {
        val isPinNotSet: Boolean
        val isAccountsEmpty: Boolean
        val isSystemLockOff: Boolean
        val mainShowedOnce: Boolean

        fun validateKeyStore(): KeyStoreValidationResult
    }

    interface IInteractorDelegate

    interface IRouter {
        fun openWelcomeModule()
        fun openMainModule()
        fun openUnlockModule()
        fun closeApplication()
        fun openNoSystemLockModule()
        fun openKeyInvalidatedModule()
        fun openUserAuthenticationModule()
    }

    fun init(view: LaunchViewModel, router: IRouter) {
        val interactor = LaunchInteractor(io.vextabit.wallet.core.App.accountManager, io.vextabit.wallet.core.App.pinComponent, io.vextabit.wallet.core.App.systemInfoManager, io.vextabit.wallet.core.App.keyStoreManager, io.vextabit.wallet.core.App.localStorage)
        val presenter = LaunchPresenter(interactor, router)

        view.delegate = presenter
        presenter.view = view
        interactor.delegate = presenter
    }

    fun start(context: Context) {
        val intent = Intent(context, LauncherActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

}
