package com.example.cometchatdemo

import android.app.Application
import android.content.Intent
import android.util.Log
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.example.cometchatdemo.constants.AppConstants
import com.example.cometchatdemo.ui.VIdeoCallActivity


class AppClass : Application() {
    override fun onCreate() {
        super.onCreate()
        setIncommingCallListener()
    }

    private fun setIncommingCallListener() {
        CometChat.addCallListener(resources.getString(R.string.app_name),
            object : CometChat.CallListener() {
                override fun onOutgoingCallAccepted(p0: Call?) {

                    if (VIdeoCallActivity.mainView != null) {
                        VIdeoCallActivity.videocallActivity!!.finish()
                        startActivity(
                            Intent(this@AppClass, VIdeoCallActivity::class.java)
                                .putExtra(AppConstants.SESSION_ID, p0?.sessionId)
                                .putExtra(AppConstants.CALL_TYPE, AppConstants.ACCEPT_CALL)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            .putExtra(AppConstants.IS_INCOMMING, false)
                        )
                    }
                }

                override fun onIncomingCallReceived(p0: Call?) {
                    Log.d("RECIEVE_CALL_I", "Incoming call: " + p0?.toString())
//                    setCallDialog(p0!!.sessionId, (p0.callInitiator as User).name)
                    if (CometChat.getActiveCall() == null) {
                        if (p0!!.receiverType == CometChatConstants.RECEIVER_TYPE_USER) {
                            startCallIntent(
                                p0.callInitiator as User,
                                p0.sessionId
                            )
                        } else {
                            startGroupCallIntent(
                                p0.receiver as Group,
                                p0.sessionId
                            )
                        }
                    } else {
                        CometChat.rejectCall(
                            p0!!.sessionId,
                            CometChatConstants.CALL_STATUS_BUSY,
                            object : CometChat.CallbackListener<Call?>() {
                                override fun onSuccess(call: Call?) {
                                    Log.d(
                                        "REJECT_CALL",
                                        "Call rejected successfully with status: " + p0.callStatus
                                    )
                                }

                                override fun onError(e: CometChatException) {
                                    Log.d(
                                        "REJECT_CALL",
                                        "Call rejection failed with exception: " + e.message
                                    )

                                }
                            })
                    }

                }

                override fun onIncomingCallCancelled(p0: Call?) {
                    Log.d("RECIEVE_CALL_I", "Incoming call cancelled: " + p0?.toString())
                    if (VIdeoCallActivity.videocallActivity != null) VIdeoCallActivity.videocallActivity!!.finish()
                }

                override fun onOutgoingCallRejected(p0: Call?) {
                    Log.d("RECIEVE_CALL_I", "Outgoing call rejected: " + p0?.toString())
                    if (VIdeoCallActivity.videocallActivity != null) VIdeoCallActivity.videocallActivity!!.finish()
                }

            })
    }

    private fun startGroupCallIntent(
        group: Group,
        sessionId: String?
    ) {

        startActivity(
            Intent(this, VIdeoCallActivity::class.java)
                .putExtra(AppConstants.NAME, group.name)
                .putExtra(AppConstants.UID, group.guid)
                .putExtra(AppConstants.SESSION_ID, sessionId)
                .putExtra(AppConstants.AVATAR, group.icon)
                .putExtra(AppConstants.CALL_TYPE, AppConstants.INCOMMING_CALL)
                .putExtra(AppConstants.IS_GROUP, true)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun startCallIntent(
        user: User,
        sessionId: String?
    ) {
        startActivity(
            Intent(this, VIdeoCallActivity::class.java)
                .putExtra(AppConstants.NAME, user.name)
                .putExtra(AppConstants.UID, user.uid)
                .putExtra(AppConstants.SESSION_ID, sessionId)
                .putExtra(AppConstants.AVATAR, user.avatar)
                .putExtra(AppConstants.CALL_TYPE, AppConstants.INCOMMING_CALL)
                .putExtra(AppConstants.IS_GROUP, false)
                .putExtra(AppConstants.IS_GROUP, false)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        )
    }

    override fun onTerminate() {
        super.onTerminate()
        CometChat.removeCallListener(resources.getString(R.string.app_name))
    }
}