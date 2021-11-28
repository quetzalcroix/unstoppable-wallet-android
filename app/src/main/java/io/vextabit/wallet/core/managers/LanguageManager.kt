package io.vextabit.wallet.core.managers

import io.vextabit.wallet.core.App
import io.horizontalsystems.core.ILanguageManager
import java.util.*

class LanguageManager : ILanguageManager {

    override var fallbackLocale: Locale = Locale.ENGLISH

    override var currentLocale: Locale = io.vextabit.wallet.core.App.instance.getLocale()
        set(value) {
            field = value

            io.vextabit.wallet.core.App.instance.setLocale(currentLocale)
        }

    override var currentLanguage: String
        get() = currentLocale.language
        set(value) {
            currentLocale = Locale(value)
        }

    override val currentLanguageName: String
        get() = currentLocale.displayLanguage.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    override fun getName(language: String): String {
        return Locale(language).displayLanguage.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    override fun getNativeName(language: String): String {
        val locale = Locale(language)
        return locale.getDisplayLanguage(locale).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

}
