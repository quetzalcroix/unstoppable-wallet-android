package io.vextabit.wallet.modules.settings.experimental

import androidx.lifecycle.ViewModel

class ExperimentalFeaturesPresenter(
        val router: ExperimentalFeaturesModule.IRouter
) : ViewModel(), ExperimentalFeaturesModule.IViewDelegate {

    override fun didTapBitcoinHodling() {
        router.showBitcoinHodling()
    }

}
