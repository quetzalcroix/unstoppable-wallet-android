package io.vextabit.wallet.modules.swap.confirmation.oneinch

import androidx.fragment.app.viewModels
import io.vextabit.wallet.R
import io.vextabit.wallet.core.AppLogger
import io.vextabit.wallet.core.ethereum.EthereumFeeViewModel
import io.vextabit.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.vextabit.wallet.modules.swap.confirmation.BaseSwapConfirmationFragment
import io.horizontalsystems.core.findNavController

class OneInchSwapConfirmationFragment : BaseSwapConfirmationFragment() {
    override val logger = AppLogger("swap_1inch")

    private val vmFactory by lazy { OneInchConfirmationModule.Factory(dex.blockchain, requireArguments()) }
    override val sendViewModel by viewModels<SendEvmTransactionViewModel> { vmFactory }
    override val feeViewModel by viewModels<EthereumFeeViewModel> { vmFactory }

    override fun navigateToFeeInfo() {
        findNavController().navigate(R.id.oneInchConfirmationFragment_to_feeSpeedInfo, null, navOptions())
    }

}
