package io.vextabit.wallet.modules.market

import io.vextabit.wallet.core.IMarketStorage

class MarketService(private val storage: IMarketStorage) {

    var currentTab: MarketModule.Tab?
        get() = storage.currentTab
        set(value) {
            storage.currentTab = value
        }

}
