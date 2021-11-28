package io.vextabit.wallet.modules.receive

import androidx.lifecycle.ViewModel
import io.vextabit.wallet.core.IAdapterManager
import io.vextabit.wallet.entities.Wallet
import io.vextabit.wallet.entities.addressType
import io.vextabit.wallet.modules.balance.NetworkTypeChecker

class ReceiveViewModel(
        wallet: Wallet,
        adapterManager: IAdapterManager,
        networkTypeChecker: NetworkTypeChecker) : ViewModel() {

    val receiveAddress: String
    val addressType: String?
    val testNet: Boolean

    init {
        val receiveAdapter = adapterManager.getReceiveAdapterForWallet(wallet) ?: throw NoReceiverAdapter()

        testNet = !networkTypeChecker.isMainNet(wallet)
        receiveAddress = receiveAdapter.receiveAddress
        addressType = wallet.configuredCoin.settings.derivation?.addressType()
    }

    class NoReceiverAdapter : Error("No Receiver Adapter")

}
