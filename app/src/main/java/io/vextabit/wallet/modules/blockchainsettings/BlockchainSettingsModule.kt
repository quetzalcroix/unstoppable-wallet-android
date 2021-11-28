package io.vextabit.wallet.modules.blockchainsettings

import io.vextabit.wallet.entities.AccountType.Derivation
import io.vextabit.wallet.entities.BitcoinCashCoinType
import io.vextabit.wallet.ui.extensions.BottomSheetSelectorViewItem
import io.vextabit.coinkit.models.Coin

object BlockchainSettingsModule {

    data class Request(val coin: Coin, val type: RequestType)

    sealed class RequestType {
        class DerivationType(val derivations: List<Derivation>, val current: Derivation) : RequestType()
        class BitcoinCashType(val types: List<BitcoinCashCoinType>, val current: BitcoinCashCoinType) : RequestType()
    }

    data class Config(
            val coin: Coin,
            val title: String,
            val subtitle: String,
            val selectedIndexes: List<Int>,
            val viewItems: List<BottomSheetSelectorViewItem>,
            val description: String
    )

}
