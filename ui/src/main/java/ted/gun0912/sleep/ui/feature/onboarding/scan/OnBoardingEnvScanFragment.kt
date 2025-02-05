package ted.gun0912.sleep.ui.feature.onboarding.scan

import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import ted.gun0912.sleep.common.util.Constant
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.common.util.loge
import ted.gun0912.sleep.model.BluetoothDevice
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.template.FragmentTemplate
import ted.gun0912.sleep.ui.databinding.FragmentOnboardingScanBinding
import ted.gun0912.sleep.ui.extension.findListener
import ted.gun0912.sleep.ui.feature.onboarding.BaseOnBoardingFragment
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingListener
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingStep

@AndroidEntryPoint
class OnBoardingEnvScanFragment :
    BaseOnBoardingFragment<FragmentOnboardingScanBinding, OnBoardingScanViewModel, OnBoardingScanViewModel.Event>(
        R.layout.fragment_onboarding_scan, OnBoardingStep.SCAN_ENV
    ) {

    override val viewModel: OnBoardingScanViewModel by viewModels()

    private val scanner = BluetoothLeScannerCompat.getScanner()

    private val scanCallback = createScanCallback()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDescription.text = getString(R.string.onboarding_env_scan_text)
        binding.pulsator.start()
    }

    private fun scanDevice() {
        val settings: ScanSettings = ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(1000)
            .setUseHardwareBatchingIfSupported(false)
            .build()

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid.fromString(Constant.ENV_RX_SERVICE_UUID_TEXT))
            .build()
        scanner.startScan(listOf(scanFilter), settings, scanCallback)
    }

    override fun handleEvent(event: OnBoardingScanViewModel.Event) = when (event) {
        is OnBoardingScanViewModel.Event.SelectDevice -> selectComplete(event.bluetoothDevice)
    }

    private fun createScanCallback(): ScanCallback = object : ScanCallback() {
        override fun onBatchScanResults(results: List<ScanResult>) {
            viewModel.setScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            loge("errorCode: $errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            logd("$callbackType : $result")
        }
    }

    private fun selectComplete(bluetoothDevice: BluetoothDevice) {
        findListener<OnBoardingListener>()?.onEnvDeviceSelected(bluetoothDevice)
    }

    override fun onStop() {
        scanner.stopScan(scanCallback)
        super.onStop()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            scanDevice()
        } else {
            scanner.stopScan(scanCallback)
        }
    }

    companion object : FragmentTemplate<OnBoardingEnvScanFragment>()
}
