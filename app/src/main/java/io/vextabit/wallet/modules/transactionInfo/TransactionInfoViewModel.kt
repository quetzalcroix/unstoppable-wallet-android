package io.vextabit.wallet.modules.transactionInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.vextabit.wallet.core.Clearable
import io.vextabit.wallet.core.subscribeIO
import io.vextabit.wallet.entities.Account
import io.vextabit.wallet.entities.CurrencyValue
import io.vextabit.wallet.entities.transactionrecords.TransactionRecord
import io.vextabit.wallet.entities.transactionrecords.binancechain.BinanceChainIncomingTransactionRecord
import io.vextabit.wallet.entities.transactionrecords.binancechain.BinanceChainOutgoingTransactionRecord
import io.vextabit.wallet.entities.transactionrecords.bitcoin.BitcoinIncomingTransactionRecord
import io.vextabit.wallet.entities.transactionrecords.bitcoin.BitcoinOutgoingTransactionRecord
import io.vextabit.wallet.entities.transactionrecords.evm.*
import io.vextabit.wallet.modules.transactionInfo.TransactionInfoButtonType.*
import io.vextabit.wallet.modules.transactionInfo.adapters.TransactionInfoViewItem
import io.vextabit.wallet.modules.transactions.TransactionSource.*
import io.vextabit.wallet.modules.transactions.TransactionWallet
import io.horizontalsystems.coinkit.models.Coin
import io.horizontalsystems.core.SingleLiveEvent
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.disposables.CompositeDisposable
import java.math.BigDecimal

