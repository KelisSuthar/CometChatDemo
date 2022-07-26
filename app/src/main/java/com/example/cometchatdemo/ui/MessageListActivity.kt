package com.example.cometchatdemo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.MessageListener
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.CustomMessage
import com.cometchat.pro.models.MediaMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatdemo.R
import com.example.cometchatdemo.ui.ChatAdapter.chatAdapter
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
    var adapter: chatAdapter? = null
    var recyclerView: RecyclerView? = null
    var editText: TextInputEditText? = null
    private val mMessages: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        recyclerView = findViewById(R.id.recyclerview)
        btn = findViewById(R.id.send)
        editText = findViewById(R.id.text)
        file_attech = findViewById(R.id.appCompatImageView)
        image = findViewById(R.id.image)
        adapter = chatAdapter(mMessages)

        btn!!.setOnClickListener(this)
        file_attech!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.send -> {
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
        }
    }

    override fun onResume() {
        super.onResume()
        callHistory()
        val listenerID = "UNIQUE_LISTENER_ID"

        CometChat.addMessageListener(listenerID, object : MessageListener() {
            override fun onTextMessageReceived(textMessage: TextMessage) {
                Log.d("NEW_MSG", "Text message received successfully: $textMessage")
            }

            override fun onMediaMessageReceived(mediaMessage: MediaMessage) {
                Log.d("NEW_MSG", "Media message received successfully: $mediaMessage")
            }

            override fun onCustomMessageReceived(customMessage: CustomMessage) {
                Log.d("NEW_MSG", "Custom message received successfully: $customMessage")
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        val listenerID = "UNIQUE_LISTENER_ID"
        CometChat.removeMessageListener(listenerID)
    }

    private fun callHistory() {
        val messagesRequest: MessagesRequest?
        val UID = "ios_1"
        messagesRequest = MessagesRequest.MessagesRequestBuilder()
            .setLimit(30)
            .setUID(UID)
            .build();
        messagesRequest?.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(p0: List<BaseMessage>?) {
                if (!p0.isNullOrEmpty()) {
                    for (baseMessage in p0) {

                        if (baseMessage is TextMessage) {
                            Log.d("HISTORY", "TextMessage: ${baseMessage.text}")
                        }
                        if (baseMessage is MediaMessage) {
                            Log.d("HISTORY", "MediaMessage: ${baseMessage.attachment.fileUrl}")
                        }
                    }
                }

            }

            override fun onError(p0: CometChatException?) {
                Log.d("HISTORY", "Message fetching failed with exception: " + p0?.message)

            }
        })
    }

    private fun sendNormalMessage() {
        val receiverID = "ios_1"
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER

        val textMessage = TextMessage(receiverID, editText!!.text!!.trim().toString(), receiverType)

        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(p0: TextMessage?) {
                Log.d("SEND_NORMAL_MESSAGE", "Message sent successfully: " + p0?.toString())
                editText!!.text = null
                image!!.visibility = View.GONE
                editText!!.visibility = View.VISIBLE
                file_attech!!.visibility = View.VISIBLE
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
        val receiverID = "ios_1"
        val messageType: String = CometChatConstants.MESSAGE_TYPE_IMAGE
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER

        val mediaMessage = MediaMessage(receiverID, selectedFile, messageType, receiverType)

        CometChat.sendMediaMessage(
            mediaMessage,
            object : CometChat.CallbackListener<MediaMessage>() {
                override fun onSuccess(p0: MediaMessage?) {
                    Log.d(
                        "SEND_MEDIA_MESSAGE",
                        "Media message sent successfully: " + p0?.toString()
                    )
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