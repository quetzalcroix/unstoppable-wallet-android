package io.vextabit.bankwallet.modules.networksettings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.entities.Account

object NetworkSettingsModule {

    private const val ACCOUNT = "account"

    fun args(account: Account) = bundleOf(ACCOUNT to account)

    class Factory(arguments: Bundle) : ViewModelProvider.Factory {
        val account: Account = arguments.getParcelable(ACCOUNT)!!

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val networkSettingsService = NetworkSettingsService(account, io.vextabit.bankwallet.core.App.accountSettingManager)
            return NetworkSettingsViewModel(networkSettingsService) as T
        }
    }

}
