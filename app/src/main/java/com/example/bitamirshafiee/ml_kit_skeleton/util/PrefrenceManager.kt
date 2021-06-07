package com.example.bitamirshafiee.ml_kit_skeleton.util

import android.content.Context
import android.content.SharedPreferences
import com.example.bitamirshafiee.ml_kit_skeleton.models.Hobbies
import com.example.bitamirshafiee.ml_kit_skeleton.models.Report
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/*
This Class stores the data of entered
hobbies and history of reports and return them when needed.
 */
class PrefrenceManager internal constructor(private val mContext: Context) {

    var rate: Int?
        get() = Prefrence.readInteger(mContext, Prefrence.RATE, 0)
        set(rat) {
            if (rat != null) {
                Prefrence.writeInteger(mContext, Prefrence.RATE, rat)
            }
        }

    private var reportData: String
        private get() = Prefrence.readString(mContext, Prefrence.REPORT_DATA, "")
        private set(reportData) {
            Prefrence.writeString(mContext, Prefrence.REPORT_DATA, reportData)
        }

    private var hobbiesData: String
        private get() = Prefrence.readString(mContext, Prefrence.HOBBIES_DATA, "")
        private set(hobbiesData) {
            Prefrence.writeString(mContext, Prefrence.HOBBIES_DATA, hobbiesData)
        }

    private var historyData: String
        private get() = Prefrence.readString(mContext, Prefrence.HISTORY_DATA, "")
        private set(historyData) {
            Prefrence.writeString(mContext, Prefrence.HISTORY_DATA, historyData)
        }

    fun clear() {
        Prefrence.clear(mContext)
    }

    var history: ArrayList<Report>?
        get() {
            val historyData: String? = historyData
            return if (historyData!!.count() > 0) {
                Gson().fromJson<ArrayList<Report>>(
                    historyData,
                    object : TypeToken<ArrayList<Report?>?>() {}.type
                )
            } else ArrayList()
        }
        set(reports) {
            historyData = Gson().toJson(reports)
        }

    var hobbies: Hobbies?
        get() {
            val hobbiesData: String? = hobbiesData
            return if (hobbiesData!!.count() > 0) {
                Gson().fromJson(hobbiesData, Hobbies::class.java)
            } else Hobbies()
        }
        set(hobbies) {
            hobbiesData = Gson().toJson(hobbies)
        }

    var report: Report?
        get() {
            val reportData: String? = reportData
            return if (reportData!!.count() > 0) {
                Gson().fromJson(reportData, Report::class.java)
            } else Report()
        }
        set(report) {
            reportData = Gson().toJson(report)
        }

    object Prefrence {
        const val PREF_NAME = "210603_DeepFeel"
        const val RATE = "RATE"
        const val HOBBIES_DATA = "HOBBIES_DATA"
        const val HISTORY_DATA = "HISTORY_DATA"
        const val REPORT_DATA = "REPORT_DATA"

        fun writeBoolean(context: Context, key: String?, value: Boolean) {
            getEditor(context).putBoolean(key, value).commit()
        }

        fun readBoolean(context: Context, key: String?, defValue: Boolean): Boolean {
            return getPreferences(context).getBoolean(key, defValue)
        }

        fun writeInteger(context: Context, key: String?, value: Int) {
            getEditor(context).putInt(key, value).commit()
        }

        fun readInteger(context: Context, key: String?, defValue: Int): Int {
            return getPreferences(context).getInt(key, defValue)
        }

        fun writeString(context: Context, key: String?, value: String?) {
            getEditor(context).putString(key, value).commit()
        }

        fun readString(context: Context, key: String?, defValue: String?): String {
            return getPreferences(context).getString(key, defValue)
        }

        fun writeFloat(context: Context, key: String?, value: Float) {
            getEditor(context).putFloat(key, value).commit()
        }

        fun readFloat(context: Context, key: String?, defValue: Float): Float {
            return getPreferences(context).getFloat(key, defValue)
        }

        fun writeLong(context: Context, key: String?, value: Long) {
            getEditor(context).putLong(key, value).commit()
        }

        fun readLong(context: Context, key: String?, defValue: Long): Long {
            return getPreferences(context).getLong(key, defValue)
        }

        fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREF_NAME, 0)
        }

        fun getEditor(context: Context): SharedPreferences.Editor {
            return getPreferences(context).edit()
        }

        fun clear(act: Context) {
            getEditor(act).clear().commit()
        }
    }

    companion object {
        private var prefrenceManager: PrefrenceManager? = null
        fun getInstance(content: Context): PrefrenceManager? {
            if (prefrenceManager == null) {
                prefrenceManager = PrefrenceManager(content)
            }
            return prefrenceManager
        }
    }
}