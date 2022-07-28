package com.example.cometchatdemo.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.core.ConversationsRequest.ConversationsRequestBuilder
import com.cometchat.pro.core.GroupsRequest
import com.cometchat.pro.core.GroupsRequest.GroupsRequestBuilder
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.Group
import com.example.cometchatdemo.R
import com.example.cometchatdemo.ui.FriendsListAdapter.FriendsListAdapterr
import com.example.cometchatdemo.ui.GroupListAdapter.GroupListAdapter
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class FriendsListActivity : AppCompatActivity() {
    var rvfriendsList: RecyclerView? = null
    var friendsadapter: FriendsListAdapterr? = null
    var groupadapter: GroupListAdapter? = null
    var tabLayout: TabLayout? = null
    var tabFriends: TabItem? = null
    var tabGroups: TabItem? = null
    val friends_array = ArrayList<Conversation>()
    val group_array = ArrayList<Conversation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        rvfriendsList = findViewById(R.id.rvfriendsList)
        tabFriends = findViewById(R.id.tabFriends)
        tabGroups = findViewById(R.id.tabGroups)
        getAllFriendsList(CometChatConstants.CONVERSATION_TYPE_USER)
//
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.text == "Friends") {
                    getAllFriendsList(CometChatConstants.CONVERSATION_TYPE_USER)
                } else {
                    getAllFriendsList(CometChatConstants.CONVERSATION_TYPE_GROUP)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private fun getParticipetedGroupList() {
        val groupsRequest = GroupsRequestBuilder()
            .joinedOnly(true)
            .build()
        groupsRequest?.fetchNext(object : CometChat.CallbackListener<List<Group>>() {
            override fun onSuccess(p0: List<Group>?) {
                Log.d("GROUPS_LIST", p0.toString())
                p0!!.forEach {
                    Log.d("GROUPS_LIST", it.name)
                }
//                adapter = FriendsListAdapterr(this@FriendsListActivity, group_array)
//                rvfriendsList!!.adapter = adapter
            }

            override fun onError(p0: CometChatException?) {
                Log.d("GROUPS_LIST", "Groups list fetching failed with exception: " + p0?.message)
            }
        })
    }

    private fun deleteAConversation() {
        CometChat.deleteConversation(
            "superhero1",//UID U WANT TO DELETE CONVERSATION
            CometChatConstants.RECEIVER_TYPE_USER,
            object : CallbackListener<String?>() {
                override fun onSuccess(s: String?) {
                    Log.d("DELETE_CONVERSATION", s!!)
                }

                override fun onError(e: CometChatException) {
                    Log.d("DELETE_CONVERSATION", e.message!!)
                }
            })
    }

    private fun getAllGroupsList() {
        var groupRequest: GroupsRequest?
        val limit = 30

        groupRequest = GroupsRequestBuilder().setLimit(limit).build()

        groupRequest?.fetchNext(object : CometChat.CallbackListener<List<Group>>() {
            override fun onSuccess(p0: List<Group>?) {
                Log.d("GROUPS_LIST", p0.toString())
                p0!!.forEach {
                    Log.d("GROUPS_LIST", it.name)

                }

            }

            override fun onError(p0: CometChatException?) {
                Log.d("GROUPS_LIST", "Groups list fetching failed with exception: " + p0?.message)
            }
        })
    }

    private fun getAllFriendsList(conversationType: String) {
        val conversationsRequest = ConversationsRequestBuilder()
            .setLimit(50)
            .setConversationType(conversationType)
            .build()
        friends_array.clear()
        group_array.clear()
        conversationsRequest?.fetchNext(object : CometChat.CallbackListener<List<Conversation>>() {
            override fun onSuccess(p0: List<Conversation>?) {
                Log.d("USER_LIST", "User list received: " + p0!!.size)
                p0.forEach {
//                    if (conversationType == CometChatConstants.CONVERSATION_TYPE_GROUP) {
//                        val data: JSONObject = (it.lastMessage.rawMessage.get("data") as JSONObject)
//                        val entities: JSONObject =
//                            (data.get("entities") as JSONObject).get("sender") as JSONObject
//                        val sender: JSONObject =
//                            (entities.get("entity") as JSONObject)
//                        Log.d("USER_LIST_STATUS", sender.get("name").toString())
//                    }


                    Log.d("USER_LIST_STATUS", it.conversationId)
                    Log.d("USER_LIST_STATUS", it.toString())
                    Log.d("USER_LIST_STATUS", (it.lastMessage.rawMessage).toString())

                    if (conversationType == CometChatConstants.CONVERSATION_TYPE_USER) {

                        friends_array.add(it)
                    } else {

                        group_array.add(it)
                    }

                }
                if (conversationType == CometChatConstants.CONVERSATION_TYPE_USER) {
                    friendsadapter = FriendsListAdapterr(this@FriendsListActivity, friends_array)
                    rvfriendsList!!.adapter = friendsadapter
                } else {
                    groupadapter = GroupListAdapter(this@FriendsListActivity, group_array)
                    rvfriendsList!!.adapter = groupadapter
                }
            }

            override fun onError(p0: CometChatException?) {
                Log.d("USER_LIST", "User list fetching failed with exception: $p0")
            }
        })
    }
}