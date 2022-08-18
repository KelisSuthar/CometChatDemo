package com.example.cometchatdemo.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.core.UsersRequest
import com.cometchat.pro.core.UsersRequest.UsersRequestBuilder
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.GroupMember
import com.cometchat.pro.models.User
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants
import com.example.cometchatdemo.ui.UserListAdapter.AllUserListAdapter
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout


class AllUsersList : AppCompatActivity() {
    var rvList: RecyclerView? = null
    var usersRequest: UsersRequest? = null
    var allUserListAdapter: AllUserListAdapter? = null
    var limit = 30
    var array = ArrayList<User>()
    var selectedUUidList = ArrayList<GroupMember>()
    var tabLayout: TabLayout? = null
    var tabFriends: TabItem? = null
    var tabAllUsers: TabItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users_list)
        rvList = findViewById(R.id.rvList)
        tabLayout = findViewById(R.id.tabLayout)
        tabFriends = findViewById(R.id.tabFriends)
        tabAllUsers = findViewById(R.id.tabAllUsers)
        setAdapter()

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                array.clear()
                allUserListAdapter!!.notifyDataSetChanged()
                usersRequest = if (tab.text == "Friends") {
                    UsersRequestBuilder()
                        .setLimit(limit)
                        .friendsOnly(true)
                        .build();
                } else {
                    UsersRequestBuilder().setLimit(30).build()

                }
                getAllUsers()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        //TODO:Get Detials of Logged in user if there are null resp than no user logged in
        val user = CometChat.getLoggedInUser()
        Log.d("LOGGED_IN_USER", user.toString())

        usersRequest = UsersRequestBuilder().setLimit(30).build()

        //TODO:Set Search Keyword
//        usersRequest = UsersRequestBuilder()
//            .setLimit(limit)
//            .setSearchKeyword("abc")
//            .build()
        //TODO:List Of Online Offline users
//       usersRequest = UsersRequestBuilder()
//            .setLimit(limit)
//            .setUserStatus(UsersRequest.USER_STATUS_ONLINE)
//            .build()
//        CometChat.USER_STATUS.ONLINE - will return the list of only online users.
//        CometChat.USER_STATUS.OFFLINE - will return the list of only offline users.
        //TODO:List Of Blocked Unblocked Users
//        usersRequest = UsersRequestBuilder()
//            .setLimit(limit)
//            .hideBlockedUsers(true)
//            .build()
        //TODO:List Of friend(if False than return all users)
//       usersRequest = UsersRequestBuilder()
//            .setLimit(limit)
//            .friendsOnly(true)
//            .build()
//
        // TODO:List Of Sorted accordingly
//        usersRequest = UsersRequestBuilder()
//            .setLimit(20)
//            .sortBy(CometChatConstants.SORT_BY_NAME)
////            .sortBy(CometChatConstants.SORT_BY_STATUS)
////            .sortByOrder(CometChatConstants.SORT_ORDER_ASCENDING)
////            .sortByOrder(CometChatConstants.SORT_ORDER_DESCENDING)
//            .build()
        getAllUsers()
        getPerticulerUserDetails()
        getCountForAllOnlineUserForApp()
    }

    private fun setAdapter() {

        allUserListAdapter =
            AllUserListAdapter(this, array, object : AllUserListAdapter.onItemClick {
                override fun addItem(user: User, position: Int) {
                    selectedUUidList.add(
                        GroupMember(
                            user.uid,
                            CometChatConstants.SCOPE_PARTICIPANT
                        )
                    )
                }

                override fun removeItem(user: User, position: Int) {
                    var POS = -1

                    selectedUUidList.forEachIndexed { index, s ->
                        if (s.uid == user.uid) {
                            POS = index
                        }
                        if (selectedUUidList.size == index + 1) {
                            selectedUUidList.removeAt(POS)
                        }
                    }


                }

            })
        rvList!!.adapter = allUserListAdapter
    }

    private fun getCountForAllOnlineUserForApp() {
        CometChat.getOnlineUserCount(object : CallbackListener<Int>() {
            override fun onSuccess(count: Int) {
                Log.d("ONLINE_USER_NUM", "Online users : $count")
            }

            override fun onError(e: CometChatException) {
                Log.d("ONLINE_USER_NUM", "Error : " + e.message)
            }
        })
    }

    private fun getPerticulerUserDetails() {
        val UID: String = AppConstants.MY_UID

        CometChat.getUser(UID, object : CometChat.CallbackListener<User>() {
            override fun onSuccess(p0: User?) {
                Log.d("USER_DETAILS", "User details fetched for user: " + p0?.toString())
            }

            override fun onError(p0: CometChatException?) {
                Log.d("USER_DETAILS", "User details fetching failed with exception: " + p0?.message)
            }
        })
    }

    private fun getAllUsers() {
        usersRequest?.fetchNext(object : CometChat.CallbackListener<List<User>>() {
            override fun onSuccess(p0: List<User>?) {

                Log.d("USER_LIST", "User list received: " + p0?.size)
                p0!!.forEach {
                    Log.d("USER_LIST", it.toString())
                    array.add(it)
                }
                allUserListAdapter!!.notifyDataSetChanged()
            }

            override fun onError(p0: CometChatException?) {
                Log.d("USER_LIST", "User list fetching failed with exception: " + p0?.message)
            }
        })
    }

    override fun onBackPressed() {
        Log.i("SELECTED_UID_LIST", selectedUUidList.toString())
        if (selectedUUidList.isNullOrEmpty()) {
            super.onBackPressed()
        } else {
            alertDialog()
        }
    }

    private fun alertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@AllUsersList)
        builder.setTitle("Alert Dialog!!")
        builder.setMessage("Do you want add selected member(s) to your group?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                dialog.cancel()
                addMemberToGroup(selectedUUidList)
            }
            .setNegativeButton(
                "No"
            ) { dialog, id ->
                dialog.cancel()

            }

        val alert: AlertDialog = builder.create()
        alert.setTitle("AlertDialogExample")
        alert.show()
    }

    private fun addMemberToGroup(uidList: ArrayList<GroupMember>) {


        CometChat.addMembersToGroup(intent.extras!!.get(AppConstants.UID).toString(),uidList,null,object :CometChat.CallbackListener<HashMap<String,String>>(){

            override fun onSuccess(p0: HashMap<String, String>?) {
                Log.d("MEMBERS_ADD", p0.toString())
                selectedUUidList.clear()


            }

            override fun onError(p0: CometChatException?) {
                Log.d("MEMBERS_ADD", "Error while adding participants"+p0.toString())
            }

        })
    }
}