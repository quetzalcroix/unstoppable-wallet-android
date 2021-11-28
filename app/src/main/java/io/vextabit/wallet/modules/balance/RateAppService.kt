package io.vextabit.wallet.modules.balance

import io.vextabit.wallet.core.Clearable
import io.vextabit.wallet.core.IRateAppManager

class RateAppService(private val rateAppManager: IRateAppManager) : Clearable {

    fun onBalancePageActive() {
        rateAppManager.onBalancePageActive()
    }

    fun onBalancePageInactive() {
        rateAppManager.onBalancePageInactive()
    }

    override fun clear() = Unit
}
