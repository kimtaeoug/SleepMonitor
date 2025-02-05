package ted.gun0912.sleep.ui.feature.onboarding.verification

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ted.gun0912.sleep.domain.usecase.SaveUserIdUseCase
import ted.gun0912.sleep.domain.usecase.ValidateAccountUseCase
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import javax.inject.Inject

@HiltViewModel
class OnBoardingValidationViewModel @Inject constructor(
    private val validateAccountUseCase: ValidateAccountUseCase,
    private val saveUserIdUseCase: SaveUserIdUseCase
) : BaseViewModel<OnBoardingValidationViewModel.Event>() {
    private val log : LoggerUtil = LoggerUtil("OnBoardingValidationViewModel").getInstance()
    private val _isUser: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUser = _isUser

    fun validateAccount(userId: String) {
        log.error("userId : $userId")
        viewModelScope.launch {
//            validateAccountUseCase.invoke(userId)
//            saveUserIdUseCase(userId).await()
//            _isUser.value = true


            validateAccountUseCase.invoke(userId).collectDataResource(
                onSuccess = {
                    saveUserIdUseCase(userId).await()
                    _isUser.value = true
                },
                onError = {
                    log.error("hello!")
                }
            )
        }
    }

    sealed interface Event : ViewEvent {
    }
}