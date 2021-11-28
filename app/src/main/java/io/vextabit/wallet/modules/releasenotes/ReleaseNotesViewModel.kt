package io.vextabit.wallet.modules.releasenotes

import androidx.lifecycle.ViewModel
import io.vextabit.wallet.core.IAppConfigProvider
import io.vextabit.wallet.core.managers.ReleaseNotesManager

class ReleaseNotesViewModel(
        appConfigProvider: IAppConfigProvider,
        releaseNotesManager: ReleaseNotesManager
) : ViewModel() {

    val releaseNotesUrl = releaseNotesManager.releaseNotesUrl
    val twitterUrl = appConfigProvider.appTwitterLink
    val telegramUrl = appConfigProvider.appTelegramLink
    val redditUrl = appConfigProvider.appRedditLink
}
