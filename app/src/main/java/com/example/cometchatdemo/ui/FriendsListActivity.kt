package com.example.cometchatdemo.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.ConversationsRequest.ConversationsRequestBuilder
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.User
import com.example.cometchatdemo.R
import com.example.cometchatdemo.ui.FriendsListAdapter.FriendsListAdapterr
import org.json.JSONObject


class FriendsListActivity : AppCompatActivity() {
    var rvfriendsList: RecyclerView? = null
    var adapter: FriendsListAdapterr? = null
    val array = ArrayList<Conversation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        rvfriendsList = findViewById(R.id.rvfriendsList)
        adapter = FriendsListAdapterr(this@FriendsListActivity, array)
        rvfriendsList!!.adapter = adapter
        getAllFriendsList()
    }

    private fun getAllFriendsList() {

        val conversationsRequest = ConversationsRequestBuilder()
            .setLimit(50)
            .setConversationType(CometChatConstants.CONVERSATION_TYPE_USER)
            .build()

        conversationsRequest?.fetchNext(object : CometChat.CallbackListener<List<Conversation>>() {
            override fun onSuccess(p0: List<Conversation>?) {
                Log.d("USER_LIST", "User list received: " + p0!!.size)
                p0.forEach {
                    val user: User = it.conversationWith as User
//                    Log.d("USER_LIST_STATUS", user.status)
                    Log.d("USER_LIST_STATUS",  (it.lastMessage.rawMessage).toString())
                    array.add(it)
                }
                adapter?.notifyDataSetChanged()
            }

            override fun onError(p0: CometChatException?) {
                Log.d("USER_LIST", "User list fetching failed with exception: $p0")
            }
        })
    }
}