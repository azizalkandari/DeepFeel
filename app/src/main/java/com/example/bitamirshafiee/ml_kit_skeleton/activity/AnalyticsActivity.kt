package com.example.bitamirshafiee.ml_kit_skeleton.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bitamirshafiee.ml_kit_skeleton.R
import com.example.bitamirshafiee.ml_kit_skeleton.models.Hobbies
import com.example.bitamirshafiee.ml_kit_skeleton.models.Report
import com.example.bitamirshafiee.ml_kit_skeleton.util.PrefrenceManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_analytics.*
import java.io.File

/*
By
Abdulaziz AlKandry - S00012791
Ahmad Younis – S00046274
Feraas El Sabaa – S00046163

 */

/*
AnalyticsActivity class displays the results of happiness, average happiness,
and if eyes are open. Also displays a chart of history of emotions
and a recommended hobby.
 */

class AnalyticsActivity : AppCompatActivity() {

    private val TAG: String = AnalyticsActivity::class.java.simpleName

    private var bHobby: Button? = null
    private var bAddLog: Button? = null

    private var tvAverageEmotion: TextView? = null
    private var tvEyeOpening: TextView? = null
    private var tvHappieness: TextView? = null
    private var tvRandom: TextView? = null
    private var bLocation: Button? = null
    private var ivThumb: ImageView? = null
    private var barChart: BarChart? = null

    private var videoView : VideoView? = null

    private var cpbEmotion: CircularProgressBar? = null
    private var cpbEye: CircularProgressBar? = null
    private var cpbHappiness: CircularProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        initViews()

        initActions()

        initData()

    }


    private fun initViews() {

        bHobby = findViewById(R.id.b_hobby);
        bAddLog = findViewById(R.id.b_add_log);

        tvAverageEmotion = findViewById(R.id.tv_average_emotion)
        tvEyeOpening = findViewById(R.id.tv_eye_opening)
        tvHappieness = findViewById(R.id.tv_happieness)
        ivThumb = findViewById(R.id.iv_thumb)
        bLocation = findViewById(R.id.b_location)

        barChart = findViewById(R.id.barChart)

        videoView = findViewById(R.id.videoView)

        cpbEmotion = findViewById(R.id.cpbEmotion)
        cpbEye = findViewById(R.id.cpbEye)
        cpbHappiness = findViewById(R.id.cpbHappiness)

        tvRandom = findViewById(R.id.tv_random)

    }

    private fun initActions() {
        bHobby!!.setOnClickListener {
            startActivity(Intent(this@AnalyticsActivity, HobbiesActivity::class.java))
            finish()
        }
        bAddLog!!.setOnClickListener {
            startActivity(Intent(this@AnalyticsActivity, RatingActivity::class.java))
            finish()
        }
        bLocation!!.setOnClickListener {
            startActivity(Intent(this@AnalyticsActivity, LocationActivity::class.java))
            finish()
        }
    }


    // get the data for the eyes opening and happiness
    private fun initData() {

        val report = PrefrenceManager(this@AnalyticsActivity).report!!

        if(report.path != null) {
            val file = File(report.path!!)

            if(file.exists()) {

                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                ivThumb!!.setImageBitmap(bitmap);
            }
        }

        tvEyeOpening!!.setText("${report.eye} %")
        tvHappieness!!.setText("${report.happienesa} %")

        var averageTotal = 0

        val list = PrefrenceManager(this@AnalyticsActivity).history!!

        var pEmotion = 0.0f
        if(list.count() > 0) {

            for (report: Report in list) {
                averageTotal += report.emotion!!.toInt()
            }

            pEmotion = (averageTotal / list.count()).toFloat() * 10f
            tvAverageEmotion!!.setText("${pEmotion} %")

            setBarChart(list)
        }

        cpbEmotion!!.progress = pEmotion

        //val mediaController = MediaController(this)
        //videoView!!.setMediaController(mediaController)
        //mVideoView!!.setVideoPath("file:///android_asset/animation.mp4")
        //videoView!!.setVideoURI(Uri.parse("file:///android_asset/animation.mp4"))
        videoView!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.animation ))
        videoView!!.start()

        var pEye = 0.0f
        if(report.eye != null) {
             pEye = report.eye!!.toFloat()
        }
        cpbEye!!.progress = pEye

        var pHappieness = 0.0f
        if(report.happienesa != null) {
            pHappieness = report.happienesa!!.toFloat()
        }
        cpbHappiness!!.progress = pHappieness

        setRandomQuoute()
    }


    // display the emotions rating chart in Analytics Activity.
    private fun setBarChart(list: ArrayList<Report>) {
        val entries = ArrayList<BarEntry>()

        var i = 0.0f
        for (report: Report in list) {
            entries.add(BarEntry(i, report.emotion!!.toFloat()))
            i = i + 1.0f
        }

        val barDataSet = BarDataSet(entries, "Rating")

        val data = BarData(barDataSet)
        barChart!!.data = data // set the data and list of lables into chart

        barDataSet.color = resources.getColor(R.color.light_red)

        //barChart!!.setVisibleXRange(0f, 10f);


    }


    // randomly display one of the hobbies the user has previously entered
    private fun setRandomQuoute() {

        val hobbies: Hobbies = PrefrenceManager(this@AnalyticsActivity).hobbies!!
        val number = (1..3).shuffled().last()

        Log.e(TAG, "setRandomQuoute: " + number )
        when (number) {
            1 -> tv_random!!.setText(hobbies.hobby1)
            2 -> tv_random!!.setText(hobbies.hobby2)
            3 -> tv_random!!.setText(hobbies.hobby3)
        }

    }




}