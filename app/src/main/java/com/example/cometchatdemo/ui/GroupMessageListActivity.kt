package com.example.cometchatdemo.ui

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.GroupMembersRequest
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.helpers.Logger
import com.cometchat.pro.models.*
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants
import com.example.cometchatdemo.dataclass.ChatMessages
import com.example.cometchatdemo.ui.ChatAdapter.chatAdapter
import com.example.cometchatdemo.ui.GroupInfoAdapter.GroupInfoAdapter
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import java.io.File


class GroupMessageListActivity : AppCompatActivity(), View.OnClickListener {
    var IMG_CODE = 101
    var VIDEOS_CODE = 102
    var IS_IMG = false
    var selectedFile: File? = null
    var btn: Button? = null
    var file_attech: ImageView? = null
    var imgGroupInfo: ImageView? = null
    var image: ImageView? = null
    var img_profile: ImageView? = null
    var imgStatus: ImageView? = null
    var imgVideoCall: ImageView? = null
    var txt_user_name: TextView? = null
    var txt_user_message: TextView? = null
    var adapter: chatAdapter? = null
    var recyclerView: RecyclerView? = null
    var editText: TextInputEditText? = null
    var group_info: Group? = null
    private val mMessages: ArrayList<ChatMessages> = ArrayList()
    private val members_list: ArrayList<GroupMember> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_message_list)
        if (!hasPermissions(this, Manifest.permission.RECORD_AUDIO) && !hasPermissions(
                this,
                Manifest.permission.CAMERA
            ) && !hasPermissions(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) && !hasPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_MEDIA_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ), 1001
                )
            }
        }

        recyclerView = findViewById(R.id.recyclerview)
        btn = findViewById(R.id.send)
        editText = findViewById(R.id.text)
        file_attech = findViewById(R.id.appCompatImageView)
        image = findViewById(R.id.image)
        img_profile = findViewById(R.id.img_profile)
        imgStatus = findViewById(R.id.imgStatus)
        txt_user_name = findViewById(R.id.txt_user_name)
        txt_user_message = findViewById(R.id.txt_user_message)
        imgVideoCall = findViewById(R.id.imgVideoCall)
        imgGroupInfo = findViewById(R.id.imgGroupInfo)
        adapter = chatAdapter(mMessages, true)
        recyclerView!!.adapter = adapter

        callHistory()

        btn!!.setOnClickListener(this)
        file_attech!!.setOnClickListener(this)
        imgVideoCall!!.setOnClickListener(this)
        imgGroupInfo!!.setOnClickListener(this)
    }

    private fun callHistory() {
        val messagesRequest: MessagesRequest? = MessagesRequest.MessagesRequestBuilder()
            .setLimit(100)
//            .setUID(AppConstants.REC_UID)
            .setGUID(intent.extras!!.get(AppConstants.UID).toString())
            .build()
        messagesRequest?.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(p0: List<BaseMessage>?) {
                if (!p0.isNullOrEmpty()) {
                    for (baseMessage in p0) {
                        if (baseMessage is TextMessage) {
                            Log.d("HISTORY", "TextMessage: ${baseMessage.text}")
                            Log.d("HISTORY", "TextMessage: ${baseMessage.sender.name}")
                            mMessages.add(
                                ChatMessages(
                                    baseMessage.text,
                                    "",
                                    baseMessage.sender.uid,
                                    baseMessage.sender.name,
                                    false
                                )
                            )
                        } else if (baseMessage is MediaMessage) {
                            Log.d("HISTORY", "MediaMessage: ${baseMessage.attachment.fileUrl}")
                            Log.d("HISTORY", "TextMessage: ${baseMessage.sender.name}")
                            mMessages.add(
                                ChatMessages(
                                    "",
                                    baseMessage.attachment.fileUrl,
                                    baseMessage.sender.uid,
                                    baseMessage.sender.name,
                                    true
                                )
                            )

                        }
                        CometChat.markAsRead(
                            baseMessage
                        )

                    }
                    adapter?.notifyDataSetChanged()
                    recyclerView?.scrollToPosition(adapter!!.itemCount - 1)
                }

            }

            override fun onError(p0: CometChatException?) {
                Log.d("HISTORY", "Message fetching failed with exception: " + p0?.message)

            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.send -> {
                image!!.visibility = View.GONE
                editText!!.visibility = View.VISIBLE
                file_attech!!.visibility = View.VISIBLE
                if (!selectedFile?.absolutePath.isNullOrEmpty()) {
                    sendMediaMessage()
                } else {
                    if (editText!!.text?.trim().toString().isNullOrEmpty()) {
                        Toast.makeText(this, "PLease Enter Text", Toast.LENGTH_SHORT).show()
                    } else {
                        sendNormalMessage()
                    }
                }
            }
            R.id.appCompatImageView -> {


                setMediaDialog()
            }
            R.id.imgVideoCall -> {
                startActivity(
                    Intent(
                        this@GroupMessageListActivity,
                        VIdeoCallActivity::class.java
                    ).putExtra(AppConstants.UID, intent.extras!!.get(AppConstants.UID).toString())
                        .putExtra(AppConstants.IS_GROUP, true)
                )
                finish()
            }
            R.id.imgGroupInfo -> {
                showGroupInfoDialog()
            }
        }

    }

    private fun showGroupInfoDialog() {
        val dialog = Dialog(
            this@GroupMessageListActivity,
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.group_details_dialog_layout)
        dialog.setCancelable(true)

        val img_profile = dialog.findViewById<ImageView>(R.id.img_profile)
        val txt_user_name = dialog.findViewById<TextView>(R.id.txt_user_name)
        val txtDesc = dialog.findViewById<TextView>(R.id.txtDesc)
        val rvGroupMembers = dialog.findViewById<RecyclerView>(R.id.rvGroupMembers)
        val aadpter = GroupInfoAdapter(members_list)
        rvGroupMembers.adapter = aadpter
        txtDesc.text = group_info!!.description
        txt_user_name.text = group_info!!.name
        Glide.with(this)
            .load(group_info!!.icon)
            .placeholder(R.drawable.ic_img_placeholder)
            .into(img_profile)

        dialog.show()
    }


    private fun sendNormalMessage() {
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_GROUP

        val textMessage =
//            TextMessage(AppConstants.REC_UID, editText!!.text!!.trim().toString(), receiverType)
            TextMessage(
                intent.extras!!.get(AppConstants.UID).toString(),
                editText!!.text!!.trim().toString(),
                receiverType
            )

        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(p0: TextMessage?) {
                Log.d("SEND_NORMAL_MESSAGE", "Message sent successfully: " + p0?.toString())

                mMessages.add(ChatMessages(p0?.text, "", p0?.sender?.uid, p0!!.sender.name, false))
                editText!!.setText("")
                adapter!!.notifyDataSetChanged()
                recyclerView!!.scrollToPosition(recyclerView!!.adapter!!.itemCount - 1)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(
                    "SEND_NORMAL_MESSAGE",
                    "Message sending failed with exception: " + p0?.message
                )
            }

        })
    }

    private fun setMediaDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)


        builder.setMessage("Do you want to use Images ?")

        builder.setTitle("Alert !!!!!")
        builder.setCancelable(true)
        builder
            .setPositiveButton(
                "Yes"
            ) { dialog, which -> // When the user click yes button
                ImagePicker.with(this)
                    .compress(1024)
                    .maxResultSize(
                        1080,
                        1080
                    )
                    .start(IMG_CODE)
                dialog.cancel()
            }
        builder
            .setNegativeButton(
                "No"
            ) { dialog, which -> // If user click no
//                getAllVideos()
                dialog.cancel()
            }


        val alertDialog: android.app.AlertDialog? = builder.create()
        alertDialog?.show()
    }

    private fun getAllVideos() {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .toString() + "/" + resources.getString(R.string.app_name) + "/"
        val f = File(path)
        val file = f.listFiles()
        if (!file.isNullOrEmpty()) {
            for ((i, element) in file.withIndex()) {
                Log.d("Files", "FileName:" + element.name)
                Log.d(
                    "Files",
                    "FileName:" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/" + resources.getString(R.string.app_name) + "/" + element.name
                )
                selectedFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/" + resources.getString(R.string.app_name) + "/" + element.name
                )

            }
            Log.e("VIDEOS", selectedFile!!.absolutePath)
            Log.e("VIDEOS", selectedFile!!.exists().toString())
            IS_IMG = false
        }
    }

    private fun sendMediaMessage() {
        var messageType = ""
        messageType = if (IS_IMG) {
            CometChatConstants.MESSAGE_TYPE_IMAGE
        } else {
            CometChatConstants.MESSAGE_TYPE_VIDEO
        }
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_GROUP

        val mediaMessage =
//            MediaMessage(AppConstants.REC_UID, selectedFile, messageType, receiverType)
            MediaMessage(
                intent.extras!!.get(AppConstants.UID).toString(),
                selectedFile,
                messageType,
                receiverType
            )

        CometChat.sendMediaMessage(
            mediaMessage,
            object : CometChat.CallbackListener<MediaMessage>() {
                override fun onSuccess(p0: MediaMessage?) {
                    Log.d(
                        "SEND_MEDIA_MESSAGE",
                        "Media message sent successfully: " + p0?.toString()
                    )
                    selectedFile = null
                    mMessages.add(
                        ChatMessages(
                            "",
                            p0?.attachment?.fileUrl,
                            p0?.sender?.uid,
                            p0!!.sender.name,
                            true
                        )
                    )
                    adapter!!.notifyDataSetChanged()
                    recyclerView!!.scrollToPosition(recyclerView!!.adapter!!.itemCount - 1)
                }

                override fun onError(p0: CometChatException?) {
                    Log.d(
                        "SEND_MEDIA_MESSAGE",
                        "Message sending failed with exception: " + p0?.message
                    )
                }
            })
    }

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                Logger.error(
                    "PERMISSION", " hasPermissions() : Permission : " + permission
                            + "checkSelfPermission : " + ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    )
                )
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()

        getGroupDetails()
        //setIncommingCallListener()

        CometChat.addMessageListener(
            resources.getString(R.string.app_name),
            object : CometChat.MessageListener() {
                override fun onTextMessageReceived(textMessage: TextMessage) {
                    Log.d("NEW_MSG", "Text message received successfully: $textMessage")
                    mMessages.add(
                        ChatMessages(
                            textMessage.text,
                            "",
                            textMessage.sender.uid,
                            textMessage.sender.name,
                            false
                        )
                    )
                    adapter!!.notifyDataSetChanged()
                    recyclerView!!.scrollToPosition(recyclerView!!.adapter!!.itemCount - 1)
                }

                override fun onMediaMessageReceived(mediaMessage: MediaMessage) {
                    Log.d("NEW_MSG", "Media message received successfully: $mediaMessage")
                    mMessages.add(
                        ChatMessages(
                            "",
                            mediaMessage.attachment.fileUrl,
                            mediaMessage.sender.uid,
                            mediaMessage.sender.name,
                            true
                        )
                    )
                    adapter!!.notifyDataSetChanged()
                    recyclerView!!.scrollToPosition(recyclerView!!.adapter!!.itemCount - 1)
                }

                override fun onCustomMessageReceived(customMessage: CustomMessage) {
                    Log.d("NEW_MSG", "Custom message received successfully: $customMessage")
                }
            })

    }

    private fun getGroupDetails() {
        CometChat.getGroup(
            intent.extras!!.getString(AppConstants.UID).toString(),
            object : CometChat.CallbackListener<Group>() {
                override fun onSuccess(p0: Group?) {
                    Log.d("GROUP_DETAILS", "Group details fetched successfully: " + p0?.toString())
                    group_info = p0

                    Glide.with(this@GroupMessageListActivity)
                        .load(p0!!.icon)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(img_profile!!)
                    txt_user_name!!.text = p0.name
                }

                override fun onError(p0: CometChatException?) {
                    Log.d(
                        "GROUP_DETAILS",
                        "Group details fetching failed with exception: " + p0?.message
                    )
                }
            })

        val groupMembersRequest: GroupMembersRequest? =
            GroupMembersRequest.GroupMembersRequestBuilder(
                intent.extras!!.getString(AppConstants.UID).toString()
            ).setLimit(100).build()

        groupMembersRequest?.fetchNext(object : CometChat.CallbackListener<List<GroupMember>>() {
            override fun onSuccess(p0: List<GroupMember>?) {
                p0?.forEach {
                    Log.d("GROUP_DETAILS_MEMBERS", it.toString())
                    members_list.add(it)
                }

            }

            override fun onError(p0: CometChatException?) {
                Log.d(
                    "GROUP_DETAILS_MEMBERS",
                    "Group Member list fetching failed with exception: " + p0?.message
                )
            }
        })
    }

    private fun setIncommingCallListener() {
        CometChat.addCallListener(resources.getString(R.string.app_name),
            object : CometChat.CallListener() {
                override fun onOutgoingCallAccepted(p0: Call?) {
                    Log.d("RECIEVE_CALL", "Outgoing call accepted: " + p0?.toString())
                }

                override fun onIncomingCallReceived(p0: Call?) {
                    Log.d("RECIEVE_CALL", "Incoming call: " + p0?.toString())
                    setCallDialog(p0!!.sessionId, (p0.callInitiator as User).name)
                }

                override fun onIncomingCallCancelled(p0: Call?) {
                    Log.d("RECIEVE_CALL", "Incoming call cancelled: " + p0?.toString())
                }

                override fun onOutgoingCallRejected(p0: Call?) {
                    Log.d("RECIEVE_CALL", "Outgoing call rejected: " + p0?.toString())
                }

            })
    }

    private fun setCallDialog(sessionId: String, name: String) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)


        builder.setMessage("Do you want Join Call With $name ?")

        builder.setTitle("Alert !!!!!\n Incoming Call")
        builder.setCancelable(true)
        builder
            .setPositiveButton(
                "Yes"
            ) { dialog, which -> // When the user click yes button
                acceptCall(sessionId)
                finish()
            }
        builder
            .setNegativeButton(
                "No"
            ) { dialog, which -> // If user click no
                rejectCall(sessionId)
                dialog.cancel()
            }


        val alertDialog: android.app.AlertDialog? = builder.create()
        alertDialog?.show()
    }

    private fun acceptCall(sessionId: String) {
        CometChat.acceptCall(sessionId, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("ACCEPT_CALL", "Call accepted successfully: " + p0?.toString())
                startActivity(
                    Intent(
                        this@GroupMessageListActivity,
                        VIdeoCallActivity::class.java
                    ).putExtra(AppConstants.UID, intent.extras!!.get(AppConstants.UID).toString())
                        .putExtra(AppConstants.IS_JOIN_CALL, true).putExtra(
                            AppConstants.SESSION_ID,
                            p0!!.sessionId
                        )
                )
                finish()
            }

            override fun onError(p0: CometChatException?) {
                Log.d("ACCEPT_CALL", "Call acceptance failed with exception: " + p0?.message)
            }

        })
    }

    private fun rejectCall(sessionId: String) {
        val status: String = CometChatConstants.CALL_STATUS_REJECTED
        CometChat.rejectCall(sessionId, status, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("REJECT_CALL", "Call rejected successfully with status: " + p0?.callStatus)
            }

            override fun onError(p0: CometChatException?) {
                Log.d("REJECT_CALL", "Call rejection failed with exception: " + p0?.message)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMG_CODE) {
            image!!.visibility = View.VISIBLE
            editText!!.visibility = View.GONE
            file_attech!!.visibility = View.GONE
            image!!.setImageURI(data?.data!!)
            selectedFile = File(
                data.getStringExtra("extra.file_path")
            )
            Log.e("IMG", selectedFile!!.absolutePath)
            IS_IMG = true
        } else if (resultCode == RESULT_OK && requestCode == VIDEOS_CODE) {

            Log.e("VIDEOS", data?.data!!.path.toString())

            IS_IMG = false
        }
    }

}