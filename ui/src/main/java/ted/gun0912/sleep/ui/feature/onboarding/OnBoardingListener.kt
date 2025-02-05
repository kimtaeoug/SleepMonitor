package ted.gun0912.sleep.ui.feature.onboarding

import ted.gun0912.sleep.model.BluetoothDevice

interface OnBoardingListener {
    fun showNextStep(currentStep: OnBoardingStep)

    fun onSleepDeviceSelected(bluetoothDevice: BluetoothDevice)

    fun onEnvDeviceSelected(bluetoothDevice: BluetoothDevice)
}
