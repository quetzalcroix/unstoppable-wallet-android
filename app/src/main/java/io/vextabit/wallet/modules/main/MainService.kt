package io.vextabit.wallet.modules.main

import io.vextabit.wallet.core.ILocalStorage
import io.vextabit.wallet.core.utils.RootUtil

class MainService(
        private val rootUtil: RootUtil,
        private val localStorage: ILocalStorage
) {

    val isDeviceRooted: Boolean
        get() = rootUtil.isRooted

    val ignoreRootCheck: Boolean
        get() = localStorage.ignoreRootedDeviceWarning

}
