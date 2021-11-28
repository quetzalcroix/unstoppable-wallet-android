package io.vextabit.wallet.modules.restoremnemonic

import io.vextabit.wallet.core.Clearable
import io.vextabit.wallet.core.managers.PassphraseValidator
import io.vextabit.wallet.core.managers.WordsManager
import io.vextabit.wallet.entities.AccountType
import io.reactivex.subjects.BehaviorSubject

class RestoreMnemonicService(
        private val wordsManager: WordsManager,
        private val passphraseValidator: PassphraseValidator
) : Clearable {

    var passphraseEnabled: Boolean = false
        set(value) {
            field = value
            passphraseEnabledObservable.onNext(value)
        }
    val passphraseEnabledObservable = BehaviorSubject.createDefault(passphraseEnabled)

    var passphrase = ""

    override fun clear() = Unit

    fun isWordValid(word: String): Boolean {
        return wordsManager.isWordValid(word)
    }

    fun isWordPartiallyValid(word: String): Boolean {
        return wordsManager.isWordPartiallyValid(word)
    }

    fun accountType(words: List<String>): AccountType {
        if (passphraseEnabled && passphrase.isBlank()) {
            throw RestoreError.EmptyPassphrase
        }

        if (words.size != 12 && words.size != 24) {
            throw ValidationError.InvalidWordCountException(words.size)
        }

        wordsManager.validateChecksum(words)

        return AccountType.Mnemonic(words, passphrase)
    }

    fun validatePassphrase(text: String?): Boolean {
        return passphraseValidator.validate(text)
    }

    sealed class ValidationError : Throwable() {
        class InvalidWordCountException(val count: Int) : ValidationError()
    }

    sealed class RestoreError : Throwable() {
        object EmptyPassphrase : RestoreError()
    }

}
