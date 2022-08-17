package com.example.cometchatdemo.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
import com.example.cometchatdemo.constants.SharedPreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class VIdeoCallActivity : AppCompatActivity(), View.OnClickListener {
    var rlVideoCall: RelativeLayout? = null
    var IS_GROUP = false
    var IS_AUTO_END = true
    var session_id: String = ""
    var incoming_call_view: MaterialCardView? = null
    var caller_name: TextView? = null
    var call_type: TextView? = null
    var caller_av: ImageView? = null
    var decline_incoming: MaterialButton? = null
    var accept_incoming: MaterialButton? = null
    var outgoing_call_view: RelativeLayout? = null
    var calling_tv: TextView? = null
    var user_av: ImageView? = null
    var user_tv: TextView? = null
    var call_hang_btn: FloatingActionButton? = null
    var is_group: Boolean = false
    var reject_session_id: String = ""
    var isCall_rejected: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        incoming_call_view = findViewById(R.id.incoming_call_view)
        caller_name = findViewById(R.id.caller_name)
        call_type = findViewById(R.id.call_type)
        caller_av = findViewById(R.id.caller_av)
        decline_incoming = findViewById(R.id.decline_incoming)
        accept_incoming = findViewById(R.id.accept_incoming)
        outgoing_call_view = findViewById(R.id.outgoing_call_view)
        calling_tv = findViewById(R.id.calling_tv)
        user_av = findViewById(R.id.user_av)
        user_tv = findViewById(R.id.user_tv)
        call_hang_btn = findViewById(R.id.call_hang_btn)

        decline_incoming!!.setOnClickListener(this)
        accept_incoming!!.setOnClickListener(this)
        call_hang_btn!!.setOnClickListener(this)



        rlVideoCall = findViewById(R.id.rlVideoCall)
        mainView = findViewById(R.id.rlVideoCall)
        caller_name!!.text = intent.extras!!.get(AppConstants.NAME).toString()
        call_type!!.text = "Video"
        caller_av!!.LoadImg(intent.extras!!.get(AppConstants.AVATAR).toString())
        user_av!!.LoadImg(SharedPreferenceManager.getString(AppConstants.AVATAR, "").toString())
        user_tv!!.text = SharedPreferenceManager.getString(AppConstants.NAME, "Iron Man")

        is_group = intent.extras!!.getBoolean(AppConstants.IS_GROUP)
        if (intent.extras!!.getBoolean(AppConstants.IS_GROUP)) {
            IS_GROUP = true
        }
        when (intent.extras!!.getString(AppConstants.CALL_TYPE)) {
            AppConstants.INCOMMING_CALL -> {
                outgoing_call_view!!.visibility = View.GONE
            }
            AppConstants.ACCEPT_CALL -> {
                outgoing_call_view!!.visibility = View.GONE
                incoming_call_view!!.visibility = View.GONE
                rlVideoCall!!.visibility = View.VISIBLE
                videoCall(intent.extras!!.get(AppConstants.SESSION_ID).toString())
            }
            AppConstants.CREATE_CALL -> {
                initiatCall()
                incoming_call_view!!.visibility = View.GONE
            }
        }
        videocallActivity = this
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
//                videoCall(p0!!.sessionId)
                session_id = p0!!.sessionId
                reject_session_id = p0!!.sessionId
                VideoAutoEndCall(p0!!.sessionId)
            }

            override fun onError(p0: CometChatException?) {
                Log.d("INITIATE_CALL", "Call initialization failed with exception: " + p0?.message)
            }
        })
    }


    private fun VideoAutoEndCall(sessionId: String) {
        Handler().postDelayed({
            if (CometChat.getActiveCall() == null && !isCall_rejected) {
                rejectCall(sessionId, CometChatConstants.CALL_STATUS_CANCELLED)
            }

        }, 30000)
    }

    fun ImageView.LoadImg(url: String) {
        Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_img_placeholder)
            .into(this)

    }

    companion object {
        var videocallActivity: VIdeoCallActivity? = null
        var mainView: RelativeLayout? = null
    }

    private fun acceptCall(sessionId: String) {
        CometChat.acceptCall(sessionId, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("ACCEPT_CALL", "Call accepted successfully: " + p0?.toString())

                rlVideoCall!!.visibility = View.VISIBLE
                outgoing_call_view!!.visibility = View.GONE
                incoming_call_view!!.visibility = View.GONE
                videoCall(p0!!.sessionId)
            }

            override fun onError(p0: CometChatException?) {
                Log.d("ACCEPT_CALL", "Call acceptance failed with exception: " + p0?.message)
            }

        })
    }

    private fun rejectCall(sessionId: String, status: String) {
        CometChat.rejectCall(sessionId, status, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("REJECT_CALL", "Call rejected successfully with status: " + p0?.callStatus)
                finish()
            }

            override fun onError(p0: CometChatException?) {
                Log.d("REJECT_CALL", "Call rejection failed with exception: " + p0?.message)
            }
        })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.decline_incoming -> {
                rejectCall(
                    intent.extras?.get(AppConstants.SESSION_ID).toString(),
                    CometChatConstants.CALL_STATUS_REJECTED
                )
            }
            R.id.accept_incoming -> {
                acceptCall(intent.extras?.get(AppConstants.SESSION_ID).toString())
            }
            R.id.call_hang_btn -> {
                isCall_rejected = true
                rejectCall(
                    reject_session_id,
                    CometChatConstants.CALL_STATUS_CANCELLED
                )
            }
        }
    }
}