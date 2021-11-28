package io.vextabit.wallet.modules.launcher

import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.core.ILocalStorage
import io.horizontalsystems.core.IKeyStoreManager
import io.horizontalsystems.core.IPinComponent
import io.horizontalsystems.core.ISystemInfoManager
import io.horizontalsystems.core.security.KeyStoreValidationResult

class LaunchInteractor(
        private val accountManager: IAccountManager,
        private val pinComponent: IPinComponent,
        private val systemInfoManager: ISystemInfoManager,
        private val keyStoreManager: IKeyStoreManager,
        localStorage: ILocalStorage)
    : LaunchModule.IInteractor {

    var delegate: LaunchModule.IInteractorDelegate? = null

    override val isPinNotSet: Boolean
        get() = !pinComponent.isPinSet

    override val isAccountsEmpty: Boolean
        get() = accountManager.isAccountsEmpty

    override val isSystemLockOff: Boolean
        get() = systemInfoManager.isSystemLockOff

    override fun validateKeyStore(): KeyStoreValidationResult {
        return keyStoreManager.validateKeyStore()
    }

    override val mainShowedOnce: Boolean = localStorage.mainShowedOnce
}
