package com.example.cometchatdemo.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.GroupsRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.example.cometchatdemo.R
import com.example.cometchatdemo.ui.GroupListAdapter.GroupListAdapter
import com.example.cometchatdemo.ui.JoinGroupAdapter.AllGroupsAdapter

class AllGroupListActivity : AppCompatActivity() {
    var rvfriendsList: RecyclerView? = null
    var groupadapter: GroupListAdapter? = null
    val group_array = ArrayList<Group>()
    var allGroupsAdapter: AllGroupsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_group_list)
        rvfriendsList = findViewById(R.id.rvfriendsList)
//        groupadapter = GroupListAdapter(this@AllGroupListActivity, group_array)
//        rvfriendsList!!.adapter = groupadapter
        allGroupsAdapter = AllGroupsAdapter(this@AllGroupListActivity, group_array)
        rvfriendsList!!.adapter = allGroupsAdapter

        getAllGroupsList()

    }


    private fun getParticipetedGroupList() {
        val groupsRequest = GroupsRequest.GroupsRequestBuilder()
            .joinedOnly(false)
            .build()
        groupsRequest?.fetchNext(object : CometChat.CallbackListener<List<Group>>() {
            override fun onSuccess(p0: List<Group>?) {
                Log.d("GROUPS_LIST", p0.toString())
                p0!!.forEach {
                    Log.d("GROUPS_LIST", it.name)
//                    Log.d("GROUPS_LIST", it.isJoined.toString())
                    Log.d("GROUPS_LIST", it.toString())

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

    private fun getAllGroupsList() {
        var groupRequest: GroupsRequest?
        val limit = 30

        groupRequest = GroupsRequest.GroupsRequestBuilder().setLimit(limit).build()

        groupRequest?.fetchNext(object : CometChat.CallbackListener<List<Group>>() {
            override fun onSuccess(p0: List<Group>?) {
                Log.d("GROUPS_LIST", p0.toString())
                p0!!.forEach {
                    Log.d("GROUPS_LIST", it.toString())
                    if(!it.isJoined){
                        group_array.add(it)
                    }

                    allGroupsAdapter?.notifyDataSetChanged()
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
}