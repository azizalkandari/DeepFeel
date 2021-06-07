package com.example.bitamirshafiee.ml_kit_skeleton.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.bitamirshafiee.ml_kit_skeleton.R
import com.example.bitamirshafiee.ml_kit_skeleton.models.Hobbies
import com.example.bitamirshafiee.ml_kit_skeleton.util.PrefrenceManager

/*
This class sets all the texts of the EditTexts
according to the users' entry and saves them for
future display through the PreferanceManager
 */

class HobbiesActivity : AppCompatActivity() {

    private val TAG: String = HobbiesActivity::class.java.simpleName

    private var etHobby1: EditText? = null
    private var etHobby2: EditText? = null
    private var etHobby3: EditText? = null
    private var bDone: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobbies)

        initViews()

        initActions()

        initData()

    }

    private fun initViews() {
        etHobby1 = findViewById(R.id.et_hobby_1)
        etHobby2 = findViewById(R.id.et_hobby_2)
        etHobby3 = findViewById(R.id.et_hobby_3)
        bDone = findViewById(R.id.b_done)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initActions() {
        bDone!!.setOnClickListener {
            val hobby1 = etHobby1!!.text.toString().trim()
            val hobby2 = etHobby2!!.text.toString().trim()
            val hobby3 = etHobby3!!.text.toString().trim()
            var hobbies = Hobbies()
            hobbies.hobby1 = hobby1
            hobbies.hobby2 = hobby2
            hobbies.hobby3 = hobby3

            PrefrenceManager(this@HobbiesActivity).hobbies = hobbies
            startActivity(Intent(this@HobbiesActivity, AnalyticsActivity::class.java))
            finish()
        }
    }

    private fun initData() {

        val hobbies: Hobbies = PrefrenceManager(this@HobbiesActivity).hobbies!!
        etHobby1!!.setText(hobbies.hobby1)
        etHobby2!!.setText(hobbies.hobby2)
        etHobby3!!.setText(hobbies.hobby3)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@HobbiesActivity, AnalyticsActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this@HobbiesActivity, AnalyticsActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}