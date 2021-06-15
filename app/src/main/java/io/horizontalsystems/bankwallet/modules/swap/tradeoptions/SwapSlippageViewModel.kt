package io.horizontalsystems.bankwallet.modules.swap.tradeoptions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.ISwapTradeOptionsService.State
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.ISwapTradeOptionsService.TradeOptionsError
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.uniswap.UniswapSettingsService
import io.horizontalsystems.bankwallet.modules.swap.uniswap.UniswapService
import io.reactivex.disposables.CompositeDisposable

class SwapSlippageViewModel(private val service: UniswapSettingsService) : ViewModel(), IVerifiedInputViewModel {

    private val disposable = CompositeDisposable()

    override val inputFieldButtonItems: List<InputFieldButtonItem>
        get() {
            val bounds = UniswapSettingsService.recommendedSlippageBounds
            val lowerBoundTitle = bounds.lower.toPlainString()
            val upperBoundTitle = bounds.upper.toPlainString()

            return listOf(
                    InputFieldButtonItem("$lowerBoundTitle%") {
                        setTextLiveData.postValue(lowerBoundTitle)
                        onChangeText(lowerBoundTitle)
                    },
                    InputFieldButtonItem("$upperBoundTitle%") {
                        setTextLiveData.postValue(upperBoundTitle)
                        onChangeText(upperBoundTitle)
                    }
            )
        }

    override val inputFieldPlaceholder = UniswapService.defaultSlippage.stripTrailingZeros().toPlainString()
    override val setTextLiveData = MutableLiveData<String?>()
    override val cautionLiveData = MutableLiveData<Caution?>()
    override val initialValue: String?
        get() {
            val state = service.state
            if (state is State.Valid && state.tradeOptions.allowedSlippage.compareTo(UniswapService.defaultSlippage) != 0) {
                return state.tradeOptions.allowedSlippage.stripTrailingZeros().toPlainString()
            }

            return null
        }

    init {
        service.errorsObservable
                .subscribe { errors ->
                    val caution = errors.firstOrNull {
                        it is TradeOptionsError.InvalidSlippage
                    }?.localizedMessage?.let { localizedMessage ->
                        Caution(localizedMessage, Caution.Type.Error)
                    }

                    cautionLiveData.postValue(caution)
                }.let {
                    disposable.add(it)
                }
    }

    override fun onCleared() {
        disposable.clear()
    }

    override fun onChangeText(text: String?) {
        service.slippage = text?.toBigDecimalOrNull() ?: UniswapService.defaultSlippage
    }

    override fun isValid(text: String?): Boolean {
        if (text.isNullOrBlank()) return true

        val parsed = text.toBigDecimalOrNull()
        return parsed != null && parsed.scale() <= 2
    }
}
