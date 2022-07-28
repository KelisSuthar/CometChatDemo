package com.example.cometchatdemo.ui.GroupListAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.Group
import com.example.cometchatdemo.R
import com.example.cometchatdemo.ui.ChatAdapter.LoadImg
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class GroupListAdapter(val context: Context, var array: ArrayList<Conversation>) :
    RecyclerView.Adapter<GroupListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupListAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.friends_row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GroupListAdapter.MyViewHolder, position: Int) {
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
        fun bind(data: Conversation, position: Int) {
            val group: Group = data.conversationWith as Group
            val date = Date(data.updatedAt)
            val format = SimpleDateFormat("HH:mm a")
            tvTimeStamp.text = format.format(date)
            if (data.lastMessage.rawMessage.get("type") == "image") {
                txt_user_message.text = "Image"
            } else if (data.lastMessage.rawMessage.get("type") == "video") {

                txt_user_message.text = "Video"
            } else {
                val parent: JSONObject = (data.lastMessage.rawMessage.get("data") as JSONObject)
                val entities: JSONObject =
                    (parent.get("entities") as JSONObject).get("sender") as JSONObject
                val sender: String =
                    (entities.get("entity") as JSONObject).get("name").toString()
                txt_user_message.text = "$sender: " +
                        parent.get("text")
                            .toString()
            }
            if (group.icon.isNullOrEmpty()) {
                img_profile.LoadImg("")
            } else {
                img_profile.LoadImg(group.icon)
            }
            txt_user_name.text = group.name
            if (data.unreadMessageCount == 0) {
                totalMessage.visibility = View.GONE
            } else {
                totalMessage.visibility = View.VISIBLE
            }
            totalMessage.text = data.unreadMessageCount.toString()

            imgStatus.visibility = View.GONE


//            itemView.setOnClickListener {
//                context.startActivity(
//                    Intent(
//                        context,
//                        MessageListActivity::class.java
//                    ).putExtra(AppConstants.UID, group.guid.toString())
//                )
//            }
        }
    }
}