package io.vextabit.wallet.core.managers

import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.core.IBackupManager
import io.reactivex.Flowable

class BackupManager(private val accountManager: IAccountManager) : IBackupManager {

    override val allBackedUp: Boolean
        get() = accountManager.accounts.all { it.isBackedUp }

    override val allBackedUpFlowable: Flowable<Boolean>
        get() = accountManager.accountsFlowable.map { accounts ->
            accounts.all { it.isBackedUp }
        }

    override fun setIsBackedUp(id: String) {
        accountManager.accounts.find { it.id == id }?.let { account ->
            account.isBackedUp = true
            accountManager.update(account)
        }
    }
}
