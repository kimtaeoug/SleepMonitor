package ted.gun0912.sleep.ui.feature.measure

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.common.primitives.UnsignedBytes.toInt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ted.gun0912.sleep.model.EnvSensorInfo
import ted.gun0912.sleep.model.VitalSensorInfo
import ted.gun0912.sleep.model.extension.toSecond
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.BaseFragment
import ted.gun0912.sleep.ui.base.template.FragmentTemplate
import ted.gun0912.sleep.ui.databinding.FragmentMeasureBinding
import ted.gun0912.sleep.ui.extension.findListener
import ted.gun0912.sleep.ui.extension.play
import ted.gun0912.sleep.ui.feature.alarm.AlarmReceiver
import java.time.LocalTime

@AndroidEntryPoint
class MeasureFragment :
    BaseFragment<FragmentMeasureBinding, MeasureViewModel, MeasureViewModel.Event>(
        R.layout.fragment_measure
    ) {
    override val viewModel: MeasureViewModel by viewModels()

    //envSensorInfo
    private val chartLayout by lazy { binding.layoutSleepChart.layoutChart }
    private val chartRespiration by lazy { binding.layoutSleepChart.chartRespiration }
    private val chartHeart by lazy { binding.layoutSleepChart.chartHeart }

    private val alarmManager: AlarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private lateinit var alarmIntent: PendingIntent
//    private val logger = LoggerUtil().getInstance()
    private val logger = LoggerUtil("MEASURE_FRAGMENT").getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logger.debug("MeasureFragment is created")
        super.onViewCreated(view, savedInstanceState)
        /*binding.swipeBackLayout.apply {
            setDragEdge(SwipeBackLayout.DragEdge.BOTTOM)
            setOnSwipeBackListener { _, fractionScreen ->
                viewModel.canShowUI = fractionScreen == 0f
            }
            setOnFinishListener { finish() }
        }*/
        binding.exoPlayerView.play(lifecycle, R.string.video_onboarding)
//        viewModel.envSensorInfo.observe(::addBleData)
        viewModel.vitalSensorInfo.observe(::addBleData)
    
        viewModel.launch()
        setupAlarm()
        setupFinishButton()
        setupChart()


        chartLayout.visibility = View.INVISIBLE
        viewModel.isDeviceInit = false
        InitCountDown()
        viewModel.createSleepRawFile(getStorageFolderName())
        logger.debug("createSleepRawFile")



        viewModel.isRecordComplete.observe { isComplete ->
            logger.debug("isComplete : $isComplete")
            if (isComplete) {
                logger.debug("isRecordComplete is true")
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmManager.cancel(alarmIntent)
    }

    private fun setupFinishButton() {//YJH0512 추가함
        binding.progressBar.max = FINISH_LONG_CLICK_DURATION
        binding.viewFinish.setOnLongClickListener {
            object : CountDownTimer(FINISH_LONG_CLICK_DURATION.toLong(), 1) {
                override fun onTick(millisUntilFinished: Long) {
                    if (binding.viewFinish.isPressed) {
                        binding.progressBar.progress =
                            FINISH_LONG_CLICK_DURATION - millisUntilFinished.toInt()
                    } else {
                        binding.progressBar.progress = 0
                        cancel()
                    }
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onFinish() {
                    logger.debug("binding view finish onFinish is invoked")
                    finish()
                }
            }.start()
            logger.debug("Setup Finish Button")
            true
        }
    }

    override fun handleEvent(event: MeasureViewModel.Event) = Unit


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAlarm() {
        val alarmDateTime = viewModel.alarmDateTime

        val alarmReceiverIntent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.KEY_ALARM_TIME, alarmDateTime.toLocalTime())
        }

        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        alarmIntent = PendingIntent.getBroadcast(context, 0, alarmReceiverIntent, flags)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC, // RTC_WAKEUP YJH0705
            alarmDateTime.toSecond() * 1000,
            alarmIntent
        )
        logger.debug("Start Alarm")
    }

    private fun InitCountDown() {
        //60000 : 1분
        //1000 : 1초
        val countDownTimer = object : CountDownTimer(60000, 1000) {
            //60000
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                chartLayout.visibility = View.VISIBLE
                viewModel.isDeviceInit = true
            }
        }

        countDownTimer.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun finish() {
        lifecycleScope.launch {
            logger.debug("finish launch is invoked")
            alarmManager.cancel(alarmIntent)
            logger.debug("alarmManager is canceled")
            //todo
            viewModel.createSleepRawFile(getStorageFolderName())
            logger.debug("createSleepRawFile")
            viewModel.determineSaveSleepRecord()
            logger.debug("alarmManager is canceled")
            //viewModel.disconnect() //YJH 0512
            findListener<MeasureFinishListener>()?.onMeasureFinish()
        }
    }

    // storage 디렉토리 경로 가져오기
    private fun getStorageFolderName(): String? {
        return activity?.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
    }
    private fun setupChart() {
        chartHeart.setup(ContextCompat.getColor(requireContext(), R.color.heart), 50, 120)
        chartRespiration.setup(Color.WHITE, 10, 30)
    }

