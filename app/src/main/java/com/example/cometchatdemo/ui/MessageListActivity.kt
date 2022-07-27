package com.example.cometchatdemo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.MessageListener
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants
import com.example.cometchatdemo.ui.ChatAdapter.chatAdapter
import com.example.pubnubchatdemo.dataclass.ChatMessages
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import java.io.File


class MessageListActivity : AppCompatActivity(), View.OnClickListener {
    var IMG_CODE = 101
    var img = ""
    var selectedFile: File? = null
    var btn: Button? = null
    var file_attech: ImageView? = null
    var image: ImageView? = null
    var img_profile: ImageView? = null
    var imgStatus: ImageView? = null
    var imgVideoCall: ImageView? = null
    var txt_user_name: TextView? = null
    var txt_user_message: TextView? = null
    var adapter: chatAdapter? = null
    var recyclerView: RecyclerView? = null
    var editText: TextInputEditText? = null
    private val mMessages: ArrayList<ChatMessages> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

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
        adapter = chatAdapter(mMessages)
        recyclerView!!.adapter = adapter

        callHistory()

        btn!!.setOnClickListener(this)
        file_attech!!.setOnClickListener(this)
        imgVideoCall!!.setOnClickListener(this)


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
                ImagePicker.with(this)
                    .compress(1024)
                    .maxResultSize(
                        1080,
                        1080
                    )
                    .start(IMG_CODE)
            }
            R.id.imgVideoCall -> {
                startActivity(
                    Intent(
                        this@MessageListActivity,
                        VIdeoCallActivity::class.java
                    ).putExtra(AppConstants.UID, intent.extras!!.get(AppConstants.UID).toString())
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        getOtherUserHistory()

        CometChat.addMessageListener(
            resources.getString(R.string.app_name),
            object : MessageListener() {
                override fun onTextMessageReceived(textMessage: TextMessage) {
                    Log.d("NEW_MSG", "Text message received successfully: $textMessage")
                    mMessages.add(ChatMessages(textMessage.text, "", textMessage.sender.uid, false))
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

    private fun getOtherUserHistory() {

        CometChat.getUser(
            intent.extras!!.get(AppConstants.UID).toString(),
            object : CometChat.CallbackListener<User>() {
                override fun onSuccess(p0: User?) {
                    Log.d("OTHER_USER_DETAILS", "User details fetched for user: " + p0?.toString())

                    Glide.with(this@MessageListActivity)
                        .load(p0!!.avatar)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(img_profile!!)
                    if (p0!!.status == "online") {
                        imgStatus?.setImageResource(R.drawable.ic_online)
                    } else {
                        imgStatus?.setImageResource(R.drawable.ic_offline)
                    }
                    txt_user_name!!.text = p0.name
                }

                override fun onError(p0: CometChatException?) {
                    Log.d(
                        "OTHER_USER_DETAILS",
                        "User details fetching failed with exception: " + p0?.message
                    )
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        CometChat.removeMessageListener(resources.getString(R.string.app_name))
    }

    private fun callHistory() {

        val messagesRequest: MessagesRequest? = MessagesRequest.MessagesRequestBuilder()
            .setLimit(30)
//            .setUID(AppConstants.REC_UID)
            .setUID(intent.extras!!.get(AppConstants.UID).toString())
            .build();
        messagesRequest?.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(p0: List<BaseMessage>?) {
                if (!p0.isNullOrEmpty()) {
                    for (baseMessage in p0) {
//                        Log.d("HISTORY", "TextMessage: ${baseMessage}")
                        Log.d("HISTORY", "TextMessage SEND: ${baseMessage.sender.uid}")
                        Log.d("HISTORY", "TextMessage REC: ${baseMessage.receiverUid}")
                        if (baseMessage is TextMessage) {
                            Log.d("HISTORY", "TextMessage: ${baseMessage.text}")
                            mMessages.add(
                                ChatMessages(
                                    baseMessage.text,
                                    "",
                                    baseMessage.sender.uid,
                                    false
                                )
                            )
                        } else if (baseMessage is MediaMessage) {
                            Log.d("HISTORY", "MediaMessage: ${baseMessage.attachment.fileUrl}")
                            mMessages.add(
                                ChatMessages(
                                    "",
                                    baseMessage.attachment.fileUrl,
                                    baseMessage.sender.uid,
                                    true
                                )
                            )


                        }
                        adapter!!.notifyDataSetChanged()
                        recyclerView!!.scrollToPosition(recyclerView!!.adapter!!.itemCount - 1)
                    }
                }

            }

            override fun onError(p0: CometChatException?) {
                Log.d("HISTORY", "Message fetching failed with exception: " + p0?.message)

            }
        })
    }

    private fun sendNormalMessage() {
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER

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

                mMessages.add(ChatMessages(p0?.text, "", p0?.sender?.uid, false))
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

    private fun sendMediaMessage() {
        val messageType: String = CometChatConstants.MESSAGE_TYPE_IMAGE
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER

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
                    mMessages.add(ChatMessages("", p0?.attachment?.fileUrl, p0?.sender?.uid, true))
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
        }
    }

}