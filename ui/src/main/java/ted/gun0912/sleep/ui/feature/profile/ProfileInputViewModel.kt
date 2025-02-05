package ted.gun0912.sleep.ui.feature.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import ted.gun0912.sleep.domain.usecase.UpdateUserUseCase
import ted.gun0912.sleep.model.Gender
import ted.gun0912.sleep.model.User
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.extension.requireValue
import ted.gun0912.sleep.ui.feature.profile.ProfileInputViewModel.Event
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ProfileInputViewModel @Inject constructor(
    private val updateUserUseCase: UpdateUserUseCase,
) : BaseViewModel<Event>() {

    var genderGetter: ((String) -> Gender)? = null

    val genderText = MutableStateFlow<String?>(null)
    private val gender = genderText.map { genderGetter?.invoke(it) }
    val isGenderInputted =
        gender.mapLatest { it != null }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val birthYearText = MutableStateFlow<String?>(null)
    private val birthYear = birthYearText.map { if (it.isNotBlank()) it.toInt() else 0 }
    val isBirthYearInputted = birthYear.map { it in 1900..2022 }

    private val selectedTime = MutableStateFlow<LocalTime?>(null)
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedTimeText = selectedTime.map { DateTimeFormatter.ofPattern("a hh:mm").format(it) }

    val isInputCompleted =
        combine(gender, birthYear, selectedTime) { (gender, birthYear, selectedTime) ->
            gender != null && birthYear != null && selectedTime != null
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        launch {
            isBirthYearInputted
                .filter { it == true }
                .collect { hideKeyboard() }
        }

    }

    fun updateSelectedTime(time: LocalTime) {
        selectedTime.value = time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectTime() {
        event(Event.SelectTime(selectedTime.value ?: LocalTime.now()))
    }

    fun complete() = launch {
        Log.d("LOG", "hey!")
        val user = User(
            gender.requireValue,
            birthYear.requireValue,
            selectedTime.requireValue,
            true
        )
        updateUserUseCase(user).await()
        finish(isComplete = true)
    }


    sealed interface Event : ViewEvent {
        data class SelectTime(val selectedTime: LocalTime) : Event
    }
}
