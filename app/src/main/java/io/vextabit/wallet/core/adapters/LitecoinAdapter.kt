package io.vextabit.wallet.core.adapters

import io.vextabit.wallet.core.AdapterErrorWrongParameters
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.ISendBitcoinAdapter
import io.vextabit.wallet.core.UnsupportedAccountException
import io.vextabit.wallet.entities.AccountType
import io.vextabit.wallet.entities.SyncMode
import io.vextabit.wallet.entities.transactionrecords.TransactionRecord
import io.vextabit.wallet.entities.Wallet
import io.horizontalsystems.bitcoincore.BitcoinCore
import io.horizontalsystems.bitcoincore.models.BalanceInfo
import io.horizontalsystems.bitcoincore.models.BlockInfo
import io.horizontalsystems.bitcoincore.models.TransactionInfo
import io.vextabit.coinkit.models.Coin
import io.horizontalsystems.core.BackgroundManager
import io.horizontalsystems.litecoinkit.LitecoinKit
import io.horizontalsystems.litecoinkit.LitecoinKit.NetworkType
import java.math.BigDecimal

class LitecoinAdapter(
        override val kit: LitecoinKit,
        syncMode: SyncMode?,
        backgroundManager: BackgroundManager,
        coin: Coin
) : BitcoinBaseAdapter(kit, syncMode, backgroundManager, coin), LitecoinKit.Listener, ISendBitcoinAdapter {

    constructor(wallet: Wallet, syncMode: SyncMode?, testMode: Boolean, backgroundManager: BackgroundManager) : this(createKit(wallet, syncMode, testMode), syncMode, backgroundManager, wallet.coin)

    init {
        kit.listener = this
    }

    //
    // BitcoinBaseAdapter
    //

    override val satoshisInBitcoin: BigDecimal = BigDecimal.valueOf(Math.pow(10.0, decimal.toDouble()))

    //
    // LitecoinKit Listener
    //

    override fun onBalanceUpdate(balance: BalanceInfo) {
        balanceUpdatedSubject.onNext(Unit)
    }

    override fun onLastBlockInfoUpdate(blockInfo: BlockInfo) {
        lastBlockUpdatedSubject.onNext(Unit)
    }

    override fun onKitStateUpdate(state: BitcoinCore.KitState) {
        setState(state)
    }

    override fun onTransactionsUpdate(inserted: List<TransactionInfo>, updated: List<TransactionInfo>) {
        val records = mutableListOf<TransactionRecord>()

        for (info in inserted) {
            records.add(transactionRecord(info))
        }

        for (info in updated) {
            records.add(transactionRecord(info))
        }

        transactionRecordsSubject.onNext(records)
    }

    override fun onTransactionsDelete(hashes: List<String>) {
        // ignored for now
    }

    companion object {

        private fun getNetworkType(testMode: Boolean) =
                if (testMode) NetworkType.TestNet else NetworkType.MainNet

        private fun createKit(wallet: Wallet, syncMode: SyncMode?, testMode: Boolean): LitecoinKit {
            val account = wallet.account
            val accountType = (account.type as? AccountType.Mnemonic) ?: throw UnsupportedAccountException()
            val derivation = wallet.configuredCoin.settings.derivation ?: throw AdapterErrorWrongParameters("Derivation not set")

            return LitecoinKit(context = io.vextabit.wallet.core.App.instance,
                    words = accountType.words,
                    passphrase = accountType.passphrase,
                    walletId = account.id,
                    syncMode = getSyncMode(syncMode),
                    networkType = getNetworkType(testMode),
                    confirmationsThreshold = confirmationsThreshold,
                    bip = getBip(derivation))
        }

        fun clear(walletId: String, testMode: Boolean) {
            LitecoinKit.clear(io.vextabit.wallet.core.App.instance, getNetworkType(testMode), walletId)
        }
    }
}
