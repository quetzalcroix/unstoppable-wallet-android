package io.vextabit.wallet.modules.evmnetwork

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.entities.Account
import kotlinx.android.parcel.Parcelize

object EvmNetworkModule {
    fun args(blockchain: Blockchain, account: Account): Bundle {
        return bundleOf("blockchain" to blockchain, "account" to account)
    }

    class Factory(arguments: Bundle) : ViewModelProvider.Factory {
        private val blockchain = arguments.getParcelable<Blockchain>("blockchain")!!
        private val account = arguments.getParcelable<Account>("account")!!

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            val service = EvmNetworkService(
                blockchain,
                account,
                io.vextabit.wallet.core.App.evmNetworkManager,
                io.vextabit.wallet.core.App.accountSettingManager
            )

            return EvmNetworkViewModel(service) as T
        }
    }


    @Parcelize
    enum class Blockchain : Parcelable {
        Ethereum, BinanceSmartChain
    }

}


