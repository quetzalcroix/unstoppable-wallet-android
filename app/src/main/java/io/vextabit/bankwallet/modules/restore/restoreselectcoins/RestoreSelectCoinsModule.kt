package io.vextabit.bankwallet.modules.restore.restoreselectcoins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.entities.AccountType
import io.horizontalsystems.bankwallet.modules.blockchainsettings.CoinSettingsViewModel
import io.horizontalsystems.bankwallet.modules.enablecoins.*

object RestoreSelectCoinsModule {

    class Factory(private val accountType: AccountType) : ViewModelProvider.Factory {

        private val enableCoinsService by lazy {
            EnableCoinsService(
                    io.vextabit.bankwallet.core.App.buildConfigProvider,
                    io.vextabit.bankwallet.core.App.coinManager,
                    EnableCoinsBep2Provider(io.vextabit.bankwallet.core.App.buildConfigProvider),
                    EnableCoinsEip20Provider(io.vextabit.bankwallet.core.App.networkManager, io.vextabit.bankwallet.core.App.appConfigProvider, EnableCoinsEip20Provider.EnableCoinMode.Erc20),
                    EnableCoinsEip20Provider(io.vextabit.bankwallet.core.App.networkManager, io.vextabit.bankwallet.core.App.appConfigProvider, EnableCoinsEip20Provider.EnableCoinMode.Bep20)
            )
        }

        private val restoreSettingsService by lazy {
            RestoreSettingsService(io.vextabit.bankwallet.core.App.restoreSettingsManager)
        }
        private val coinSettingsService by lazy {
            CoinSettingsService()
        }

        private val restoreSelectCoinsService by lazy {
            RestoreSelectCoinsService(
                    accountType,
                    io.vextabit.bankwallet.core.App.accountFactory,
                    io.vextabit.bankwallet.core.App.accountManager,
                    io.vextabit.bankwallet.core.App.walletManager,
                    io.vextabit.bankwallet.core.App.coinManager,
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

