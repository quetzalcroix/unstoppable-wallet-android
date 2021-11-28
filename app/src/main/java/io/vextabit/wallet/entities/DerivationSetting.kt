package io.vextabit.wallet.entities

import io.vextabit.wallet.entities.AccountType.Derivation
import io.horizontalsystems.coinkit.models.CoinType

class DerivationSetting(val coinType: CoinType,
                        var derivation: Derivation)
