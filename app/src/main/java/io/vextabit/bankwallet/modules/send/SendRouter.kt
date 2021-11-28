package io.vextabit.bankwallet.modules.send

import io.horizontalsystems.core.SingleLiveEvent

class SendRouter : SendModule.IRouter {

    val closeWithSuccess = SingleLiveEvent<Unit>()

    override fun closeWithSuccess() {
        closeWithSuccess.call()
    }
}
