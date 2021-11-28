package io.vextabit.wallet.modules.rooteddevice

import androidx.lifecycle.ViewModel
import io.vextabit.wallet.core.ILocalStorage
import io.horizontalsystems.core.SingleLiveEvent

class RootedDeviceViewModel(private val localStorage: ILocalStorage): ViewModel() {

    val openMainActivity = SingleLiveEvent<Void>()

    fun ignoreRootedDeviceWarningButtonClicked() {
        localStorage.ignoreRootedDeviceWarning = true
        openMainActivity.call()
    }

}