//    private fun setupChart() {
//        chartHeart.setup(ContextCompat.getColor(requireContext(), R.color.heart), 100, 100)
//        chartRespiration.setup(Color.WHITE, 0, 1000)
//    }

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

    private var previousVitalSensorInfo: VitalSensorInfo? = null


    private fun addBleData(vitalSensorInfo: VitalSensorInfo) {
        if (vitalSensorInfo.state.isNotStable && !viewModel.isDeviceInit) {
            return
        }
        logger.debug("heartRate : ${vitalSensorInfo.heartRate}")
        logger.debug("respirationRate : ${vitalSensorInfo.respirationRate}")

        chartHeart.addBleData(vitalSensorInfo.heartRate)
        chartRespiration.addBleData(vitalSensorInfo.respirationRate)

        previousVitalSensorInfo = vitalSensorInfo
    }

    private fun LineChart.addBleData(value: Int?) {
        if (value == null) {
            return
        }
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


    interface MeasureFinishListener {
        fun onMeasureFinish()
    }

    companion object : FragmentTemplate<MeasureFragment>() {
        private const val FINISH_LONG_CLICK_DURATION = 800

        fun show(
            fragmentManager: FragmentManager,
            @IdRes containerViewId: Int,
            sleepDeviceAddress: String,
            envDeviceAddress: String,
            alarmTime: LocalTime,
        ) =
            MeasureFragment().apply {
                arguments = bundleOf(
                    MeasureViewModel.KEY_SLEEP_DEVICE_ADDRESS to sleepDeviceAddress,
                    MeasureViewModel.KEY_ENV_DEVICE_ADDRESS to envDeviceAddress,
                    MeasureViewModel.KEY_ALARM_TIME to alarmTime,
                )
                fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_from_top, R.anim.slide_down_from_outside_5)
                    .replace(containerViewId, this, tag)
                    .commitAllowingStateLoss()
            }
    }
}


