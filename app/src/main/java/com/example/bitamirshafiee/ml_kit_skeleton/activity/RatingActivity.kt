package com.example.bitamirshafiee.ml_kit_skeleton.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.example.bitamirshafiee.ml_kit_skeleton.R
import com.example.bitamirshafiee.ml_kit_skeleton.models.Hobbies
import com.example.bitamirshafiee.ml_kit_skeleton.util.PrefrenceManager
/*
RatingActivity allows the user to set his feelings on the SeekBar
and presses on the button to proceed to upload/take his photo
through the FaceAnalysisActivity.
 */
class RatingActivity : AppCompatActivity() {

    val TAG: String = RatingActivity::class.java.simpleName

    private var sbRate: SeekBar? = null
    private var tvMessage: TextView? = null
    private var bNext: Button? = null

    private var rate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        initViews()

        initActions()

    }

    private fun initViews() {
        sbRate = findViewById(R.id.sb_rate)
        tvMessage = findViewById(R.id.tv_message)
        bNext = findViewById(R.id.b_next)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }
//initActions listens to a change in SeekBar and to a click in
// the button then uses the preference manager to
// save the emotion determined through the seek bar
// and then goes to FaceAnalysisActivity.
    private fun initActions() {

        sbRate?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setFeeling(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        bNext!!.setOnClickListener {
            PrefrenceManager(this@RatingActivity).rate = rate
            startActivity(Intent(this@RatingActivity, FaceAnalysisActivity::class.java))
            finish()
        }
    }

    // changes the text of the text view according to the user’s entry.
    private fun setFeeling(percentage: Int){

        rate = percentage / 10

        when (rate) {
            1 -> tvMessage!!.setText(getString(R.string.label_rate_1))
            2 -> tvMessage!!.setText(getString(R.string.label_rate_2))
            3 -> tvMessage!!.setText(getString(R.string.label_rate_3))
            4 -> tvMessage!!.setText(getString(R.string.label_rate_4))
            5 -> tvMessage!!.setText(getString(R.string.label_rate_5))
            6 -> tvMessage!!.setText(getString(R.string.label_rate_6))
            7 -> tvMessage!!.setText(getString(R.string.label_rate_7))
            8 -> tvMessage!!.setText(getString(R.string.label_rate_8))
            9 -> tvMessage!!.setText(getString(R.string.label_rate_9))
            10 -> tvMessage!!.setText(getString(R.string.label_rate_10))
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this@RatingActivity, AnalyticsActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this@RatingActivity, AnalyticsActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}