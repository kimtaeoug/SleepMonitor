package ted.gun0912.sleep.ui.feature.sleep.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.BaseBottomDialogFragment
import ted.gun0912.sleep.ui.base.template.DialogFragmentTemplate
import ted.gun0912.sleep.ui.databinding.DialogChargePhoneNoticeBinding
import ted.gun0912.sleep.ui.extension.findListener
import ted.gun0912.sleep.ui.feature.sleep.dialog.ChargePhoneNoticeViewModel.Event

@AndroidEntryPoint
class ChargePhoneNoticeDialogFragment :
    BaseBottomDialogFragment<DialogChargePhoneNoticeBinding, ChargePhoneNoticeViewModel, Event>(
        R.layout.dialog_charge_phone_notice
    ) {
    override val viewModel: ChargePhoneNoticeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun handleEvent(event: Event) = when (event) {
        Event.StartSleep -> startSleep()
    }

    private fun startSleep() {
        findListener<StartSleepListener>()?.startSleep()
        dismissAllowingStateLoss()
    }

    fun interface StartSleepListener {
        fun startSleep()
    }

    companion object : DialogFragmentTemplate<ChargePhoneNoticeDialogFragment>()
}
