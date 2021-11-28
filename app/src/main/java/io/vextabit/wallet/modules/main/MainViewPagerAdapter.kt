package io.vextabit.wallet.modules.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.vextabit.wallet.modules.balance.BalanceFragment
import io.vextabit.wallet.modules.balance.BalanceNoCoinsFragment
import io.vextabit.wallet.modules.balanceonboarding.BalanceOnboardingViewModel.BalanceViewType
import io.vextabit.wallet.modules.market.MarketFragment
import io.vextabit.wallet.modules.onboarding.OnboardingFragment
import io.vextabit.wallet.modules.settings.main.MainSettingsFragment
import io.vextabit.wallet.modules.transactions.TransactionsFragment

class MainViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment.childFragmentManager, fragment.viewLifecycleOwner.lifecycle) {

    private val itemIds = mutableListOf(0, getBalancePageId(BalanceViewType.Balance), 2, 3)

    var balancePageType: BalanceViewType = BalanceViewType.Balance
        set(value) {
            if (field == value) return
            field = value

            itemIds[1] = getBalancePageId(value)
            notifyItemChanged(1)
        }

    override fun containsItem(itemId: Long) = itemIds.contains(itemId)
    override fun getItemId(position: Int) = itemIds[position]
    override fun getItemCount() = 4

    override fun createFragment(position: Int) = when (position) {
        0 -> MarketFragment()
        1 -> when (val tmp = balancePageType) {
            BalanceViewType.NoAccounts -> OnboardingFragment()
            is BalanceViewType.NoCoins -> BalanceNoCoinsFragment(tmp.accountName)
            BalanceViewType.Balance -> BalanceFragment()
        }
        2 -> TransactionsFragment()
        3 -> MainSettingsFragment()
        else -> throw IllegalStateException()
    }

    private fun getBalancePageId(type: BalanceViewType): Long = when (type) {
        BalanceViewType.NoAccounts -> 10
        BalanceViewType.Balance -> 11
        is BalanceViewType.NoCoins -> 12
    }

}
