package io.horizontalsystems.bankwallet.modules.swap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.bankwallet.core.subscribeIO
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule.Dex
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class DexManager(dex: Dex) {
    private val onProviderUpdateSubject = PublishSubject.create<Dex>()
    val onProviderUpdateObservable: Observable<Dex> = onProviderUpdateSubject

    var dex: Dex = dex
        set(value) {
            val oldProvider = field.provider
            field = value
            if (oldProvider.id != value.provider.id) {
                onProviderUpdateSubject.onNext(value)
            }
        }
}

class SwapMainViewModel(
        val dexManager: DexManager
) : ViewModel() {

    val dexLiveData = MutableLiveData<Dex>()

    val dex: Dex
        get() = dexManager.dex

    private val disposable = CompositeDisposable()

    init {
        dexManager.onProviderUpdateObservable
                .subscribeIO {
                    dexLiveData.postValue(it)
                }.let {
                    disposable.add(it)
                }
    }

    fun setProvider(provider: Dex.Provider) {
        val blockchain = dex.blockchain
        val swapInputs = dex.swapInputs
        dexManager.dex = Dex(blockchain, provider, swapInputs)
    }

    fun setSwapInputs(swapInputs: SwapMainModule.SwapInputs) {
        val blockchain = dex.blockchain
        val provider = dex.provider

        dexManager.dex = Dex(blockchain, provider, swapInputs)
    }

}
