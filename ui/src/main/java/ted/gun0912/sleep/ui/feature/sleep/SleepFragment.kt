package ted.gun0912.sleep.ui.feature.sleep

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.model.VitalSensorInfo
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.databinding.FragmentSleepBinding
import ted.gun0912.sleep.ui.extension.play
import ted.gun0912.sleep.ui.feature.main.BaseTabFragment
import ted.gun0912.sleep.ui.feature.measure.MeasureFragment
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingActivity
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingStep
import ted.gun0912.sleep.ui.feature.sleep.SleepViewModel.Event
import ted.gun0912.sleep.ui.feature.sleep.dialog.ChargePhoneNoticeDialogFragment
import ted.gun0912.sleep.ui.permission.PermissionUtil

@AndroidEntryPoint
class SleepFragment :
    BaseTabFragment<FragmentSleepBinding, SleepViewModel, Event>(R.layout.fragment_sleep),
    ChargePhoneNoticeDialogFragment.StartSleepListener,
    MeasureFragment.MeasureFinishListener {

    override val viewModel: SleepViewModel by viewModels()

    private val chartRespiration by lazy { binding.layoutSleepChart.chartRespiration }
    private val chartHeart by lazy { binding.layoutSleepChart.chartHeart }
    val isSleepStarted: Boolean
        get() = binding.containerMeasure.isVisible

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logd("")
        setupListener()
        checkPermission()
        setupChart()
        //InitCountDown()
        binding.exoPlayerView.play(lifecycle, R.string.video_onboarding)
//        binding.exoPlayerView.play(lifecycle, R.string.video_onboarding) //exoPlayer enable YJH0706
        viewModel.vitalSensorInfo.observe(::addBleData)
        viewModel.lastAlarmTime.observe { lastAlarmTime ->
            logd("lastAlarmTime: $lastAlarmTime")
            binding.timePicker.apply {
                hour = lastAlarmTime.hour
                minute = lastAlarmTime.minute
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListener() {
        binding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            viewModel.setAlarmTime(hourOfDay, minute)
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            viewModel.updateVitalInfo()
        }
    }

    private fun checkPermission() = lifecycleScope.launch {
        val hasPermission = PermissionUtil.hasPermission(requireContext())
        if (hasPermission) {
            return@launch
        }

        val isGranted = PermissionUtil.requestPermission(requireActivity())
        if (isGranted) {
            viewModel.launch()
        } else {
            OnBoardingActivity.startActivity(requireContext(), OnBoardingStep.PERMISSION)
        }
    }

    override fun onStart() {
        logd("SleepFragment Started")
        super.onStart()
        viewModel.launch()
    }

    override fun onStop() {
        logd("")
        viewModel.disconnect()
//        viewModel.disconnectEnvDevice()
        super.onStop()
    }

    private fun InitCountDown(){

        val countDownTimer  = object : CountDownTimer(20000,1000){//60000
            override fun onTick(p0: Long) {
                Log.d("TimerTest",p0.toString())
            }

            override fun onFinish() {
                viewModel.isDeviceInit = true
            }
        }

        countDownTimer.start()
    }

    override fun handleEvent(event: Event) = when (event) {
        Event.ShowChargePhone -> showChargePhoneNotice()
        Event.StartSleep -> startSleep()
    }

    private var measureFragment: MeasureFragment? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun startSleep() {
        viewModel.disconnect()
        viewModel.setMeasureShowing(true)
        binding.containerMeasure.isVisible = true
        measureFragment = MeasureFragment.show(
            childFragmentManager,
            R.id.container_measure,
            viewModel.sleepDeviceAddress,
            viewModel.envDeviceAddress,
            viewModel.alarmTime
        )
    }
//    private val logger: LoggerUtil = LoggerUtil().getInstance()

    private val logger: LoggerUtil = LoggerUtil("SLEEP_FRAGMENT").getInstance()
    override fun onMeasureFinish() {
        logger.debug("onMeasureFinish is invoked")
        measureFragment?.let {
            childFragmentManager.beginTransaction().remove(it).commit();
        }
        binding.containerMeasure.isVisible = false
        viewModel.setMeasureShowing(false)
        logger.debug("viewModel Measure end")
        //viewModel.launch() //YJH0711 측정이 완료된이후 재연결을함
    }

    private fun showChargePhoneNotice() {
        ChargePhoneNoticeDialogFragment.show(this)
    }

    private fun setupChart() {
        chartHeart.setup(ContextCompat.getColor(requireContext(), R.color.heart), 50, 120)
        chartRespiration.setup(Color.WHITE, 10, 30)
    }

    private fun LineChart.setup(@ColorInt lineColor: Int, min: Int, max: Int) {
        setTouchEnabled(false)
        description.isEnabled = false
        setDrawGridBackground(false)
        setBackgroundColor(Color.TRANSPARENT)
        setViewPortOffsets(0f, 0f, 0f, 0f)
        legend.isEnabled = false
        xAxis.apply {
            isEnabled = false
            setAvoidFirstLastClipping(true)
            setDrawGridLines(false)
        }
        axisRight.isEnabled = false

        axisLeft.apply {
            setDrawGridLines(false)
            textColor = resources.getColor(ted.gun0912.sleep.design.R.color.white_60)
            isEnabled = false
            axisMinimum = min.toFloat()
            axisMaximum = max.toFloat()
        }


        val emptyData = LineData().apply {
            addDataSet(createSet(lineColor))
        }
        data = emptyData
    }

    private fun createSet(@ColorInt lineColor: Int): LineDataSet =
        LineDataSet(null, "BleResult").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            setDrawFilled(true)
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = 1.8f
            color = lineColor
            fillColor = lineColor
        }

    private fun addBleData(vitalSensorInfo: VitalSensorInfo) {
        if (vitalSensorInfo.state.isNotStable && !viewModel.isDeviceInit) {
            return
        }

        chartHeart.addBleData(vitalSensorInfo.heartRate)
        chartRespiration.addBleData(vitalSensorInfo.respirationRate)

    }

    private fun LineChart.addBleData(value: Int) {
        if (value < 0) {
            return
        }
        val data = data ?: return
        val set = data.getDataSetByIndex(0) ?: return
        data.addEntry(Entry(set.entryCount.toFloat(), value.toFloat()), 0)
        data.notifyDataChanged()

        notifyDataSetChanged()
        setVisibleXRangeMaximum(15f)
        moveViewToX(data.entryCount.toFloat())
    }

    companion object {

        fun newInstance(sleepDeviceAddress: String, envDeviceAddress: String) = SleepFragment().apply {
            arguments = bundleOf(
                SleepViewModel.KEY_SLEEP_DEVICE_ADDRESS to sleepDeviceAddress,
                SleepViewModel.KEY_ENV_DEVICE_ADDRESS to envDeviceAddress
            )
        }
    }
}
