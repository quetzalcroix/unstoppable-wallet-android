package io.vextabit.wallet.core.factories

import io.vextabit.wallet.core.IAddressParser
import io.vextabit.wallet.core.utils.AddressParser
import io.vextabit.coinkit.models.Coin
import io.vextabit.coinkit.models.CoinType

class AddressParserFactory {
    fun parser(coin: Coin): IAddressParser {
        return when (coin.type) {
            is CoinType.Bitcoin -> AddressParser("bitcoin", true)
            is CoinType.Litecoin -> AddressParser("litecoin", true)
            is CoinType.BitcoinCash -> AddressParser("bitcoincash", false)
            is CoinType.Dash -> AddressParser("dash", true)
            is CoinType.Ethereum -> AddressParser("ethereum", true)
            is CoinType.Erc20 -> AddressParser("", true)
            is CoinType.BinanceSmartChain -> AddressParser("", true)
            is CoinType.Bep20 -> AddressParser("", true)
            is CoinType.Bep2 -> AddressParser("binance", true)
            is CoinType.Zcash -> AddressParser("zcash", true)
            is CoinType.Unsupported -> AddressParser("", false)
        }
    }

}
