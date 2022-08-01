package com.example.cometchatdemo.ui.GroupInfoAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.models.GroupMember
import com.example.cometchatdemo.R

class GroupInfoAdapter(var array: ArrayList<GroupMember>) :
    RecyclerView.Adapter<GroupInfoAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupInfoAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.group_user_row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(array[position], position)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfileImg: ImageView = itemView.findViewById(R.id.ivProfileImg)
        var txtName: TextView = itemView.findViewById(R.id.txtName)
        var ivAdminIndicator: ImageView = itemView.findViewById(R.id.ivAdminIndicator)
        fun bind(data: GroupMember, position: Int) {
            if (data.avatar.isNullOrEmpty()) {
                ivProfileImg.LoadImg("")
            } else {
                ivProfileImg.LoadImg(data.avatar)
            }
            if(data.scope.toString() == "admin")
            {
                ivAdminIndicator.visibility = View.VISIBLE
            }else{
                ivAdminIndicator.visibility = View.GONE
            }
            txtName.text = data.name
        }

    }

    fun ImageView.LoadImg(url: String) {
        Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_img_placeholder)
            .into(this)

    }

}