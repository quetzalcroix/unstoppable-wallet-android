package io.vextabit.bankwallet.core.providers

import androidx.annotation.StringRes
import io.vextabit.bankwallet.core.App

object Translator {

    fun getString(@StringRes id: Int): String {
        return io.vextabit.bankwallet.core.App.instance.localizedContext().getString(id)
    }

    fun getString(@StringRes id: Int, vararg params: Any): String {
        return io.vextabit.bankwallet.core.App.instance.localizedContext().getString(id, *params)
    }
}
