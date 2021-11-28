package io.vextabit.wallet.modules.transactionInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.providers.Translator
import io.vextabit.wallet.entities.transactionrecords.bitcoin.TransactionLockState
import io.vextabit.wallet.modules.send.SendModule
import io.vextabit.wallet.modules.transactions.TransactionType
import io.vextabit.wallet.modules.transactions.TransactionViewItem
import io.horizontalsystems.core.helpers.DateHelper
import java.util.*

object TransactionInfoModule {

    class Factory(private val transactionViewItem: TransactionViewItem) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val adapter =
                io.vextabit.wallet.core.App.adapterManager.getTransactionsAdapterForWallet(transactionViewItem.wallet)!!
            val service = TransactionInfoService(
                adapter,
                io.vextabit.wallet.core.App.xRateManager,
                io.vextabit.wallet.core.App.currencyManager,
                io.vextabit.wallet.core.App.buildConfigProvider,
                io.vextabit.wallet.core.App.accountSettingManager
            )
            val factory = TransactionInfoViewItemFactory(
                io.vextabit.wallet.core.App.numberFormatter,
                Translator,
                DateHelper,
                TransactionInfoAddressMapper
            )
            return TransactionInfoViewModel(
                service,
                factory,
                transactionViewItem.record,
                transactionViewItem.wallet,
                listOf(service)
            ) as T
        }

    }

    data class TitleViewItem(
        val date: Date?,
        val primaryAmountInfo: SendModule.AmountInfo,
        val secondaryAmountInfo: SendModule.AmountInfo?,
        val type: TransactionType,
        val lockState: TransactionLockState?
    )

    data class ExplorerData(val title: String, val url: String?)
}

sealed class TransactionStatusViewItem {
    class Pending(val name: String) : TransactionStatusViewItem()

    //progress in 0.0 .. 1.0
    class Processing(val progress: Double, val name: String) : TransactionStatusViewItem()
    class Completed(val name: String) : TransactionStatusViewItem()
    object Failed : TransactionStatusViewItem()
}
