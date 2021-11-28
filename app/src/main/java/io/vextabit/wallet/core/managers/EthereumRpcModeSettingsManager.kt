package io.vextabit.wallet.core.managers

import io.vextabit.wallet.core.IAdapterManager
import io.vextabit.wallet.core.IBlockchainSettingsStorage
import io.vextabit.wallet.core.IEthereumRpcModeSettingsManager
import io.vextabit.wallet.core.IWalletManager
import io.vextabit.wallet.entities.CommunicationMode
import io.vextabit.wallet.entities.EthereumRpcMode
import io.vextabit.coinkit.models.CoinType

class EthereumRpcModeSettingsManager(
        private val blockchainSettingsStorage: IBlockchainSettingsStorage,
        private val adapterManager: IAdapterManager,
        private val walletManager: IWalletManager
) : IEthereumRpcModeSettingsManager {

    private val coinType = CoinType.Ethereum

    override val communicationModes: List<CommunicationMode>
        get() = listOf(CommunicationMode.Infura)

    override fun rpcMode(): EthereumRpcMode {
        val storedRpcMode = blockchainSettingsStorage.ethereumRpcModeSetting(coinType)
        return storedRpcMode ?: EthereumRpcMode(coinType, CommunicationMode.Infura)
    }

    override fun save(setting: EthereumRpcMode) {
        blockchainSettingsStorage.saveEthereumRpcModeSetting(setting)

        val walletsForUpdate = walletManager.wallets.filter {
            it.coin.type == CoinType.Ethereum || it.coin.type is CoinType.Erc20
        }

        if (walletsForUpdate.isNotEmpty()) {
            adapterManager.refreshAdapters(walletsForUpdate)
        }
    }

}
