package io.vextabit.wallet.modules.balance

import io.vextabit.wallet.core.IWalletManager
import io.vextabit.wallet.core.managers.AccountSettingManager
import io.vextabit.wallet.entities.Wallet
import io.reactivex.Observable

class BalanceActiveWalletRepository(
    private val walletManager: IWalletManager,
    private val accountSettingManager: AccountSettingManager
) {

    val itemsObservable: Observable<List<Wallet>> =
        Observable
            .merge(
                Observable.just(Unit),
                walletManager.activeWalletsUpdatedObservable,
                accountSettingManager.ethereumNetworkObservable,
                accountSettingManager.binanceSmartChainNetworkObservable
            )
            .map {
                walletManager.activeWallets
            }

    fun disable(wallet: Wallet) {
        walletManager.delete(listOf(wallet))
    }

    fun enable(wallet: Wallet) {
        walletManager.save(listOf(wallet))
    }

}
