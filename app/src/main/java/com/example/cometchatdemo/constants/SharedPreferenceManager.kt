package com.example.cometchatdemo.constants

import android.content.Context
import android.content.SharedPreferences
import com.cometchat.pro.models.User


object SharedPreferenceManager {
    val PREF_NAME = "_pref"
    private var isInit = false
    private var prefs: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    fun init(context: Context) {
        if (isInit)
            return
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = prefs!!.edit()
        isInit = true
    }


    fun putBoolean(key: String, value: Boolean) {
        editor!!.putBoolean(key, value)
        editor!!.commit()
    }

    fun putString(key: String, value: String) {
        editor!!.putString(key, value)
        editor!!.commit()
    }

    fun putFloat(key: String, value: Float) {
        editor!!.putFloat(key, value)
        editor!!.commit()
    }

    fun putLong(key: String, value: Long) {
        editor!!.putLong(key, value)
        editor!!.commit()
    }

    fun putLat(key: String, value: Long) {
        editor!!.putLong(key, value)
        editor!!.commit()
    }

    fun putInt(key: String, value: Int) {
        editor!!.putInt(key, value)
        editor!!.commit()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs!!.getBoolean(key, defaultValue)
    }

    fun getString(key: String, defValue: String): String? {
        return prefs!!.getString(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return prefs!!.getFloat(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return prefs!!.getInt(key, defValue)
    }

    fun getLong(key: String, defValue: Long): Long {
        return prefs!!.getLong(key, defValue)
    }


}