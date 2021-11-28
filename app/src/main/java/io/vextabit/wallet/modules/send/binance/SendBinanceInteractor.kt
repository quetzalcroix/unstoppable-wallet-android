package io.vextabit.wallet.modules.send.binance

import io.vextabit.wallet.core.AppLogger
import io.vextabit.wallet.core.ISendBinanceAdapter
import io.vextabit.wallet.modules.send.SendModule
import io.reactivex.Single
import java.math.BigDecimal

class SendBinanceInteractor(private val adapter: ISendBinanceAdapter) : SendModule.ISendBinanceInteractor {

    override val availableBalance: BigDecimal
        get() = adapter.availableBalance

    override val availableBinanceBalance: BigDecimal
        get() = adapter.availableBinanceBalance

    override val fee: BigDecimal
        get() = adapter.fee

    override fun validate(address: String) {
        adapter.validate(address)
    }

    override fun send(amount: BigDecimal, address: String, memo: String?, logger: AppLogger): Single<Unit> {
        return adapter.send(amount, address, memo, logger)
    }

}
