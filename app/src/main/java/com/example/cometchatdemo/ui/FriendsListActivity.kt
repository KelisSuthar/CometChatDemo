package com.example.cometchatdemo.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.core.CometChat.CreateGroupWithMembersListener
import com.cometchat.pro.core.ConversationsRequest.ConversationsRequestBuilder
import com.cometchat.pro.core.GroupsRequest
import com.cometchat.pro.core.GroupsRequest.GroupsRequestBuilder
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.GroupMember
import com.example.cometchatdemo.R
import com.example.cometchatdemo.ui.FriendsListAdapter.FriendsListAdapterr
import com.example.cometchatdemo.ui.GroupListAdapter.GroupListAdapter
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class FriendsListActivity : AppCompatActivity() {
    var rvfriendsList: RecyclerView? = null
    var friendsadapter: FriendsListAdapterr? = null
    var groupadapter: GroupListAdapter? = null
    var tabLayout: TabLayout? = null
    var tabFriends: TabItem? = null
    var tabGroups: TabItem? = null
    var addGroup: ImageView? = null
    val friends_array = ArrayList<Conversation>()
    val group_array = ArrayList<Conversation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        rvfriendsList = findViewById(R.id.rvfriendsList)
        tabFriends = findViewById(R.id.tabFriends)
        tabGroups = findViewById(R.id.tabGroups)
        addGroup = findViewById(R.id.addGroup)
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

        addGroup!!.setOnClickListener {
            dialogCreateGroup()
        }

    }

    private fun dialogCreateGroup() {
        val dialog = Dialog(
            this@FriendsListActivity,
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.create_group_dialog_layout)
        dialog.setCancelable(true)

        val SUB = dialog.findViewById<AppCompatButton>(R.id.btn_create_group)
        val grouptype_spinner = dialog.findViewById<Spinner>(R.id.grouptype_spinner)
        val group_name = dialog.findViewById<TextInputEditText>(R.id.group_name)
        val group_desc = dialog.findViewById<TextInputEditText>(R.id.group_desc)
        val group_pwd = dialog.findViewById<TextInputEditText>(R.id.group_pwd)
//        val group_cnf_pwd = dialog.findViewById<TextInputEditText>(R.id.group_cnf_pwd)
//        val group_cnf_pwd = dialog.findViewById<TextInputLayout>(R.id.input_group_cnf_pwd)


        if (grouptype_spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, resources.getStringArray(R.array.groupType)
            )
            grouptype_spinner.adapter = adapter

            grouptype_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    if (position == 2) {
                        group_pwd.visibility = View.VISIBLE
//                        group_cnf_pwd.visibility = View.VISIBLE
                    } else {
                        group_pwd.visibility = View.GONE
//                        group_cnf_pwd.visibility = View.GONE
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
            SUB.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun createGroupWithUsers() {
        val group = Group("test_1", "Test 1", CometChatConstants.GROUP_TYPE_PUBLIC, null)

        val groupMembers: MutableList<GroupMember> = ArrayList()
        groupMembers.add(GroupMember("superhero1", CometChatConstants.SCOPE_ADMIN))

        groupMembers.add(GroupMember("superhero2", CometChatConstants.SCOPE_MODERATOR))

        groupMembers.add(GroupMember("superhero3", CometChatConstants.SCOPE_PARTICIPANT))

        val bannedUIDs: MutableList<String> = ArrayList()
        bannedUIDs.add("superhero4")

        CometChat.createGroupWithMembers(
            group,
            groupMembers,
            bannedUIDs,
            object : CreateGroupWithMembersListener() {
                override fun onSuccess(group: Group, hashMap: HashMap<String?, String?>) {
                    Log.d("CREATE_GROUP_MEMBER", group.toString())
                    Log.d("CREATE_GROUP_MEMBER", hashMap.toString())
                }

                override fun onError(e: CometChatException) {
                    Log.d("CREATE_GROUP_MEMBER", e.message.toString())
                }
            })

    }

    private fun createGroup() {
        val GUID = "GUID"
        val groupName = "Hello Group!"
        val groupType = CometChatConstants.GROUP_TYPE_PUBLIC
        val password = ""

        val group = Group(GUID, groupName, groupType, password)

        CometChat.createGroup(group, object : CallbackListener<Group>() {
            override fun onSuccess(group: Group) {
                Log.d("CREATE_GROUP", "Group created successfully: $group")
            }

            override fun onError(e: CometChatException) {
                Log.d("CREATE_GROUP", "Group creation failed with exception: " + e.message)
            }
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
                Log.d(
                    "GROUPS_LIST",
                    "Groups list fetching failed with exception: " + p0?.message
                )
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
                Log.d(
                    "GROUPS_LIST",
                    "Groups list fetching failed with exception: " + p0?.message
                )
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
        conversationsRequest?.fetchNext(object :
            CometChat.CallbackListener<List<Conversation>>() {
            override fun onSuccess(p0: List<Conversation>?) {
                Log.d("USER_LIST", "User list received: " + p0!!.size)
                p0.forEach {
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
                    friendsadapter =
                        FriendsListAdapterr(this@FriendsListActivity, friends_array)
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