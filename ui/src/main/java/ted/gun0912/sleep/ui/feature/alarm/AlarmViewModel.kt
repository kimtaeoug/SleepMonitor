package ted.gun0912.sleep.ui.feature.alarm

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.extension.getNotNull
import ted.gun0912.sleep.ui.feature.alarm.AlarmViewModel.Event
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<Event>() {

    private val alarmTime = savedStateHandle.getNotNull<LocalTime>(KEY_ALARM_TIME)
    @RequiresApi(Build.VERSION_CODES.O)
    val alarmTimeText = DateTimeFormatter.ofPattern("a hh:mm").format(alarmTime)

    sealed interface Event : ViewEvent {

    }

    companion object {
        const val KEY_ALARM_TIME = "KEY_ALARM_TIME"
    }

}
