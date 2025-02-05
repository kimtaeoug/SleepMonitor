package ted.gun0912.sleep.ui.feature.profile

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.sleep.model.Gender
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.ActivityTransitionType
import ted.gun0912.sleep.ui.base.BaseActivity
import ted.gun0912.sleep.ui.base.template.ActivityTemplate
import ted.gun0912.sleep.ui.databinding.ActivityProfileInputBinding
import ted.gun0912.sleep.ui.extension.play
import ted.gun0912.sleep.ui.feature.profile.ProfileInputViewModel.Event
import java.time.LocalTime

@AndroidEntryPoint
class ProfileInputActivity :
    BaseActivity<ActivityProfileInputBinding, ProfileInputViewModel, Event>(
        R.layout.activity_profile_input
    ) {
    override val viewModel: ProfileInputViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTransition = ActivityTransitionType.FADE
        viewModel.genderGetter = { getGender(it) }
        binding.exoPlayerView.play(lifecycle, R.string.video_onboarding)
//        binding.exoPlayerView.play(lifecycle, R.string.video_onboarding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleEvent(event: Event) = when (event) {
        is Event.SelectTime -> showTimePicker(event.selectedTime)
    }

    private fun getGender(genderText: String): Gender {
        val genderArray = resources.getStringArray(R.array.gender_array)
        val index = genderArray.indexOf(genderText)
        return Gender.values()[index]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTimePicker(selectedTime: LocalTime) {

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(selectedTime.hour)
            .setMinute(selectedTime.minute)
            .build()

        materialTimePicker.show(supportFragmentManager, "fragment_tag")

        materialTimePicker.addOnPositiveButtonClickListener {
            val newHour = materialTimePicker.hour
            val newMinute = materialTimePicker.minute
            val selectedTime = LocalTime.of(newHour, newMinute)
            viewModel.updateSelectedTime(selectedTime)
        }
    }

    override fun onBackPressed() = Unit

    companion object : ActivityTemplate<ProfileInputActivity>()
}
