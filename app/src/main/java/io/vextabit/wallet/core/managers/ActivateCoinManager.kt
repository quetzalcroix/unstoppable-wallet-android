package io.vextabit.wallet.core.managers

import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.core.IWalletManager
import io.vextabit.wallet.entities.Wallet
import io.horizontalsystems.coinkit.CoinKit
import io.horizontalsystems.coinkit.models.CoinType

class ActivateCoinManager(
        private val coinKit: CoinKit,
        private val walletManager: IWalletManager,
        private val accountManager: IAccountManager
) {

    fun activate(coinType: CoinType) {
        val coin = coinKit.getCoin(coinType) ?: return // coin type is not supported

        if (walletManager.activeWallets.any { it.coin == coin })  return // wallet already exists

        val account = accountManager.activeAccount ?: return // active account does not exist

        val wallet = Wallet(coin, account)
        walletManager.save(listOf(wallet))
    }

}
