package io.vextabit.wallet.modules.swap.confirmation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import io.vextabit.wallet.R
import io.vextabit.wallet.core.AppLogger
import io.vextabit.wallet.core.BaseFragment
import io.vextabit.wallet.core.ethereum.EthereumFeeViewModel
import io.vextabit.wallet.core.setOnSingleClickListener
import io.vextabit.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.vextabit.wallet.modules.swap.SwapMainModule
import io.vextabit.wallet.modules.swap.SwapMainViewModel
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import io.horizontalsystems.snackbar.CustomSnackbar
import io.horizontalsystems.snackbar.SnackbarDuration
import kotlinx.android.synthetic.main.fragment_confirmation_swap.*

abstract class BaseSwapConfirmationFragment : BaseFragment() {

    protected abstract val logger: AppLogger
    protected abstract val sendViewModel: SendEvmTransactionViewModel
    protected abstract val feeViewModel: EthereumFeeViewModel
    protected abstract fun navigateToFeeInfo()

    private val mainViewModel by navGraphViewModels<SwapMainViewModel>(R.id.swapFragment)
    protected val dex: SwapMainModule.Dex
        get() = mainViewModel.dex

    private var snackbarInProcess: CustomSnackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_confirmation_swap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuClose -> {
                    findNavController().popBackStack(R.id.swapFragment, true)
                    true
                }
                else -> false
            }
        }
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        sendViewModel.sendEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            swapButton.isEnabled = enabled
        })

        sendViewModel.sendingLiveData.observe(viewLifecycleOwner, {
            snackbarInProcess = HudHelper.showInProcessMessage(
                requireView(),
                R.string.Swap_Swapping,
                SnackbarDuration.INDEFINITE
            )
        })

        sendViewModel.sendSuccessLiveData.observe(viewLifecycleOwner, { transactionHash ->
            HudHelper.showSuccessMessage(
                requireActivity().findViewById(android.R.id.content),
                R.string.Hud_Text_Success
            )
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack(R.id.swapFragment, true)
            }, 1200)
        })

        sendViewModel.sendFailedLiveData.observe(viewLifecycleOwner, {
            HudHelper.showErrorMessage(requireActivity().findViewById(android.R.id.content), it)

            findNavController().popBackStack()
        })

        sendEvmTransactionView.init(
            sendViewModel,
            feeViewModel,
            viewLifecycleOwner,
            parentFragmentManager,
            showSpeedInfoListener = {
                navigateToFeeInfo()
            }
        )

        swapButton.setOnSingleClickListener {
            logger.info("click swap button")
            sendViewModel.send(logger)
        }
    }

    override fun onDestroyView() {
        snackbarInProcess?.dismiss()
        super.onDestroyView()
    }

}
