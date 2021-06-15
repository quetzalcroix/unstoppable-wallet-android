package io.horizontalsystems.bankwallet.modules.swap.oneinch

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
import io.horizontalsystems.bankwallet.modules.swap.uniswap.UniswapTradeService
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.getNavigationResult
import io.horizontalsystems.core.setOnSingleClickListener
import kotlinx.android.synthetic.main.fragment_uniswap.*

class OneInchFragment : BaseFragment() {

    private val mainViewModel by navGraphViewModels<SwapMainViewModel>(R.id.swapFragment)

    val dex: SwapMainModule.Dex
        get() = requireArguments().getParcelable(dexParams)!!

    private val vmFactory by lazy { OneInchModule.Factory(this, requireArguments().getParcelable(dexParams)!!) }
    private val swapViewModel by navGraphViewModels<OneInchSwapViewModel>(R.id.swapFragment) { vmFactory }
    private val allowanceViewModel by navGraphViewModels<SwapAllowanceViewModel>(R.id.swapFragment) { vmFactory }
    private val coinCardViewModelFactory by lazy { SwapMainModule.CoinCardViewModelFactory(this, dex, swapViewModel.service, swapViewModel.tradeService) }

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
            swapViewModel.restoreInputs(it)
        }

        getNavigationResult(SwapApproveModule.requestKey)?.let {
            if (it.getBoolean(SwapApproveModule.resultKey)) {
                swapViewModel.didApprove()
            }
        }

        switchButton.setOnClickListener {
            swapViewModel.onTapSwitch()
        }

        advancedSettings.setOnSingleClickListener {
            mainViewModel.setSwapInputs(swapViewModel.getInputs())

            findNavController().navigate(R.id.swapFragment_to_swapTradeOptionsFragment)
        }

        approveButton.setOnSingleClickListener {
            swapViewModel.onTapApprove()
        }

        proceedButton.setOnSingleClickListener {
            swapViewModel.onTapProceed()
        }
    }

    private fun observeViewModel() {
        swapViewModel.isLoadingLiveData().observe(viewLifecycleOwner, { isLoading ->
            progressBar.isVisible = isLoading
        })

        swapViewModel.swapErrorLiveData().observe(viewLifecycleOwner, { error ->
            commonError.text = error
            commonError.isVisible = error != null
        })

        swapViewModel.tradeViewItemLiveData().observe(viewLifecycleOwner, { tradeViewItem ->
            setTradeViewItem(tradeViewItem)
        })

        swapViewModel.proceedActionLiveData().observe(viewLifecycleOwner, { action ->
            handleButtonAction(proceedButton, action)
        })

        swapViewModel.approveActionLiveData().observe(viewLifecycleOwner, { approveActionState ->
            handleButtonAction(approveButton, approveActionState)
        })

        swapViewModel.openApproveLiveEvent().observe(viewLifecycleOwner, { approveData ->
            SwapApproveModule.start(this, R.id.swapFragment_to_swapApproveFragment, navOptions(), approveData)
        })

        swapViewModel.advancedSettingsVisibleLiveData().observe(viewLifecycleOwner, { visible ->
            advancedSettingsViews.isVisible = visible
        })

        swapViewModel.openConfirmationLiveEvent().observe(viewLifecycleOwner, { sendEvmData ->
            SwapConfirmationModule.start(this, R.id.swapFragment_to_swapConfirmationFragment, navOptions(), sendEvmData)
        })

        poweredBy.text = "Powered by 1inch Network"
    }

    private fun handleButtonAction(button: Button, action: OneInchSwapViewModel.ActionState?) {
        when (action) {
            OneInchSwapViewModel.ActionState.Hidden -> {
                button.isVisible = false
            }
            is OneInchSwapViewModel.ActionState.Enabled -> {
                button.isVisible = true
                button.isEnabled = true
                button.text = action.title
            }
            is OneInchSwapViewModel.ActionState.Disabled -> {
                button.isVisible = true
                button.isEnabled = false
                button.text = action.title
            }
        }
    }

    private fun setTradeViewItem(tradeViewItem: OneInchSwapViewModel.TradeViewItem?) {
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
