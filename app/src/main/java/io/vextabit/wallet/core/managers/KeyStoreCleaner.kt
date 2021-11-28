package io.vextabit.wallet.core.managers

import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.core.ILocalStorage
import io.vextabit.wallet.core.IWalletManager
import io.horizontalsystems.core.IKeyStoreCleaner

class KeyStoreCleaner(
        private val localStorage: ILocalStorage,
        private val accountManager: IAccountManager,
        private val walletManager: IWalletManager)
    : IKeyStoreCleaner {

    override var encryptedSampleText: String?
        get() = localStorage.encryptedSampleText
        set(value) {
            localStorage.encryptedSampleText = value
        }

    override fun cleanApp() {
        accountManager.clear()
        walletManager.enable(listOf())
        localStorage.clear()
    }
}
