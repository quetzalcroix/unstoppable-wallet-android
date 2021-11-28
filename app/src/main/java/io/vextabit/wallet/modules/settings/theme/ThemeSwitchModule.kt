package io.vextabit.wallet.modules.settings.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.R
import io.vextabit.wallet.core.App
import io.horizontalsystems.views.ListPosition

object ThemeSwitchModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ThemeSwitchViewModel(io.vextabit.wallet.core.App.localStorage) as T
        }
    }
}

data class ThemeViewItem(
        val themeType: ThemeType,
        val checked: Boolean,
        val listPosition: ListPosition
)

enum class ThemeType(val value: String) {
    Dark("Dark"),
    Light("Light"),
    System("System");

    fun getTitle(): Int {
        return when (this) {
            Dark -> R.string.SettingsTheme_Dark
            Light -> R.string.SettingsTheme_Light
            System -> R.string.SettingsTheme_System
        }
    }

    fun getIcon(): Int {
        return when (this) {
            Dark -> R.drawable.ic_theme_dark
            Light -> R.drawable.ic_theme_light
            System -> R.drawable.ic_theme_system
        }
    }
}
