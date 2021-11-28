package io.vextabit.wallet.core.ethereum

import io.vextabit.wallet.core.IRateManager
import io.vextabit.coinkit.CoinKit
import io.vextabit.coinkit.models.Coin
import io.vextabit.coinkit.models.CoinType
import io.horizontalsystems.core.ICurrencyManager
import io.horizontalsystems.ethereumkit.models.Address

class EvmCoinServiceFactory(
        private val baseCoin: Coin,
        private val coinKit: CoinKit,
        private val currencyManager: ICurrencyManager,
        private val rateManager: IRateManager
) {
    val baseCoinService = EvmCoinService(baseCoin, currencyManager, rateManager)

    fun getCoinService(contractAddress: Address) = getCoinService(contractAddress.hex)

    fun getCoinService(contractAddress: String ) = getCoin(contractAddress)?.let { coin ->
        EvmCoinService(coin, currencyManager, rateManager)
    }

    private fun getCoin(contractAddress: String) = when (baseCoin.type) {
        CoinType.Ethereum -> coinKit.getCoin(CoinType.Erc20(contractAddress))
        CoinType.BinanceSmartChain -> coinKit.getCoin(CoinType.Bep20(contractAddress))
        else -> null
    }

}
