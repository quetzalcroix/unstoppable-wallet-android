package io.horizontalsystems.bankwallet.modules.swap.oneinch

import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule.AmountType
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.uniswap.SwapTradeOptions
import io.horizontalsystems.coinkit.models.Coin
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*


class OneInchTradeService(
        evmKit: EthereumKit,
//        private val uniswapProvider: UniswapProvider,
        coinFrom: Coin?
) : SwapMainModule.ISwapTradeService {

    private var lastBlockDisposable: Disposable? = null

    //region internal subjects
    private val amountTypeSubject = PublishSubject.create<AmountType>()
    private val coinFromSubject = PublishSubject.create<Optional<Coin>>()
    private val coinToSubject = PublishSubject.create<Optional<Coin>>()
    private val amountFromSubject = PublishSubject.create<Optional<BigDecimal>>()
    private val amountToSubject = PublishSubject.create<Optional<BigDecimal>>()
    private val stateSubject = PublishSubject.create<State>()
    private val tradeOptionsSubject = PublishSubject.create<SwapTradeOptions>()
    //endregion

    init {
        lastBlockDisposable = evmKit.lastBlockHeightFlowable
                .subscribeOn(Schedulers.io())
                .subscribe {
//                    syncSwapData()
                }
    }

    //region outputs
    override var coinFrom: Coin? = coinFrom
        private set(value) {
            field = value
            coinFromSubject.onNext(Optional.ofNullable(value))
        }
    override val coinFromObservable: Observable<Optional<Coin>> = coinFromSubject

    override var coinTo: Coin? = null
        private set(value) {
            field = value
            coinToSubject.onNext(Optional.ofNullable(value))
        }
    override val coinToObservable: Observable<Optional<Coin>> = coinToSubject

    override var amountFrom: BigDecimal? = null
        private set(value) {
            field = value
            amountFromSubject.onNext(Optional.ofNullable(value))
        }
    override val amountFromObservable: Observable<Optional<BigDecimal>> = amountFromSubject

    override var amountTo: BigDecimal? = null
        private set(value) {
            field = value
            amountToSubject.onNext(Optional.ofNullable(value))
        }
    override val amountToObservable: Observable<Optional<BigDecimal>> = amountToSubject

    override var amountType: AmountType = AmountType.ExactFrom
        private set(value) {
            field = value
            amountTypeSubject.onNext(value)
        }
    override val amountTypeObservable: Observable<AmountType> = amountTypeSubject

    var state: State = State.NotReady()
        private set(value) {
            field = value
            stateSubject.onNext(value)
        }
    val stateObservable: Observable<State> = stateSubject

    var tradeOptions: SwapTradeOptions = SwapTradeOptions()
        set(value) {
            field = value
            tradeOptionsSubject.onNext(value)
            syncQuote()
        }

    val tradeOptionsObservable: Observable<SwapTradeOptions> = tradeOptionsSubject

    @Throws
    fun transactionData(quote: Quote): TransactionData {
//        TODO()
        return TransactionData(Address("0x11111112542d85b3ef69ae05771c2dccff4faa26"), BigInteger.ZERO, byteArrayOf())
    }

    override fun enterCoinFrom(coin: Coin?) {
        if (coinFrom == coin) return

        coinFrom = coin

        if (amountType == AmountType.ExactTo) {
            amountFrom = null
        }

        if (coinTo == coinFrom) {
            coinTo = null
            amountTo = null
        }

        syncQuote()
    }

    override fun enterCoinTo(coin: Coin?) {
        if (coinTo == coin) return

        coinTo = coin

        if (amountType == AmountType.ExactFrom) {
            amountTo = null
        }

        if (coinFrom == coinTo) {
            coinFrom = null
            amountFrom = null
        }

        syncQuote()
    }

    override fun enterAmountFrom(amount: BigDecimal?) {
        amountType = AmountType.ExactFrom

        if (amountsEqual(amountFrom, amount)) return

        amountFrom = amount
        amountTo = null
        syncQuote()
    }

    override fun enterAmountTo(amount: BigDecimal?) {
        amountType = AmountType.ExactTo

        if (amountsEqual(amountTo, amount)) return

        amountTo = amount
        amountFrom = null
        syncQuote()
    }

    override fun switchCoins() {
        val swapCoin = coinTo
        coinTo = coinFrom

        enterCoinFrom(swapCoin)
    }

    override fun restoreState(swapInputs: SwapMainModule.SwapInputs) {
        coinTo = swapInputs.coinTo
        coinFrom = swapInputs.coinFrom
        amountType = swapInputs.amountType

        when (swapInputs.amountType) {
            AmountType.ExactFrom -> {
                amountFrom = swapInputs.amountFrom
                amountTo = null
            }
            AmountType.ExactTo -> {
                amountTo = swapInputs.amountTo
                amountFrom = null
            }
        }

        syncQuote()
    }

    fun onCleared() {
        lastBlockDisposable?.dispose()
    }
    //endregion


    private fun syncQuote() {
        val amountFrom = amountFrom ?: return
        val coinFrom = coinFrom ?: return
        val coinTo = coinTo ?: return


        handle(Quote(amountFrom, coinFrom, amountFrom.multiply(BigDecimal("2")), coinTo))
    }

    private fun handle(quote: Quote) {
        amountTo = quote.amountTo

        state = State.Ready(quote)
    }

    private fun amountsEqual(amount1: BigDecimal?, amount2: BigDecimal?): Boolean {
        return when {
            amount1 == null && amount2 == null -> true
            amount1 != null && amount2 != null && amount2.compareTo(amount1) == 0 -> true
            else -> false
        }
    }

    //region models
    sealed class State {
        object Loading : State()
        class Ready(val quote: Quote) : State()
        class NotReady(val errors: List<Throwable> = listOf()) : State()
    }

    data class Quote(
            val amountFrom: BigDecimal,
            val coinFrom: Coin,
            val amountTo: BigDecimal,
            val coinTo: Coin
    )
    //endregion

}
