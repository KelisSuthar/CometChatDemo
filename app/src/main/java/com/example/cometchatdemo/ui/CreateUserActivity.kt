package com.example.cometchatdemo.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants


class CreateUserActivity : AppCompatActivity() {
    var status: TextView? = null
    var edtUser: EditText? = null
    var btnCreate: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        status = findViewById(R.id.txtStatus)
        edtUser = findViewById(R.id.edtUser)
        btnCreate = findViewById(R.id.btnCreate)


        status!!.text = resources.getString(R.string.app_name) + "_" + System.currentTimeMillis()

        btnCreate!!.setOnClickListener {

            createUser(edtUser!!.text.trim().toString(), status!!.text.trim().toString())
        }
    }

    private fun createUser(user_name: String, uuid: String) {
        val user = User()
        user.uid = uuid// Replace with your uid for the user to be created.
        user.name = user_name  // Replace with the name of the user

        CometChat.createUser(
            user,
            AppConstants.AUTH_KEY,
            object : CometChat.CallbackListener<User>() {
                override fun onSuccess(user: User) {
                    Log.d("CREATE_USER", user.toString())
                }

                override fun onError(e: CometChatException) {
                    Log.e("CREATE_USER", e.message.toString())
                }
            })
    }

    private fun updateUser(user_name: String, uuid: String) {


        val user = User()
        user.uid = uuid // Replace with your uid for the user to be updated.

        user.name = user_name// Replace with the name of the user


        CometChat.updateUser(user, AppConstants.AUTH_KEY, object : CallbackListener<User>() {
            override fun onSuccess(user: User) {
                Log.d("UPDATE_USER", user.toString())
            }

            override fun onError(e: CometChatException) {
                Log.e("UPDATE_USER", e.message.toString())
            }
        })
    }

    private fun updateLoggedInUser(user_name: String) {
        val user = User()
        user.name = "Iron Man"

        CometChat.updateCurrentUserDetails(user, object : CallbackListener<User>() {
            override fun onSuccess(user: User) {
                Log.d("UPDATE_LOGGEDIN_USER", user.toString())
            }

            override fun onError(e: CometChatException) {
                Log.d("UPDATE_LOGGEDIN_USER", e.message.toString())
            }
        })
    }
}