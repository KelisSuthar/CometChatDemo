package com.example.cometchatdemo.ui

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CallSettings.CallSettingsBuilder
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.OngoingCallListener
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.AudioMode
import com.cometchat.pro.models.User
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants


class VIdeoCallActivity : AppCompatActivity() {
    var rlVideoCall: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)
        rlVideoCall = findViewById(R.id.rlVideoCall)

    }

    override fun onResume() {
        super.onResume()
        initiatCall()

    }

    private fun initiatCall() {
        val receiverID: String = intent.extras!!.get(AppConstants.UID).toString()
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER
        val callType: String = CometChatConstants.CALL_TYPE_VIDEO

        val call = Call(receiverID, receiverType, callType)

        CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("INITIATE_CALL", "Call initiated successfully: " + p0?.toString())
                videoCall()
            }

            override fun onError(p0: CometChatException?) {
                Log.d("INITIATE_CALL", "Call initialization failed with exception: " + p0?.message)
            }

        })
    }

    private fun videoCall() {

        val callSettings: CallSettings = CallSettingsBuilder(this@VIdeoCallActivity, rlVideoCall)
            .setSessionId(System.currentTimeMillis().toString())
            .setAudioOnlyCall(false)
            .build()

        CometChat.startCall(callSettings, object : OngoingCallListener {
            override fun onUserJoined(user: User) {
                Log.d("VIDEOCALL", "onUserJoined: Name " + user.name)
            }

            override fun onUserLeft(user: User) {
                Log.d("VIDEOCALL", "onUserLeft: " + user.name)
            }

            override fun onError(e: CometChatException) {
                Log.d("VIDEOCALL", "onError: " + e.message)
            }

            override fun onCallEnded(call: Call) {
                Log.d("VIDEOCALL", "onCallEnded: $call")
            }

            override fun onUserListUpdated(list: List<User>) {
                Log.d("VIDEOCALL", "onUserListUpdated: $list")
            }

            override fun onAudioModesUpdated(list: List<AudioMode>) {
                Log.d("VIDEOCALL", "onAudioModesUpdated: $list")
            }

            override fun onRecordingStarted(p0: User?) {
                Log.d("VIDEOCALL", "onRecordingStarted: $p0")
            }

            override fun onRecordingStopped(p0: User?) {
                Log.d("VIDEOCALL", "onRecordingStopped: $p0")
            }

            override fun onUserMuted(p0: User?, p1: User?) {
                Log.d("VIDEOCALL", "onUserMute: $p0 And $p1")
            }

            override fun onCallSwitchedToVideo(p0: String?, p1: User?, p2: User?) {
                Log.d("VIDEOCALL", "onCallSwitchToVideo: $p0 \n $p1 \n  $p2")
            }
        })
    }
}