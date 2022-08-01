package com.example.cometchatdemo.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CallSettings.CallSettingsBuilder
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.core.CometChat.OngoingCallListener
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.AudioMode
import com.cometchat.pro.models.User
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants


class VIdeoCallActivity : AppCompatActivity() {
    var rlVideoCall: RelativeLayout? = null
    var IS_GROUP = false
    var IS_AUTO_END = true
    var session_id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)
        rlVideoCall = findViewById(R.id.rlVideoCall)
        if (intent.extras!!.getBoolean(AppConstants.IS_GROUP)) {
            IS_GROUP = true
        }
        if (intent.extras!!.getBoolean(AppConstants.IS_JOIN_CALL, false)) {
            videoCall(intent.extras!!.get(AppConstants.SESSION_ID).toString())
        } else {
            initiatCall()
        }


    }

    private fun initiatCall() {
        val receiverID: String = intent.extras!!.get(AppConstants.UID).toString()
        val callType: String = CometChatConstants.CALL_TYPE_VIDEO
        val receiverType: String = if (IS_GROUP) {
            CometChatConstants.RECEIVER_TYPE_GROUP
        } else {
            CometChatConstants.RECEIVER_TYPE_USER
        }
        val call = Call(receiverID, receiverType, callType)

        CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("INITIATE_CALL", "Call initiated successfully: " + p0?.toString())
                videoCall(p0!!.sessionId)
                session_id = p0.sessionId
                VideoAutoEndCall()
            }

            override fun onError(p0: CometChatException?) {
                Log.d("INITIATE_CALL", "Call initialization failed with exception: " + p0?.message)
            }
        })
    }

    private fun VideoAutoEndCall() {
        Handler().postDelayed({
            if (IS_AUTO_END) {
                endCallManually(session_id)
            }
        }, 15000)
    }


    private fun videoCall(session_id: String) {
        var left = false
        val callSettings: CallSettings = CallSettingsBuilder(this@VIdeoCallActivity, rlVideoCall)
            .setSessionId(session_id)
            .startWithAudioMuted(true)
            .startWithVideoMuted(true)
            .showCallRecordButton(true)
            .build()

        CometChat.startCall(callSettings, object : OngoingCallListener {
            override fun onUserJoined(user: User) {
                Log.d("VIDEOCALL", "onUserJoined: Name " + user.name)
            }

            override fun onUserLeft(user: User) {
                Log.d("VIDEOCALL", "onUserLeft: " + user.name)
                left = true
            }

            override fun onError(e: CometChatException) {
                Log.d("VIDEOCALL", "onError: " + e.message)
            }

            override fun onCallEnded(call: Call) {
                Log.d("VIDEOCALL", "onCallEnded: $call")
                finish()
            }

            override fun onUserListUpdated(list: List<User>) {
                Log.d("VIDEOCALL", "onUserListUpdated: $list")
                Log.d("VIDEOCALL", "${list.size}")
                if (list.size != 1) {
                    IS_AUTO_END = false
                }
                if (list.size == 1 && left) {
                    finish()
                }
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

    private fun endCallManually(session_id: String) {
        CometChat.endCall(session_id, object : CallbackListener<Call?>() {
            override fun onSuccess(call: Call?) {
                Log.d("VIDEOCALL", "onCallEndedManually: $call")
                finish()
            }

            override fun onError(e: CometChatException) {
                Log.d("VIDEOCALL", "onCallEndedError: $e")
            }
        })
    }

    override fun onResume() {
        super.onResume()
      //  setIncommingCallListener()
    }

    private fun setIncommingCallListener() {
        CometChat.addCallListener(resources.getString(R.string.app_name),
            object : CometChat.CallListener() {
                override fun onOutgoingCallAccepted(p0: Call?) {
                    Log.d("RECIEVE_CALL_I", "Outgoing call accepted: " + p0?.toString())
                }

                override fun onIncomingCallReceived(p0: Call?) {
                    Log.d("RECIEVE_CALL_I", "Incoming call: " + p0?.toString())
//                    setCallDialog(p0!!.sessionId, (p0.callInitiator as User).name)
                }

                override fun onIncomingCallCancelled(p0: Call?) {
                    Log.d("RECIEVE_CALL_I", "Incoming call cancelled: " + p0?.toString())
                }

                override fun onOutgoingCallRejected(p0: Call?) {
                    Log.d("RECIEVE_CALL_I", "Outgoing call rejected: " + p0?.toString())
                }

            })
    }
}