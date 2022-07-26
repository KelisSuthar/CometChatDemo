package com.example.cometchatdemo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants
import com.example.cometchatdemo.constants.SharedPreferenceManager

class MainActivity : AppCompatActivity() {
    var login: Button? = null
    var create: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        login = findViewById(R.id.btnLogin)
        create = findViewById(R.id.btnCreate)
        SharedPreferenceManager.init(applicationContext)
        initCometChat()
        if (SharedPreferenceManager.getBoolean(AppConstants.IS_LOGIN, false)) {
            startActivity(
                Intent(
                    this@MainActivity,
                    MessageListActivity::class.java
                )
            )
        }
        login!!.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    LoginActivity::class.java
                )
            )
        }
        create!!.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    CreateUserActivity::class.java
                )
            )
        }
    }

    private fun initCometChat() {

        val appSettings = AppSettings.AppSettingsBuilder()
            .subscribePresenceForAllUsers()
            .setRegion(AppConstants.REGION)
            .autoEstablishSocketConnection(true)
            .build()

        CometChat.init(
            this,
            AppConstants.APP_ID,
            appSettings,
            object : CometChat.CallbackListener<String?>() {
                override fun onSuccess(successMessage: String?) {
                    Log.i("INIT", "Initialization completed successfully")
                }

                override fun onError(e: CometChatException) {
                    Log.e("INIT", "Initialization failed with exception: " + e.message)
                }
            })

    }
}