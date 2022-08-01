package com.example.cometchatdemo

import android.app.Application
import android.util.Log
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat

class AppClass : Application() {
    override fun onCreate() {
        super.onCreate()
        setIncommingCallListener()
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

    override fun onTerminate() {
        super.onTerminate()
        CometChat.removeCallListener(resources.getString(R.string.app_name))
    }
}