//package ted.gun0912.sleep.ui.feature.measure
//
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.os.Build
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.os.Environment
//import android.util.Log
//import android.view.View
//import androidx.annotation.ColorInt
//import androidx.annotation.IdRes
//import androidx.annotation.RequiresApi
//import androidx.core.content.ContextCompat
//import androidx.core.os.bundleOf
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.google.common.primitives.UnsignedBytes.toInt
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.async
//import kotlinx.coroutines.launch
//import ted.gun0912.sleep.model.EnvSensorInfo
//import ted.gun0912.sleep.model.VitalSensorInfo
//import ted.gun0912.sleep.model.extension.toSecond
//import ted.gun0912.sleep.ui.LoggerUtil
//import ted.gun0912.sleep.ui.R
//import ted.gun0912.sleep.ui.base.BaseFragment
//import ted.gun0912.sleep.ui.base.template.FragmentTemplate
//import ted.gun0912.sleep.ui.databinding.FragmentMeasureBinding
//import ted.gun0912.sleep.ui.extension.findListener
//import ted.gun0912.sleep.ui.feature.alarm.AlarmReceiver
//import java.time.LocalTime
//
//@AndroidEntryPoint
//class MeasureFragment :
//    BaseFragment<FragmentMeasureBinding, MeasureViewModel, MeasureViewModel.Event>(
//        R.layout.fragment_measure
//    ) {
//    override val viewModel: MeasureViewModel by viewModels()
//
//    //envSensorInfo
//    private val chartLayout by lazy { binding.layoutSleepChart.layoutChart }
//    private val chartRespiration by lazy { binding.layoutSleepChart.chartRespiration }
//    private val chartHeart by lazy { binding.layoutSleepChart.chartHeart }
//
//    private val alarmManager: AlarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
//    private lateinit var alarmIntent: PendingIntent
//    private  val logger = LoggerUtil().getInstance()
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        logger.debug("MeasureFragment is created")
//        super.onViewCreated(view, savedInstanceState)
//        /*binding.swipeBackLayout.apply {
//            setDragEdge(SwipeBackLayout.DragEdge.BOTTOM)
//            setOnSwipeBackListener { _, fractionScreen ->
//                viewModel.canShowUI = fractionScreen == 0f
//            }
//            setOnFinishListener { finish() }
//        }*/
////        binding.exoPlayerView.play(lifecycle, R.string.video_onboarding)
//        viewModel.envSensorInfo.observe(::addBleData)
////        viewModel.vitalSensorInfo.observe(::addBleData)
//
//        viewModel.launch()
//        setupAlarm()
//        setupFinishButton()
//        setupChart()
//
//
//        chartLayout.visibility = View.INVISIBLE
//        viewModel.isDeviceInit = false
//        InitCountDown()
//        viewModel.createSleepRawFile(getStorageFolderName())
//        logger.debug("createSleepRawFile")
//
//
//
//        viewModel.isRecordComplete.observe { isComplete ->
//            logger.debug("isComplete : $isComplete")
//            if (isComplete) {
//                logger.debug("isRecordComplete is true")
//                finish()
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        alarmManager.cancel(alarmIntent)
//    }
//
//    private fun setupFinishButton() {//YJH0512 추가함
//        binding.progressBar.max = FINISH_LONG_CLICK_DURATION
//        binding.viewFinish.setOnLongClickListener {
//            object : CountDownTimer(FINISH_LONG_CLICK_DURATION.toLong(), 1) {
//                override fun onTick(millisUntilFinished: Long) {
//                    if (binding.viewFinish.isPressed) {
//                        binding.progressBar.progress =
//                            FINISH_LONG_CLICK_DURATION - millisUntilFinished.toInt()
//                    } else {
//                        binding.progressBar.progress = 0
//                        cancel()
//                    }
//                }
//
//                @RequiresApi(Build.VERSION_CODES.O)
//                override fun onFinish() {
//                    logger.debug("binding view finish onFinish is invoked")
//                    finish()
//                }
//            }.start()
//            logger.debug("Setup Finish Button")
//            true
//        }
//    }
//
//    override fun handleEvent(event: MeasureViewModel.Event) = Unit
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun setupAlarm() {
//        val alarmDateTime = viewModel.alarmDateTime
//
//        val alarmReceiverIntent = Intent(requireContext(), AlarmReceiver::class.java).apply {
//            putExtra(AlarmReceiver.KEY_ALARM_TIME, alarmDateTime.toLocalTime())
//        }
//
//        val flags =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            } else {
//                PendingIntent.FLAG_UPDATE_CURRENT
//            }
//        alarmIntent = PendingIntent.getBroadcast(context, 0, alarmReceiverIntent, flags)
//
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC, // RTC_WAKEUP YJH0705
//            alarmDateTime.toSecond() * 1000,
//            alarmIntent
//        )
//        logger.debug("Start Alarm")
//    }
//
//    private fun InitCountDown() {
//        //60000 : 1분
//        //1000 : 1초
//        val countDownTimer = object : CountDownTimer(60000, 1000) {
//            //60000
//            override fun onTick(p0: Long) {
//            }
//
//            override fun onFinish() {
//                chartLayout.visibility = View.VISIBLE
//                viewModel.isDeviceInit = true
//            }
//        }
//
//        countDownTimer.start()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun finish() {
//        lifecycleScope.launch {
//            logger.debug("finish launch is invoked")
//            alarmManager.cancel(alarmIntent)
//            logger.debug("alarmManager is canceled")
//            //todo
////            viewModel.createSleepRawFile(getStorageFolderName())nm,n,
////            logger.debug("createSleepRawFile")
//            viewModel.determineSaveSleepRecord()
//            logger.debug("alarmManager is canceled")
//            //viewModel.disconnect() //YJH 0512
//            findListener<MeasureFinishListener>()?.onMeasureFinish()
//        }
//    }
//
//    // storage 디렉토리 경로 가져오기
//    private fun getStorageFolderName(): String? {
//        return activity?.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
//    }
//
//    private fun setupChart() {
//        chartHeart.setup(ContextCompat.getColor(requireContext(), R.color.heart), 100, 100)
//        chartRespiration.setup(Color.WHITE, 0, 1000)
//    }
//
//    private fun LineChart.setup(@ColorInt lineColor: Int, min: Int, max: Int) {
//        setTouchEnabled(false)
//        description.isEnabled = false
//        setDrawGridBackground(false)
//        setBackgroundColor(Color.TRANSPARENT)
//        setViewPortOffsets(0f, 0f, 0f, 0f)
//        legend.isEnabled = false
//        xAxis.apply {
//            isEnabled = false
//            setAvoidFirstLastClipping(true)
//            setDrawGridLines(false)
//        }
//        axisRight.isEnabled = false
//
//        axisLeft.apply {
//            setDrawGridLines(false)
//            textColor = resources.getColor(ted.gun0912.sleep.design.R.color.white_60)
//            isEnabled = false
//            axisMinimum = min.toFloat()
//            axisMaximum = max.toFloat()
//        }
//
//
//        val emptyData = LineData().apply {
//            addDataSet(createSet(lineColor))
//        }
//        data = emptyData
//    }
//
//    private fun createSet(@ColorInt lineColor: Int): LineDataSet =
//        LineDataSet(null, "BleResult").apply {
//            mode = LineDataSet.Mode.CUBIC_BEZIER
//            cubicIntensity = 0.2f
//            setDrawFilled(true)
//            setDrawCircles(false)
//            setDrawValues(false)
//            lineWidth = 1.8f
//            color = lineColor
//            fillColor = lineColor
//        }
//
////    private var previousVitalSensorInfo: VitalSensorInfo? = null
//
//    private var previousVitalSensorInfo: EnvSensorInfo? = null
//
////    //todo
////    //값이 달라서 그럼
//    private fun addBleData(envSensorInfo: EnvSensorInfo) {
//        logger.debug("addBleData start")
//        if(!viewModel.isDeviceInit){
//            return
//        }
//        logger.debug("envSensorInfo : $envSensorInfo")
//        logger.debug("temp : ${envSensorInfo.temp}")
//        logger.debug("co2 : ${envSensorInfo.co2}")
//        //온도
//    //    val integerValue = text.split(".")[0].toInt() // 소수점 앞 부분만 취급
//        var rawTemp : String = envSensorInfo.temp ?: ""
//        var tempValue : Int = 0
//        if(rawTemp.contains(".")){
//            tempValue = Integer.parseInt(rawTemp.split(".")[0])
//        }else{
//            tempValue = Integer.parseInt(rawTemp)
//        }
//        var rawCo2: String = envSensorInfo.co2 ?: ""
//        var co2Value : Int = 0
//        if (rawCo2.contains(".")){
//            co2Value = Integer.parseInt(rawCo2.split(".")[0])
//        }else{
//            co2Value = Integer.parseInt(rawCo2)
//        }
//        chartHeart.addBleData(tempValue)
//        chartRespiration.addBleData(co2Value)
//
//        previousVitalSensorInfo = envSensorInfo
//    }
////    private fun addBleData(vitalSensorInfo: VitalSensorInfo) {
////        if (vitalSensorInfo.state.isNotStable && !viewModel.isDeviceInit) {
////            return
////        }
////        logger.debug("heartRate : ${vitalSensorInfo.heartRate}")
////        logger.debug("respirationRate : ${vitalSensorInfo.respirationRate}")
////
////        chartHeart.addBleData(vitalSensorInfo.heartRate)
////        chartRespiration.addBleData(vitalSensorInfo.respirationRate,)
////
////        previousVitalSensorInfo = vitalSensorInfo
////    }
//
//    private fun LineChart.addBleData(value: Int?) {
//        if(value == null){
//            return
//        }
//        if(value < 0){
//            return
//        }
//        val data = data ?: return
//        val set = data.getDataSetByIndex(0) ?: return
//        data.addEntry(Entry(set.entryCount.toFloat(), value.toFloat()), 0)
//        data.notifyDataChanged()
//
//        notifyDataSetChanged()
//        setVisibleXRangeMaximum(15f)
//        moveViewToX(data.entryCount.toFloat())
//    }
//
//
//    interface MeasureFinishListener {
//        fun onMeasureFinish()
//    }
//
//    companion object : FragmentTemplate<MeasureFragment>() {
//        private const val FINISH_LONG_CLICK_DURATION = 800
//
//        fun show(
//            fragmentManager: FragmentManager,
//            @IdRes containerViewId: Int,
//            sleepDeviceAddress: String,
//            envDeviceAddress: String,
//            alarmTime: LocalTime,
//        ) =
//            MeasureFragment().apply {
//                arguments = bundleOf(
//                    MeasureViewModel.KEY_SLEEP_DEVICE_ADDRESS to sleepDeviceAddress,
//                    MeasureViewModel.KEY_ENV_DEVICE_ADDRESS to envDeviceAddress,
//                    MeasureViewModel.KEY_ALARM_TIME to alarmTime,
//                )
//                fragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_from_top, R.anim.slide_down_from_outside_5)
//                    .replace(containerViewId, this, tag)
//                    .commitAllowingStateLoss()
//            }
//    }
//}
//
