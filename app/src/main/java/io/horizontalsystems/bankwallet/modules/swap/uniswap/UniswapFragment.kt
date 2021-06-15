package io.horizontalsystems.bankwallet.modules.swap.uniswap

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseFragment
import io.horizontalsystems.bankwallet.modules.swap.SwapMainFragment.Companion.dexParams
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule
import io.horizontalsystems.bankwallet.modules.swap.SwapMainViewModel
import io.horizontalsystems.bankwallet.modules.swap.allowance.SwapAllowanceViewModel
import io.horizontalsystems.bankwallet.modules.swap.approve.SwapApproveModule
import io.horizontalsystems.bankwallet.modules.swap.coincard.SwapCoinCardViewModel
import io.horizontalsystems.bankwallet.modules.swap.confirmation.SwapConfirmationModule
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.getNavigationResult
import io.horizontalsystems.core.setOnSingleClickListener
import kotlinx.android.synthetic.main.fragment_uniswap.*

class UniswapFragment : BaseFragment() {

    private val mainViewModel by navGraphViewModels<SwapMainViewModel>(R.id.swapFragment)

    val dex: SwapMainModule.Dex
        get() = requireArguments().getParcelable(dexParams)!!

    private val vmFactory by lazy { UniswapModule.Factory(this, dex) }
    private val uniswapViewModel by navGraphViewModels<UniswapViewModel>(R.id.swapFragment) { vmFactory }
    private val allowanceViewModel by navGraphViewModels<SwapAllowanceViewModel>(R.id.swapFragment) { vmFactory }
    private val coinCardViewModelFactory by lazy { SwapMainModule.CoinCardViewModelFactory(this, dex, uniswapViewModel.service, uniswapViewModel.tradeService) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_uniswap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fromCoinCardViewModel = ViewModelProvider(this, coinCardViewModelFactory).get(SwapMainModule.coinCardTypeFrom, SwapCoinCardViewModel::class.java)
        val fromCoinCardTitle = getString(R.string.Swap_FromAmountTitle)
        fromCoinCard.initialize(fromCoinCardTitle, fromCoinCardViewModel, this, viewLifecycleOwner)

        val toCoinCardViewModel = ViewModelProvider(this, coinCardViewModelFactory).get(SwapMainModule.coinCardTypeTo, SwapCoinCardViewModel::class.java)
        val toCoinCardTile = getString(R.string.Swap_ToAmountTitle)
        toCoinCard.initialize(toCoinCardTile, toCoinCardViewModel, this, viewLifecycleOwner)

        allowanceView.initialize(allowanceViewModel, viewLifecycleOwner)

        observeViewModel()

        dex.swapInputs?.let {
            uniswapViewModel.restoreInputs(it)
        }

        getNavigationResult(SwapApproveModule.requestKey)?.let {
            if (it.getBoolean(SwapApproveModule.resultKey)) {
                uniswapViewModel.didApprove()
            }
        }

        switchButton.setOnClickListener {
            uniswapViewModel.onTapSwitch()
        }

        advancedSettings.setOnSingleClickListener {
            mainViewModel.setSwapInputs(uniswapViewModel.getInputs())
            findNavController().navigate(R.id.swapFragment_to_swapTradeOptionsFragment)
        }

        approveButton.setOnSingleClickListener {
            uniswapViewModel.onTapApprove()
        }

        proceedButton.setOnSingleClickListener {
            uniswapViewModel.onTapProceed()
        }
    }

    private fun observeViewModel() {
        uniswapViewModel.isLoadingLiveData().observe(viewLifecycleOwner, { isLoading ->
            progressBar.isVisible = isLoading
        })

        uniswapViewModel.swapErrorLiveData().observe(viewLifecycleOwner, { error ->
            commonError.text = error
            commonError.isVisible = error != null
        })

        uniswapViewModel.tradeViewItemLiveData().observe(viewLifecycleOwner, { tradeViewItem ->
            setTradeViewItem(tradeViewItem)
        })

        uniswapViewModel.proceedActionLiveData().observe(viewLifecycleOwner, { action ->
            handleButtonAction(proceedButton, action)
        })

        uniswapViewModel.approveActionLiveData().observe(viewLifecycleOwner, { approveActionState ->
            handleButtonAction(approveButton, approveActionState)
        })

        uniswapViewModel.openApproveLiveEvent().observe(viewLifecycleOwner, { approveData ->
            SwapApproveModule.start(this, R.id.swapFragment_to_swapApproveFragment, navOptions(), approveData)
        })

        uniswapViewModel.advancedSettingsVisibleLiveData().observe(viewLifecycleOwner, { visible ->
            advancedSettingsViews.isVisible = visible
        })

        uniswapViewModel.openConfirmationLiveEvent().observe(viewLifecycleOwner, { sendEvmData ->
            SwapConfirmationModule.start(this, R.id.swapFragment_to_swapConfirmationFragment, navOptions(), sendEvmData)
        })

        val dexName = when (uniswapViewModel.service.dex.provider) {
            SwapMainModule.Dex.Provider.Uniswap -> "Uniswap"
            SwapMainModule.Dex.Provider.PancakeSwap -> "PancakeSwap"
            SwapMainModule.Dex.Provider.OneInch -> throw IllegalStateException()
        }
        poweredBy.text = "Powered by $dexName"
    }

    private fun handleButtonAction(button: Button, action: UniswapViewModel.ActionState?) {
        when (action) {
            UniswapViewModel.ActionState.Hidden -> {
                button.isVisible = false
            }
            is UniswapViewModel.ActionState.Enabled -> {
                button.isVisible = true
                button.isEnabled = true
                button.text = action.title
            }
            is UniswapViewModel.ActionState.Disabled -> {
                button.isVisible = true
                button.isEnabled = false
                button.text = action.title
            }
        }
    }

    private fun setTradeViewItem(tradeViewItem: UniswapViewModel.TradeViewItem?) {
        price.text = tradeViewItem?.price

        if (tradeViewItem?.priceImpact != null) {
            priceImpactViews.isVisible = true
            priceImpactValue.text = tradeViewItem.priceImpact.value
            priceImpactValue.setTextColor(priceImpactColor(requireContext(), tradeViewItem.priceImpact.level))
        } else {
            priceImpactViews.isVisible = false
        }

        if (tradeViewItem?.guaranteedAmount != null) {
            guaranteedAmountViews.isVisible = true
            minMaxTitle.text = tradeViewItem.guaranteedAmount.title
            minMaxValue.text = tradeViewItem.guaranteedAmount.value
        } else {
            guaranteedAmountViews.isVisible = false
        }
        poweredBy.isVisible = tradeViewItem == null
        poweredByLine.isVisible = tradeViewItem == null
    }

    private fun priceImpactColor(ctx: Context, priceImpactLevel: UniswapTradeService.PriceImpactLevel?): Int {
        val color = when (priceImpactLevel) {
            UniswapTradeService.PriceImpactLevel.Normal -> R.color.remus
            UniswapTradeService.PriceImpactLevel.Warning -> R.color.jacob
            UniswapTradeService.PriceImpactLevel.Forbidden -> R.color.lucian
            else -> R.color.grey
        }

        return ctx.getColor(color)
    }

}
