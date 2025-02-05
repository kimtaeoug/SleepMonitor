package ted.gun0912.sleep.ui.feature.onboarding

import kotlinx.serialization.Serializable

@Serializable
enum class OnBoardingStep(val position: Int) {
    INTRO(0), VALIDATION(1), PERMISSION(2), SCAN_SLEEP(3), SCAN_ENV(4);

    companion object {
        fun find(position: Int) = values().find { it.position == position }
    }
}
