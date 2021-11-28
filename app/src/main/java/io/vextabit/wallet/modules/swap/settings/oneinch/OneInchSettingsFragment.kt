package io.vextabit.wallet.modules.swap.settings.oneinch

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import io.vextabit.wallet.R
import io.vextabit.wallet.core.setOnSingleClickListener
import io.vextabit.wallet.core.utils.ModuleField
import io.vextabit.wallet.modules.qrscanner.QRScannerActivity
import io.vextabit.wallet.modules.swap.oneinch.OneInchModule
import io.vextabit.wallet.modules.swap.oneinch.OneInchSwapViewModel
import io.vextabit.wallet.modules.swap.settings.RecipientAddressViewModel
import io.vextabit.wallet.modules.swap.settings.SwapSettingsBaseFragment
import io.vextabit.wallet.modules.swap.settings.SwapSlippageViewModel
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.android.synthetic.main.fragment_swap_settings_1inch.*

class OneInchSettingsFragment : SwapSettingsBaseFragment() {

    private val oneInchViewModel by navGraphViewModels<OneInchSwapViewModel>(R.id.swapFragment) { OneInchModule.Factory(dex) }

    private val vmFactory by lazy { OneInchSwapSettingsModule.Factory(oneInchViewModel.tradeService, dex.blockchain) }
    private val oneInchSettingsViewModel by viewModels<OneInchSettingsViewModel> { vmFactory }
    private val recipientAddressViewModel by viewModels<RecipientAddressViewModel> { vmFactory }
    private val slippageViewModel by viewModels<SwapSlippageViewModel> { vmFactory }

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

        slippageInputView.setViewModel(slippageViewModel, viewLifecycleOwner)

    }

}
