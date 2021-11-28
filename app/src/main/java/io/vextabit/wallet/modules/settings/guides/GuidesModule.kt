package io.vextabit.wallet.modules.settings.guides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.managers.GuidesManager

object GuidesModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val guidesService = GuidesRepository(GuidesManager, io.vextabit.wallet.core.App.connectivityManager, io.vextabit.wallet.core.App.languageManager)

            return GuidesViewModel(guidesService) as T
        }
    }
}

sealed class DataState<T> {
    class Loading<T> : DataState<T>()
    class Success<T>(val data: T) : DataState<T>()
    class Error<T>(val throwable: Throwable) : DataState<T>()
}
