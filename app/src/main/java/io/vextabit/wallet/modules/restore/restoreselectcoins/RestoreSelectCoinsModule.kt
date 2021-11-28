package io.vextabit.wallet.modules.restore.restoreselectcoins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.entities.AccountType
import io.vextabit.wallet.modules.blockchainsettings.CoinSettingsViewModel
import io.vextabit.wallet.modules.enablecoins.*

object RestoreSelectCoinsModule {

    class Factory(private val accountType: AccountType) : ViewModelProvider.Factory {

        private val enableCoinsService by lazy {
            EnableCoinsService(
                    io.vextabit.wallet.core.App.buildConfigProvider,
                    io.vextabit.wallet.core.App.coinManager,
                    EnableCoinsBep2Provider(io.vextabit.wallet.core.App.buildConfigProvider),
                    EnableCoinsEip20Provider(io.vextabit.wallet.core.App.networkManager, io.vextabit.wallet.core.App.appConfigProvider, EnableCoinsEip20Provider.EnableCoinMode.Erc20),
                    EnableCoinsEip20Provider(io.vextabit.wallet.core.App.networkManager, io.vextabit.wallet.core.App.appConfigProvider, EnableCoinsEip20Provider.EnableCoinMode.Bep20)
            )
        }

        private val restoreSettingsService by lazy {
            RestoreSettingsService(io.vextabit.wallet.core.App.restoreSettingsManager)
        }
        private val coinSettingsService by lazy {
            CoinSettingsService()
        }

        private val restoreSelectCoinsService by lazy {
            RestoreSelectCoinsService(
                    accountType,
                    io.vextabit.wallet.core.App.accountFactory,
                    io.vextabit.wallet.core.App.accountManager,
                    io.vextabit.wallet.core.App.walletManager,
                    io.vextabit.wallet.core.App.coinManager,
                    enableCoinsService,
                    restoreSettingsService,
                    coinSettingsService)
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
                EnableCoinsViewModel::class.java -> {
                    EnableCoinsViewModel(enableCoinsService) as T
                }
                RestoreSelectCoinsViewModel::class.java -> {
                    RestoreSelectCoinsViewModel(restoreSelectCoinsService, listOf(restoreSelectCoinsService)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}

