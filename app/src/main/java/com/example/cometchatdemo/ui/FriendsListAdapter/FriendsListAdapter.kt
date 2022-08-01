package com.example.cometchatdemo.ui.FriendsListAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.User
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants
import com.example.cometchatdemo.ui.ChatAdapter.LoadImg
import com.example.cometchatdemo.ui.MessageListActivity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FriendsListAdapterr(val context: Context, var array: ArrayList<Conversation>) :
    RecyclerView.Adapter<FriendsListAdapterr.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.friends_row_layout, parent, false)
        )
    }

    inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val img_profile: ImageView = itemView!!.findViewById(R.id.img_profile)
        val imgStatus: ImageView = itemView!!.findViewById(R.id.imgStatus)
        val txt_user_name: TextView = itemView!!.findViewById(R.id.txt_user_name)
        val txt_user_message: TextView = itemView!!.findViewById(R.id.txt_user_message)
        val tvTimeStamp: TextView = itemView!!.findViewById(R.id.tvTimeStamp)
        val totalMessage: TextView = itemView!!.findViewById(R.id.totalMessage)
        fun bind(data: Conversation, position: Int) {

            val user: User = data.conversationWith as User
            if (data.lastMessage.rawMessage.get("type") == "image") {
                txt_user_message.text = "Image"
            } else if (data.lastMessage.rawMessage.get("type") == "video") {

                txt_user_message.text = "Video"
            } else {
                txt_user_message.text =
                    (data.lastMessage.rawMessage.get("data") as JSONObject).get("text").toString()
            }
            if (user.avatar.isNullOrEmpty()) {
                img_profile.LoadImg("")
            } else {
                img_profile.LoadImg(user.avatar)
            }
            txt_user_name.text = user.name
            if (data.unreadMessageCount == 0) {
                totalMessage.visibility = View.GONE
            } else {
                totalMessage.visibility = View.VISIBLE
            }
            totalMessage.text = data.unreadMessageCount.toString()
            val date = Date(data.updatedAt)
            val format = SimpleDateFormat("HH:mm a")
            tvTimeStamp.text = format.format(date)
            if (user.status == "online") {
                imgStatus.setImageResource(R.drawable.ic_online)
            } else {
                imgStatus.setImageResource(R.drawable.ic_offline)
            }
            itemView.setOnClickListener {
                context.startActivity(
                    Intent(
                        context,
                        MessageListActivity::class.java
                    ).putExtra(AppConstants.UID, user.uid.toString())
                )
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(array[position], position)
    }

    override fun getItemCount(): Int {
        return array.size
    }

}

fun ImageView.LoadImg(url: String) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.ic_launcher_background)
        .into(this)

}