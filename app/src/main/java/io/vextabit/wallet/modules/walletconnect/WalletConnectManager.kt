package io.vextabit.wallet.modules.walletconnect

import io.vextabit.wallet.core.IAccountManager
import io.vextabit.wallet.core.managers.EvmKitManager
import io.vextabit.wallet.entities.Account
import io.horizontalsystems.ethereumkit.core.EthereumKit

class WalletConnectManager(
        private val accountManager: IAccountManager,
        private val ethereumKitManager: EvmKitManager,
        private val binanceSmartChainKitManager: EvmKitManager
) {

    val activeAccount: Account?
        get() = accountManager.activeAccount

    fun evmKit(chainId: Int, account: Account): EthereumKit? {
        val ethKit = ethereumKitManager.evmKit(account)
        if (ethKit.networkType.chainId == chainId) {
            return ethKit
        } else {
            ethereumKitManager.unlink(account)
        }
        
        val bscKit = binanceSmartChainKitManager.evmKit(account)
        if (bscKit.networkType.chainId == chainId) {
            return bscKit
        } else {
            binanceSmartChainKitManager.unlink(account)
        }
        
        return null
    }

}
