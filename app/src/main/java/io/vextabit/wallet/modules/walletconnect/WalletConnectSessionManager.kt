package io.vextabit.wallet.modules.walletconnect

import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.core.managers.AccountSettingManager
import io.vextabit.wallet.core.storage.WalletConnectSessionStorage
import io.vextabit.wallet.core.subscribeIO
import io.vextabit.wallet.entities.Account
import io.vextabit.wallet.entities.WalletConnectSession
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class WalletConnectSessionManager(
    private val storage: WalletConnectSessionStorage,
    private val accountManager: IAccountManager,
    private val accountSettingManager: AccountSettingManager
) {
    private val disposable = CompositeDisposable()

    private val sessionsSubject = PublishSubject.create<List<WalletConnectSession>>()
    val sessionsObservable: Flowable<List<WalletConnectSession>>
        get() = sessionsSubject.toFlowable(BackpressureStrategy.BUFFER)

    val sessions: List<WalletConnectSession>
        get() = accountManager.activeAccount?.let { account ->
            val ethereumChainId = accountSettingManager.ethereumNetwork(account).networkType.chainId
            val binanceSmartChainChainId =
                accountSettingManager.binanceSmartChainNetwork(account).networkType.chainId

            storage.getSessions(account.id, listOf(ethereumChainId, binanceSmartChainChainId))
        } ?: listOf()

    init {
        accountManager.accountsDeletedFlowable
                .subscribeIO {
                    handleDeletedAccounts()
                }
                .let {
                    disposable.add(it)
                }

        accountManager.activeAccountObservable
                .subscribeIO {
                    handleActiveAccount(it.orElse(null))
                }
                .let {
                    disposable.add(it)
                }

        accountSettingManager.ethereumNetworkObservable
            .subscribeIO {
                syncSessions()
            }
            .let {
                disposable.add(it)
            }

        accountSettingManager.binanceSmartChainNetworkObservable
            .subscribeIO {
                syncSessions()
            }
            .let {
                disposable.add(it)
            }
    }

    private fun handleActiveAccount(account: Account?) {
        syncSessions()
    }

    fun save(session: WalletConnectSession) {
        storage.save(session)
        syncSessions()
    }

    fun deleteSession(peerId: String) {
        storage.deleteSessionsByPeerId(peerId)
        syncSessions()
    }

    private fun handleDeletedAccounts() {
        val existingAccountIds = accountManager.accounts.map { it.id }
        storage.deleteSessionsExcept(accountIds = existingAccountIds)

        syncSessions()
    }

    private fun syncSessions() {
        sessionsSubject.onNext(sessions)
    }

}
