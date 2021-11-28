package io.vextabit.bankwallet.modules.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.AdapterState
import io.vextabit.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.BalanceData
import io.horizontalsystems.bankwallet.entities.Wallet
import io.horizontalsystems.xrateskit.entities.LatestRate

object BalanceModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val activeAccountService = ActiveAccountService(io.vextabit.bankwallet.core.App.accountManager)

            val balanceService2 = BalanceService2(
                BalanceActiveWalletRepository(io.vextabit.bankwallet.core.App.walletManager, io.vextabit.bankwallet.core.App.accountSettingManager),
                BalanceXRateRepository(io.vextabit.bankwallet.core.App.currencyManager, io.vextabit.bankwallet.core.App.xRateManager),
                BalanceAdapterRepository(io.vextabit.bankwallet.core.App.adapterManager, BalanceCache(io.vextabit.bankwallet.core.App.appDatabase.enabledWalletsCacheDao())),
                NetworkTypeChecker(io.vextabit.bankwallet.core.App.accountSettingManager),
                io.vextabit.bankwallet.core.App.localStorage,
                io.vextabit.bankwallet.core.App.connectivityManager,
                BalanceSorter(),
            )

            val rateAppService = RateAppService(io.vextabit.bankwallet.core.App.rateAppManager)

            return BalanceViewModel(
                balanceService2,
                rateAppService,
                activeAccountService,
                BalanceViewItemFactory(),
                io.vextabit.bankwallet.core.App.appConfigProvider.reportEmail
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
