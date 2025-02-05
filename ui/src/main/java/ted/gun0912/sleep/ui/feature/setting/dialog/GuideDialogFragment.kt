package ted.gun0912.sleep.ui.feature.setting.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import ted.gun0912.sleep.model.Guide
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.BaseDialogFragment
import ted.gun0912.sleep.ui.base.template.DialogFragmentTemplate
import ted.gun0912.sleep.ui.databinding.DialogGuideBinding

class GuideDialogFragment :
    BaseDialogFragment<DialogGuideBinding, GuideViewModel, GuideViewModel.Event>(
        R.layout.dialog_guide
    ) {
    override val viewModel: GuideViewModel by viewModels()

    val guideItems: List<Guide> by lazy { generateGuideItems() }

    override fun handleEvent(event: GuideViewModel.Event) {
        TODO("Not yet implemented")
    }


    private fun generateGuideItems(): List<Guide> =
        listOf(
            R.string.guide_title_1 to R.string.guide_description_1,
            R.string.guide_title_2 to R.string.guide_description_2,
            R.string.guide_title_3 to R.string.guide_description_3,
            R.string.guide_title_4 to R.string.guide_description_4,
            R.string.guide_title_5 to R.string.guide_description_5,
            R.string.guide_title_6 to R.string.guide_description_6,
            R.string.guide_title_7 to R.string.guide_description_7,
            R.string.guide_title_8 to R.string.guide_description_8,
        ).map { (titleResId, descriptionResId) ->
            Guide(getString(titleResId), getString(descriptionResId))
        }

    companion object : DialogFragmentTemplate<GuideDialogFragment>()
}
