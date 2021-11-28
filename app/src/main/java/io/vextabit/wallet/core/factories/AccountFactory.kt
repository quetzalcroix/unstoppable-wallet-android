package io.vextabit.wallet.core.factories

import io.vextabit.wallet.core.IAccountFactory
import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.entities.Account
import io.vextabit.wallet.entities.AccountOrigin
import io.vextabit.wallet.entities.AccountType
import java.util.*

class AccountFactory(val accountManager: IAccountManager) : IAccountFactory {

    override fun account(type: AccountType, origin: AccountOrigin, backedUp: Boolean): Account {
        val id = UUID.randomUUID().toString()

        return Account(
                id = id,
                name = getNextWalletName(),
                type = type,
                origin = origin,
                isBackedUp = backedUp
        )
    }


    private fun getNextWalletName(): String {
        val count = accountManager.accounts.count()

        return "Wallet ${count + 1}"
    }
}
