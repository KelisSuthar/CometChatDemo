package com.example.cometchatdemo.ui.JoinGroupAdapter


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants
import com.example.cometchatdemo.ui.ChatAdapter.LoadImg
import com.example.cometchatdemo.ui.GroupMessageListActivity
import java.text.SimpleDateFormat


class AllGroupsAdapter(val context: Context, var array: ArrayList<Group>) :
    RecyclerView.Adapter<AllGroupsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllGroupsAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.friends_row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AllGroupsAdapter.MyViewHolder, position: Int) {
        holder.bind(array[position], position)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val img_profile: ImageView = itemView!!.findViewById(R.id.img_profile)
        val imgStatus: ImageView = itemView!!.findViewById(R.id.imgStatus)
        val txt_user_name: TextView = itemView!!.findViewById(R.id.txt_user_name)
        val txt_user_message: TextView = itemView!!.findViewById(R.id.txt_user_message)
        val tvTimeStamp: TextView = itemView!!.findViewById(R.id.tvTimeStamp)
        val totalMessage: TextView = itemView!!.findViewById(R.id.totalMessage)
        fun bind(data: Group, position: Int) {
            tvTimeStamp.visibility = View.GONE
            totalMessage.visibility = View.GONE
            val format = SimpleDateFormat("HH:mm a")
            tvTimeStamp.text = format.format(data.updatedAt)
            if (data.icon.isNullOrEmpty()) {
                img_profile.LoadImg("")
            } else {
                img_profile.LoadImg(data.icon)
            }
            txt_user_name.text = data.name
            txt_user_message.text = data.membersCount.toString() + " :Members"

            imgStatus.visibility = View.GONE


            itemView.setOnClickListener {
                setDialog(context, data.guid, data.groupType)

//                )
            }
        }
    }

    private fun setDialog(context: Context, guid: String, groupType: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Alert Dialog!!")

        builder.setMessage("Do you want to close this application ?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                dialog.cancel()
                joinGroup(guid, groupType)
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

    private fun joinGroup(guid: String, groupType: String) {

        CometChat.joinGroup(guid, groupType, "password", object : CallbackListener<Group>() {
            override fun onSuccess(joinedGroup: Group) {
                Log.d("JOIN_GROUP", joinedGroup.toString())
                context.startActivity(
                    Intent(
                        context,
                        GroupMessageListActivity::class.java
                    ).putExtra(AppConstants.UID, joinedGroup.guid.toString())
                )
            }

            override fun onError(e: CometChatException) {
                Log.d("JOIN_GROUP", "Group joining failed with exception: " + e.message)
            }
        })

    }
}