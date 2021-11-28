package io.vextabit.wallet.modules.settings.security

import io.vextabit.wallet.core.App

object SecuritySettingsModule {

    interface ISecuritySettingsView {
        fun togglePinSet(pinSet: Boolean)
        fun setEditPinVisible(visible: Boolean)
        fun setBiometricSettingsVisible(visible: Boolean)
        fun toggleBiometricEnabled(enabled: Boolean)
    }

    interface ISecuritySettingsViewDelegate {
        fun viewDidLoad()
        fun didTapEditPin()
        fun didSwitchPinSet(enable: Boolean)
        fun didSwitchBiometricEnabled(enable: Boolean)
        fun didSetPin()
        fun didCancelSetPin()
        fun didUnlockPinToDisablePin()
        fun didCancelUnlockPinToDisablePin()
        fun didTapPrivacy()
    }

    interface ISecuritySettingsInteractor {
        val isBiometricAuthSupported: Boolean
        val isPinSet: Boolean
        var isBiometricAuthEnabled: Boolean

        fun disablePin()
    }

    interface ISecuritySettingsRouter {
        fun showEditPin()
        fun showSetPin()
        fun showUnlockPin()
        fun openPrivacySettings()
    }

    fun init(view: SecuritySettingsViewModel, router: ISecuritySettingsRouter) {
        val interactor = SecuritySettingsInteractor(io.vextabit.wallet.core.App.systemInfoManager, io.vextabit.wallet.core.App.pinComponent)
        val presenter = SecuritySettingsPresenter(router, interactor)

        view.delegate = presenter
        presenter.view = view
    }
}
