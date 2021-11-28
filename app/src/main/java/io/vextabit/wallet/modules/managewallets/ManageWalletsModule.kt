package io.vextabit.wallet.modules.managewallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.modules.blockchainsettings.CoinSettingsViewModel
import io.vextabit.wallet.modules.restore.restoreselectcoins.CoinSettingsService
import io.vextabit.wallet.modules.restore.restoreselectcoins.RestoreSettingsService
import io.vextabit.wallet.modules.restore.restoreselectcoins.RestoreSettingsViewModel

object ManageWalletsModule {

    class Factory : ViewModelProvider.Factory {

        private val restoreSettingsService by lazy {
            RestoreSettingsService(io.vextabit.wallet.core.App.restoreSettingsManager)
        }

        private val coinSettingsService by lazy {
            CoinSettingsService()
        }

        private val manageWalletsService by lazy {
            ManageWalletsService(io.vextabit.wallet.core.App.coinManager, io.vextabit.wallet.core.App.walletManager, io.vextabit.wallet.core.App.accountManager, restoreSettingsService, coinSettingsService)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                RestoreSettingsViewModel::class.java -> {
                    RestoreSettingsViewModel(restoreSettingsService, listOf(restoreSettingsService)) as T
                }
                CoinSettingsViewModel::class.java -> {
                    CoinSettingsViewModel(coinSettingsService, listOf(coinSettingsService)) as T
                }
                ManageWalletsViewModel::class.java -> {
                    ManageWalletsViewModel(manageWalletsService, listOf(manageWalletsService)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
