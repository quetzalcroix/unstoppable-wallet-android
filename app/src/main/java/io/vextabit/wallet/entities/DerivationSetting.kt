package io.vextabit.wallet.entities

import io.vextabit.wallet.entities.AccountType.Derivation
import io.vextabit.coinkit.models.CoinType

class DerivationSetting(val coinType: CoinType,
                        var derivation: Derivation)
