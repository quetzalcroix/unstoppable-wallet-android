package io.vextabit.wallet.modules.walletconnect.request.sendtransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import io.vextabit.wallet.R
import io.vextabit.wallet.core.AppLogger
import io.vextabit.wallet.core.BaseFragment
import io.vextabit.wallet.core.ethereum.EthereumFeeViewModel
import io.vextabit.wallet.core.setOnSingleClickListener
import io.vextabit.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.vextabit.wallet.modules.walletconnect.WalletConnectViewModel
import io.vextabit.wallet.modules.walletconnect.request.WalletConnectRequestModule
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.android.synthetic.main.fragment_wallet_connect_request.*

class WalletConnectSendEthereumTransactionRequestFragment : BaseFragment() {
    private val logger = AppLogger("wallet-connect")
    private val baseViewModel by navGraphViewModels<WalletConnectViewModel>(R.id.walletConnectMainFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_connect_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vmFactory = WalletConnectRequestModule.Factory(baseViewModel.sharedSendEthereumTransactionRequest!!, baseViewModel.service)
        val viewModel by viewModels<WalletConnectSendEthereumTransactionRequestViewModel> { vmFactory }
        val sendViewModel by viewModels<SendEvmTransactionViewModel> { vmFactory }
        val feeViewModel by viewModels<EthereumFeeViewModel> { vmFactory }

        btnApprove.setOnSingleClickListener {
            logger.info("click approve button")
            sendViewModel.send(logger)
        }

        btnReject.setOnSingleClickListener {
            viewModel.reject()
            close()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.reject()
            close()
        }

        sendViewModel.sendEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            btnApprove.isEnabled = enabled
        })

        sendViewModel.sendingLiveData.observe(viewLifecycleOwner, {
            btnReject.isEnabled = false
        })

        sendViewModel.sendSuccessLiveData.observe(viewLifecycleOwner, { transactionHash ->
            viewModel.approve(transactionHash)
            HudHelper.showSuccessMessage(requireActivity().findViewById(android.R.id.content), R.string.Hud_Text_Success)
            close()
        })

        sendViewModel.sendFailedLiveData.observe(viewLifecycleOwner, {
            HudHelper.showErrorMessage(requireActivity().findViewById(android.R.id.content), it)
        })

        sendEvmTransactionView.init(
                sendViewModel,
                feeViewModel,
                viewLifecycleOwner,
                parentFragmentManager,
                showSpeedInfoListener = {
                    findNavController().navigate(R.id.walletConnectRequestFragment_to_feeSpeedInfo, null, navOptions())
                }
        )
    }

    private fun close() {
        baseViewModel.sharedSendEthereumTransactionRequest = null
        findNavController().popBackStack()
    }

}
