package io.vextabit.wallet.modules.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.utils.RootUtil

object MainModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = MainService(RootUtil, io.vextabit.wallet.core.App.localStorage)
            return MainViewModel(io.vextabit.wallet.core.App.pinComponent, io.vextabit.wallet.core.App.rateAppManager, io.vextabit.wallet.core.App.backupManager, io.vextabit.wallet.core.App.termsManager, io.vextabit.wallet.core.App.accountManager, io.vextabit.wallet.core.App.releaseNotesManager, service) as T
        }
    }

    fun start(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        context.startActivity(intent)
    }

    fun startAsNewTask(context: Activity, activeTab: Int? = null) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activeTab?.let {
            intent.putExtra(MainActivity.ACTIVE_TAB_KEY, it)
        }
        context.startActivity(intent)
        context.overridePendingTransition(0, 0)
    }
}
