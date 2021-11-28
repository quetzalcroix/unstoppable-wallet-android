package io.vextabit.wallet.modules.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.AdapterState
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.BalanceData
import io.vextabit.wallet.entities.Wallet
import io.vextabit.xrateskit.entities.LatestRate

object BalanceModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val activeAccountService = ActiveAccountService(io.vextabit.wallet.core.App.accountManager)

            val balanceService2 = BalanceService2(
                BalanceActiveWalletRepository(io.vextabit.wallet.core.App.walletManager, io.vextabit.wallet.core.App.accountSettingManager),
                BalanceXRateRepository(io.vextabit.wallet.core.App.currencyManager, io.vextabit.wallet.core.App.xRateManager),
                BalanceAdapterRepository(io.vextabit.wallet.core.App.adapterManager, BalanceCache(io.vextabit.wallet.core.App.appDatabase.enabledWalletsCacheDao())),
                NetworkTypeChecker(io.vextabit.wallet.core.App.accountSettingManager),
                io.vextabit.wallet.core.App.localStorage,
                io.vextabit.wallet.core.App.connectivityManager,
                BalanceSorter(),
            )

            val rateAppService = RateAppService(io.vextabit.wallet.core.App.rateAppManager)

            return BalanceViewModel(
                balanceService2,
                rateAppService,
                activeAccountService,
                BalanceViewItemFactory(),
                io.vextabit.wallet.core.App.appConfigProvider.reportEmail
            ) as T
        }
    }

    data class BalanceItem(
        val wallet: Wallet,
        val mainNet: Boolean,
        val balanceData: BalanceData,
        val state: AdapterState,
        val latestRate: LatestRate? = null
    ) {
        val fiatValue get() = latestRate?.rate?.let { balanceData.available.times(it) }
    }
}
