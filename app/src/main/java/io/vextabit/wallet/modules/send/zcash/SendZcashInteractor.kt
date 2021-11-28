package io.vextabit.wallet.modules.send.zcash

import io.vextabit.wallet.core.AppLogger
import io.vextabit.wallet.core.ISendZcashAdapter
import io.vextabit.wallet.core.adapters.zcash.ZcashAdapter
import io.vextabit.wallet.modules.send.SendModule
import io.reactivex.Single
import java.math.BigDecimal

class SendZcashInteractor(
        private val adapter: ISendZcashAdapter
) : SendModule.ISendZcashInteractor {

    override val availableBalance: BigDecimal
        get() = adapter.availableBalance

    override val fee: BigDecimal
        get() = adapter.fee

    override fun validate(address: String): ZcashAdapter.ZCashAddressType {
        return adapter.validate(address)
    }

    override fun send(amount: BigDecimal, address: String, memo: String?, logger: AppLogger): Single<Unit> {
        return adapter.send(amount, address, memo ?: "", logger)
    }

}
