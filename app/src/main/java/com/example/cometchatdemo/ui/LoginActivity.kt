package com.example.cometchatdemo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants
import com.example.cometchatdemo.constants.SharedPreferenceManager


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    var editText: EditText? = null
    var AUTH_KEY: Button? = null
    var AH_TOKEN: Button? = null
    var logout: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        editText = findViewById(R.id.edtUID)
        AUTH_KEY = findViewById(R.id.btnAuthKeys)
        AH_TOKEN = findViewById(R.id.btnAuthToken)
        logout = findViewById(R.id.btnLogout)
        AUTH_KEY!!.setOnClickListener(this)
        AH_TOKEN!!.setOnClickListener(this)
        logout!!.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAuthKeys -> {
                loginUsingAuthKeys()
            }
            R.id.btnAuthToken -> {
                loginUsingAuthToken()
            }
            R.id.btnLogout -> {
                logout()
            }
        }
    }

    private fun loginUsingAuthKeys() {


        CometChat.login(
            editText!!.text.trim().toString(),
            AppConstants.AUTH_KEY,

            object : CometChat.CallbackListener<User>() {
                override fun onSuccess(p0: User?) {
                    Log.d("LOGIN", "Login Successful : " + p0?.toString())
                    SharedPreferenceManager.putBoolean(AppConstants.IS_LOGIN,true)
                    SharedPreferenceManager.putString(AppConstants.UID, p0!!.uid)
                    startActivity(Intent(this@LoginActivity, MessageListActivity::class.java))
                }

                override fun onError(p0: CometChatException?) {
                    Log.d("LOGIN", "Login failed with exception: " + p0?.message)
                }

            })
    }

    private fun loginUsingAuthToken() {


//        CometChat.login(AppConstants.AUTH_KEY, object : CometChat.CallbackListener<User>() {
//            override fun onSuccess(p0: User?) {
//                Log.d("LOGIN", "Login Successful : " + p0?.toString())
//            }
//
//            override fun onError(p0: CometChatException?) {
//                Log.d("LOGIN", "Login failed with exception: " + p0?.message)
//            }
//        })
    }

    private fun logout() {
        CometChat.logout(object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                Log.d("LOGOUT", "Logout completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d("LOGOUT", "Logout failed with exception: " + p0?.message)
            }

        })
    }
}