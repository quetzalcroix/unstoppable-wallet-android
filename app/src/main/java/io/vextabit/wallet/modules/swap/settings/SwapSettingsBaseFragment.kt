package io.vextabit.wallet.modules.swap.settings

import androidx.navigation.navGraphViewModels
import io.vextabit.wallet.R
import io.vextabit.wallet.core.BaseFragment
import io.vextabit.wallet.modules.swap.SwapMainModule
import io.vextabit.wallet.modules.swap.SwapMainViewModel

abstract class SwapSettingsBaseFragment : BaseFragment() {
    private val mainViewModel by navGraphViewModels<SwapMainViewModel>(R.id.swapFragment)

    val dex: SwapMainModule.Dex
        get() = mainViewModel.dex

}
