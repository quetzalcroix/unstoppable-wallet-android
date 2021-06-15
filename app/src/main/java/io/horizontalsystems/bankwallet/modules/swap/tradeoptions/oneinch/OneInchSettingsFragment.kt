package io.horizontalsystems.bankwallet.modules.swap.tradeoptions.oneinch

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseFragment
import io.horizontalsystems.bankwallet.core.setOnSingleClickListener
import io.horizontalsystems.bankwallet.core.utils.ModuleField
import io.horizontalsystems.bankwallet.modules.qrscanner.QRScannerActivity
import io.horizontalsystems.bankwallet.modules.swap.SwapMainViewModel
import io.horizontalsystems.bankwallet.modules.swap.oneinch.OneInchModule
import io.horizontalsystems.bankwallet.modules.swap.oneinch.OneInchSwapViewModel
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.RecipientAddressViewModel
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.android.synthetic.main.fragment_swap_settings_uniswap.*

class OneInchSettingsFragment : BaseFragment() {
    private val swapMainViewModel by navGraphViewModels<SwapMainViewModel>(R.id.swapFragment)
    private val uniswapViewModel by navGraphViewModels<OneInchSwapViewModel>(R.id.swapFragment) { OneInchModule.Factory(this, swapMainViewModel.dex) }

    private val vmFactory by lazy { OneInchSwapSettingsModule.Factory(uniswapViewModel.tradeService) }
    private val oneInchSettingsViewModel by viewModels<OneInchSettingsViewModel> { vmFactory }
    private val recipientAddressViewModel by viewModels<RecipientAddressViewModel> { vmFactory }

    private val qrScannerResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.getStringExtra(ModuleField.SCAN_ADDRESS)?.let {
                    recipientAddressViewModel.onFetch(it)
                }
            }
            Activity.RESULT_CANCELED -> {
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_swap_settings_1inch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oneInchSettingsViewModel.actionStateLiveData.observe(viewLifecycleOwner) { actionState ->
            when (actionState) {
                is OneInchSettingsViewModel.ActionState.Enabled -> {
                    applyButton.isEnabled = true
                    applyButton.text = getString(R.string.SwapSettings_Apply)
                }
                is OneInchSettingsViewModel.ActionState.Disabled -> {
                    applyButton.isEnabled = false
                    applyButton.text = actionState.title
                }
            }
        }

        applyButton.setOnSingleClickListener {
            if (oneInchSettingsViewModel.onDoneClick()) {
                findNavController().popBackStack()
            } else {
                HudHelper.showErrorMessage(this.requireView(), getString(R.string.default_error_msg))
            }
        }

        recipientAddressInputView.setViewModel(recipientAddressViewModel, viewLifecycleOwner, {
            val intent = QRScannerActivity.getIntentForFragment(this)
            qrScannerResultLauncher.launch(intent)
        })

    }

}
