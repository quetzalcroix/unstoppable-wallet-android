package io.vextabit.wallet.entities.transactionrecords.evm

import io.horizontalsystems.coinkit.models.Coin
import io.horizontalsystems.ethereumkit.models.FullTransaction

class ContractCreationTransactionRecord(
    fullTransaction: FullTransaction,
    baseCoin: Coin
) : EvmTransactionRecord(fullTransaction, baseCoin)
