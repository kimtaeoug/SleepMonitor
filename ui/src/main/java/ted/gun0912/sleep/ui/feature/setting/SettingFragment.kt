package ted.gun0912.sleep.ui.feature.setting

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import ted.gun0912.sleep.component.Toaster
import ted.gun0912.sleep.model.Gender
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.databinding.FragmentSettingBinding
import ted.gun0912.sleep.ui.databinding.LayoutEditBirthYearBinding
import ted.gun0912.sleep.ui.databinding.LayoutEditDeviceNameBinding
import ted.gun0912.sleep.ui.feature.main.BaseTabFragment
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingActivity
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingStep
import ted.gun0912.sleep.ui.feature.profile.ProfileInputActivity
import ted.gun0912.sleep.ui.feature.setting.SettingViewModel.Event
import ted.gun0912.sleep.ui.feature.setting.dialog.GuideDialogFragment
import ted.gun0912.sleep.worker.SleepTimeNotificationWorker
import java.time.LocalTime
import javax.inject.Inject


@AndroidEntryPoint
class SettingFragment :
    BaseTabFragment<FragmentSettingBinding, SettingViewModel, Event>(R.layout.fragment_setting) {
    override val viewModel: SettingViewModel by viewModels()

    @Inject
    lateinit var toaster: Toaster

    override fun handleEvent(event: Event) = when (event) {
        Event.ChangeDevice -> showOnboardingScan()
        Event.ShowProfileInput -> showProfileInputActivity()
        is Event.ChangeGender -> changeGender(event.gender, event.completeListener)
        is Event.ChangeBirthYear -> changeBirthYear(event.birthYear, event.completeListener)
        is Event.ChangeSleepTime -> changeSleepTime(event.sleepTime, event.completeListener)
        Event.CancelSleepTimeNotification -> cancelSleepTimeNotification()
        is Event.SetupSleepTimeNotification -> setupSleepTimeNotification(event.sleepTime)
        Event.ShowGuide -> showGuideDialog()
        is Event.ChangeDeviceName -> changeDeviceName(event.deviceName, event.completeListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDeviceIdCopy()
    }

    private fun setupDeviceIdCopy() {
        val clipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.viewDeviceId.setOnLongClickListener {
            val text = binding.tvDeviceId.text
            val clipData = ClipData.newPlainText("text", text)
            clipboardManager.setPrimaryClip(clipData)
            toaster.showSuccessToast(getString(R.string.device_id_copied))
            true
        }
    }

    private fun showOnboardingScan() {
        OnBoardingActivity.startActivity(requireContext(), OnBoardingStep.SCAN_SLEEP)
    }

    private fun showProfileInputActivity() {
        ProfileInputActivity.startActivityForResult(
            requireContext(),
            subscribeAction = { viewModel.refreshUser() },
        )
    }

    private fun changeGender(
        gender: Gender,
        completeListener: (Gender) -> Unit
    ) {
        val choices = resources.getStringArray(R.array.gender_array)
        val index = Gender.values().indexOf(gender)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.gender)
            .setPositiveButton(R.string.save) { dialog: DialogInterface, _: Int ->
                val checkedItemPosition =
                    (dialog as AlertDialog).listView.checkedItemPosition
                if (checkedItemPosition != AdapterView.INVALID_POSITION) {
                    val selectedGender = Gender.values().get(checkedItemPosition)
                    completeListener(selectedGender)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .setSingleChoiceItems(choices, index, null)
            .show()
    }

    private fun changeBirthYear(
        birthYear: Int,
        completeListener: (Int) -> Unit
    ) {
        val binding =
            LayoutEditBirthYearBinding.inflate(LayoutInflater.from(requireContext())).apply {
                etBirthYear.setText(birthYear.toString())
            }
        MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val newBirthYear = binding.etBirthYear.text.toString().toInt()
                completeListener(newBirthYear)

            }
            .setNegativeButton(R.string.cancel, null)
            .show()

    }

    private fun changeSleepTime(
        sleepTime: LocalTime,
        completeListener: (LocalTime) -> Unit
    ) {
        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(sleepTime.hour)
            .setMinute(sleepTime.minute)
            .build()

        materialTimePicker.show(parentFragmentManager, "fragment_tag")

        materialTimePicker.addOnPositiveButtonClickListener {
            val newHour = materialTimePicker.hour
            val newMinute = materialTimePicker.minute
            val selectedTime = LocalTime.of(newHour, newMinute)
            completeListener(selectedTime)
        }
    }

    private fun cancelSleepTimeNotification() {
        SleepTimeNotificationWorker.cancel(requireContext())
    }

    private fun setupSleepTimeNotification(sleepTime: LocalTime) {
        SleepTimeNotificationWorker.enqueue(requireContext(), sleepTime)
    }

    private fun showGuideDialog() {
        GuideDialogFragment.show(this)
    }

    private fun changeDeviceName(
        deviceName: String,
        completeListener: (String) -> Unit
    ) {
        val binding =
            LayoutEditDeviceNameBinding.inflate(LayoutInflater.from(requireContext())).apply {
                etDeviceName.setText(deviceName)
            }
        MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val newDeviceName = binding.etDeviceName.text.toString()
                completeListener(newDeviceName)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
