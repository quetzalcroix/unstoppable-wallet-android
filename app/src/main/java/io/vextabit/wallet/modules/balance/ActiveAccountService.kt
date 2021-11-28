package io.vextabit.wallet.modules.balance

import io.vextabit.wallet.core.Clearable
import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.core.subscribeIO
import io.vextabit.wallet.entities.Account
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ActiveAccountService(private val accountManager: IAccountManager) : Clearable {
    private val disposables = CompositeDisposable()

    private val activeAccountSubject = BehaviorSubject.create<Account>()
    val activeAccountObservable: Flowable<Account> = activeAccountSubject.toFlowable(BackpressureStrategy.DROP)

    init {
        refreshActiveAccount()

        accountManager.activeAccountObservable
            .subscribeIO {
                refreshActiveAccount()
            }
            .let {
                disposables.add(it)
            }

        accountManager.accountsFlowable
            .subscribeIO {
                refreshActiveAccount()
            }
            .let {
                disposables.add(it)
            }

    }

    private fun refreshActiveAccount() {
        accountManager.activeAccount?.let {
            activeAccountSubject.onNext(it)
        }
    }

    override fun clear() {
        disposables.clear()
    }

}