class TransactionInfoViewModel(
    private val service: TransactionInfoService,
    private val factory: TransactionInfoViewItemFactory,
    private val transaction: TransactionRecord,
    transactionWallet: TransactionWallet,
    private val clearables: List<Clearable>
) : ViewModel() {

    val titleLiveData = MutableLiveData<TransactionInfoModule.TitleViewItem>()
    val showShareLiveEvent = SingleLiveEvent<String>()
    val showTransactionLiveEvent = SingleLiveEvent<String>()
    val copyRawTransactionLiveEvent = SingleLiveEvent<String>()
    val explorerButton = MutableLiveData<Pair<String, Boolean>>()
    val blockchain = transactionWallet.source.blockchain
    val account = transactionWallet.source.account

    val viewItemsLiveData = MutableLiveData<List<TransactionInfoViewItem?>>()

    private var explorerData: TransactionInfoModule.ExplorerData =
        getExplorerData(transaction.transactionHash, service.testMode, blockchain, account)

    private val disposables = CompositeDisposable()
    private var rates: Map<Coin, CurrencyValue> = mutableMapOf()

    init {
        service.ratesAsync
            .subscribeIO {
                updateRates(it)
            }
            .let {
                disposables.add(it)
            }

        service.getRates(coinsForRates, transaction.timestamp)

        updateViewItems()
    }

    private fun updateRates(rates: Map<Coin, CurrencyValue>) {
        this.rates = rates
        updateViewItems()
    }

    private fun updateViewItems() {
        val viewItems =
            factory.getMiddleSectionItems(transaction, rates, service.lastBlockInfo, explorerData)

        viewItemsLiveData.postValue(viewItems)
    }

    override fun onCleared() {
        clearables.forEach {
            it.clear()
        }
    }

    fun onAdditionalButtonClick(buttonType: TransactionInfoButtonType) {
        when (buttonType) {
            is OpenExplorer -> buttonType.url?.let {
                showTransactionLiveEvent.postValue(it)
            }
            is RevokeApproval -> {
                TODO("Not yet implemented")
            }
            is Resend -> {
                TODO("Not yet implemented")
            }
        }
    }

    fun onActionButtonClick(actionButton: TransactionInfoActionButton) {
        when (actionButton) {
            is TransactionInfoActionButton.ShareButton -> showShareLiveEvent.postValue(actionButton.value)
            TransactionInfoActionButton.CopyButton -> copyRawTransactionLiveEvent.postValue(
                service.getRaw(
                    transaction.transactionHash
                )
            )
        }
    }

    private val coinsForRates: List<Coin>
        get() {
            val coins = mutableListOf<Coin>()

            val txCoins = when (val tx = transaction) {
                is EvmIncomingTransactionRecord -> listOf(tx.value.coin)
                is EvmOutgoingTransactionRecord -> listOf(tx.fee.coin, tx.value.coin)
                is SwapTransactionRecord -> listOf(
                    tx.fee,
                    tx.valueIn,
                    tx.valueOut
                ).mapNotNull { it?.coin }
                is ApproveTransactionRecord -> listOf(tx.fee.coin, tx.value.coin)
                is ContractCallTransactionRecord -> {
                    val tempCoinList = mutableListOf<Coin>()
                    if (tx.value.value != BigDecimal.ZERO) {
                        tempCoinList.add(tx.value.coin)
                    }
                    tempCoinList.addAll(tx.incomingInternalETHs.map { it.second.coin })
                    tempCoinList.addAll(tx.incomingEip20Events.map { it.second.coin })
                    tempCoinList.addAll(tx.outgoingEip20Events.map { it.second.coin })
                    tempCoinList
                }
                is BitcoinIncomingTransactionRecord -> listOf(tx.value.coin)
                is BitcoinOutgoingTransactionRecord -> listOf(
                    tx.fee,
                    tx.value
                ).mapNotNull { it?.coin }
                is BinanceChainIncomingTransactionRecord -> listOf(tx.value.coin)
                is BinanceChainOutgoingTransactionRecord -> listOf(
                    tx.fee,
                    tx.value
                ).map { it.coin }
                else -> emptyList()
            }

            if (transaction is EvmTransactionRecord && !transaction.foreignTransaction) {
                coins.add(transaction.fee.coin)
            }

            coins.addAll(txCoins)

            return coins.distinctBy { it.type }
        }

    private fun getExplorerData(
        hash: String,
        testMode: Boolean,
        blockchain: Blockchain,
        account: Account
    ): TransactionInfoModule.ExplorerData {
        return when (blockchain) {
            is Blockchain.Bitcoin -> TransactionInfoModule.ExplorerData(
                "blockchair.com",
                if (testMode) null else "https://blockchair.com/bitcoin/transaction/$hash"
            )
            is Blockchain.BitcoinCash -> TransactionInfoModule.ExplorerData(
                "btc.com",
                if (testMode) null else "https://bch.btc.com/$hash"
            )
            is Blockchain.Litecoin -> TransactionInfoModule.ExplorerData(
                "blockchair.com",
                if (testMode) null else "https://blockchair.com/litecoin/transaction/$hash"
            )
            is Blockchain.Dash -> TransactionInfoModule.ExplorerData(
                "dash.org",
                if (testMode) null else "https://insight.dash.org/insight/tx/$hash"
            )
            is Blockchain.Ethereum -> {
                val domain = when (service.ethereumNetworkType(account)) {
                    EthereumKit.NetworkType.EthMainNet -> "etherscan.io"
                    EthereumKit.NetworkType.EthRopsten -> "ropsten.etherscan.io"
                    EthereumKit.NetworkType.EthKovan -> "kovan.etherscan.io"
                    EthereumKit.NetworkType.EthRinkeby -> "rinkeby.etherscan.io"
                    EthereumKit.NetworkType.EthGoerli -> "goerli.etherscan.io"
                    EthereumKit.NetworkType.BscMainNet -> throw IllegalArgumentException("")
                }
                TransactionInfoModule.ExplorerData("etherscan.io", "https://$domain/tx/0x$hash")
            }
            is Blockchain.Bep2 -> TransactionInfoModule.ExplorerData(
                "binance.org",
                if (testMode) "https://testnet-explorer.binance.org/tx/$hash" else "https://explorer.binance.org/tx/$hash"
            )
            is Blockchain.BinanceSmartChain -> TransactionInfoModule.ExplorerData(
                "bscscan.com",
                "https://bscscan.com/tx/0x$hash"
            )
            is Blockchain.Zcash -> TransactionInfoModule.ExplorerData(
                "blockchair.com",
                if (testMode) null else "https://blockchair.com/zcash/transaction/$hash"
            )
        }
    }

}
