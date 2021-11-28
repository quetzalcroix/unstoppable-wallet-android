package io.vextabit.wallet.entities

import io.vextabit.wallet.core.FeeRatePriority

data class FeeRateInfo(val priority: FeeRatePriority, var feeRate: Long, val duration: Long? = null)
