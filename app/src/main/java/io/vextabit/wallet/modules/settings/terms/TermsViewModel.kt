package io.vextabit.wallet.modules.settings.terms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.vextabit.wallet.core.ITermsManager
import io.vextabit.wallet.core.managers.Term

class TermsViewModel(private val termsManager: ITermsManager) : ViewModel() {

    val termsLiveData = MutableLiveData<List<Term>>()

    private var terms: List<Term> = termsManager.terms

    init {
        termsLiveData.postValue(terms)
    }

    fun onTapTerm(index: Int) {
        terms[index].checked = !terms[index].checked
        termsManager.update(terms[index])
        termsLiveData.postValue(terms)
    }

}
