package io.horizontalsystems.bankwallet.modules.swap.tradeoptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.navGraphViewModels
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseFragment
import io.horizontalsystems.bankwallet.modules.swap.SwapMainFragment.Companion.dexParams
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule
import io.horizontalsystems.bankwallet.modules.swap.SwapMainViewModel
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.oneinch.OneInchSettingsFragment
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.uniswap.UniswapSettingsFragment
import io.horizontalsystems.core.findNavController
import kotlinx.android.synthetic.main.fragment_swap_settings.*

class SwapSettingsMainFragment : BaseFragment() {
    private val swapMainViewModel by navGraphViewModels<SwapMainViewModel>(R.id.swapFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_swap_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCancel -> {
                    findNavController().popBackStack()
                    true
                }
                R.id.menuUniswap -> {
                    swapMainViewModel.setProvider(SwapMainModule.Dex.Provider.Uniswap)
                    true
                }
                R.id.menu1inch -> {
                    swapMainViewModel.setProvider(SwapMainModule.Dex.Provider.OneInch)
                    true
                }

                else -> false
            }
        }

        updateDexView(swapMainViewModel.dex)

        swapMainViewModel.dexLiveData.observe(viewLifecycleOwner, {
            updateDexView(it)
        })

    }

    private fun updateDexView(dex: SwapMainModule.Dex) {
        when (dex.provider) {
            SwapMainModule.Dex.Provider.Uniswap,
            SwapMainModule.Dex.Provider.PancakeSwap -> {
                childFragmentManager.beginTransaction().replace(R.id.fragment_placeholder, UniswapSettingsFragment::class.java, bundleOf(dexParams to swapMainViewModel.dex), null).commitNow()
            }
            SwapMainModule.Dex.Provider.OneInch -> {
                childFragmentManager.beginTransaction().replace(R.id.fragment_placeholder, OneInchSettingsFragment::class.java, bundleOf(dexParams to swapMainViewModel.dex), null).commitNow()
            }
        }
    }
}
