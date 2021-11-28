package io.vextabit.wallet.modules.swap

import android.os.Bundle
import android.view.View
import androidx.navigation.navGraphViewModels
import io.vextabit.wallet.R
import io.vextabit.wallet.core.BaseFragment

abstract class SwapBaseFragment : BaseFragment() {

    private val mainViewModel by navGraphViewModels<SwapMainViewModel>(R.id.swapFragment)

    protected val dex: SwapMainModule.Dex
        get() = mainViewModel.dex

    protected abstract fun restoreProviderState(providerState: SwapMainModule.SwapProviderState)
    protected abstract fun getProviderState(): SwapMainModule.SwapProviderState

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreProviderState(mainViewModel.providerState)
    }

    override fun onStop() {
        super.onStop()

        mainViewModel.providerState = getProviderState()
    }

}
