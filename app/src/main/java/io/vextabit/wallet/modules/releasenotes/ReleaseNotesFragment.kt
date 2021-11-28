package io.vextabit.wallet.modules.releasenotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import io.vextabit.wallet.R
import io.vextabit.wallet.core.BaseFragment
import io.vextabit.wallet.modules.markdown.MarkdownFragment
import io.vextabit.wallet.ui.helpers.LinkHelper
import kotlinx.android.synthetic.main.fragment_release_notes.*

class ReleaseNotesFragment : BaseFragment() {

    private val viewModel by viewModels<ReleaseNotesViewModel> { ReleaseNotesModule.Factory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_release_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closablePopup = arguments?.getBoolean(showAsClosablePopupKey) ?: false

        val markdownFragment = MarkdownFragment().apply {
            val bundle = Bundle()
            bundle.putString(MarkdownFragment.gitReleaseNotesUrlKey, viewModel.releaseNotesUrl)
            if (closablePopup) {
                bundle.putBoolean(MarkdownFragment.showAsClosablePopupKey, true)
            }
            arguments = bundle
        }

        childFragmentManager.commit {
            replace(R.id.fragmentContainerView, markdownFragment)
        }

        twitterIcon.setOnClickListener {
            openLink(viewModel.twitterUrl)
        }

        telegramIcon.setOnClickListener {
            openLink(viewModel.telegramUrl)
        }

        redditIcon.setOnClickListener {
            openLink(viewModel.redditUrl)
        }
    }

    private fun openLink(link: String) {
        context?.let { ctx ->
            LinkHelper.openLinkInAppBrowser(ctx, link)
        }
    }

    companion object {
        const val showAsClosablePopupKey = "showAsClosablePopup"
    }

}
