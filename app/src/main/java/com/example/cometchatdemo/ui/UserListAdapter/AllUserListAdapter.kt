package com.example.cometchatdemo.ui.UserListAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.ForwardingListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.models.User
import com.example.cometchatdemo.R
import com.example.cometchatdemo.ui.ChatAdapter.LoadImg

class AllUserListAdapter(val context: Context, var array: ArrayList<User>,var listener: onItemClick) :
    RecyclerView.Adapter<AllUserListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllUserListAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.friends_row_layout, parent, false)
        )
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
        val checkbox: AppCompatCheckBox = itemView!!.findViewById(R.id.checkbox)
        fun bind(data: User, position: Int) {
            txt_user_message.text = ""
            tvTimeStamp.visibility = View.GONE
            totalMessage.visibility = View.GONE
            checkbox.visibility = View.VISIBLE

            itemView.setOnClickListener {
                checkbox.isChecked = !checkbox.isChecked
            }
            if (data.avatar.isNullOrEmpty()) {
                img_profile.LoadImg("")
            } else {
                img_profile.LoadImg(data.avatar)
            }
            txt_user_name.text = data.name
            if (data.status == "online") {
                imgStatus?.setImageResource(R.drawable.ic_online)
            } else {
                imgStatus?.setImageResource(R.drawable.ic_offline)
            }
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // show toast , check box is checked
                    listener.addItem(data,position)
                } else {
                    // show toast , check box is not checked
                    listener.removeItem(data,position)
                }
            }
        }
    }

    interface onItemClick {
        fun addItem(user: User,position: Int)
        fun removeItem(user: User,position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(array[position], position)
    }
    fun ImageView.LoadImg(url: String) {
        Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_img_placeholder)
            .into(this)

    }
}