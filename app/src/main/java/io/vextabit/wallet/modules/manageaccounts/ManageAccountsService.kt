package io.vextabit.wallet.modules.manageaccounts

import io.vextabit.wallet.core.Clearable
import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.core.subscribeIO
import io.vextabit.wallet.entities.Account
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class ManageAccountsService(
        private val accountManager: IAccountManager
) : Clearable {
    private val disposable = CompositeDisposable()

    private val itemsSubject = PublishSubject.create<List<Item>>()
    var items: List<Item> = listOf()
        private set(value) {
            field = value
            itemsSubject.onNext(value)
        }
    val itemsObservable: Flowable<List<Item>> = itemsSubject.toFlowable(BackpressureStrategy.BUFFER)

    init {
        accountManager.accountsFlowable
                .subscribeIO { syncItems() }
                .let { disposable.add(it) }

        accountManager.activeAccountObservable
                .subscribeIO { syncItems() }
                .let { disposable.add(it) }

        syncItems()
    }

    private fun syncItems() {
        val activeAccount = accountManager.activeAccount
        items = accountManager.accounts.map { account ->
            Item(account, account == activeAccount)
        }
    }

    fun setActiveAccountId(activeAccountId: String) {
        accountManager.setActiveAccountId(activeAccountId)
    }

    override fun clear() {
        disposable.clear()
    }

    data class Item(
            val account: Account,
            val isActive: Boolean
    )

}
