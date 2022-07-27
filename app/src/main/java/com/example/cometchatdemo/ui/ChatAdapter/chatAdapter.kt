package com.example.cometchatdemo.ui.ChatAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants
import com.example.pubnubchatdemo.dataclass.ChatMessages

class chatAdapter(var array: ArrayList<ChatMessages>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var MSG = 0
    var IMG = 1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            MSG -> {
                return MessageViewHolder1(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_row_layout_msg, parent, false)
                )
            }
            IMG -> {
                MessageViewHolder2(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_row_layout_img, parent, false)
                )
            }
            else -> {
                return MessageViewHolder1(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_row_layout_msg, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageViewHolder1 -> holder.bind(array[position], position)
            is MessageViewHolder2 -> holder.bind(array[position], position)
        }

    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class MessageViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtSendMsg: TextView = itemView.findViewById(R.id.txtSendMsg)
        var txtRecieveMsg: TextView = itemView.findViewById(R.id.txtRecieveMsg)


        fun bind(data: ChatMessages, position: Int) {
            txtSendMsg.text = data.msg
            txtRecieveMsg.text = data.msg
            if (array[position].sender == AppConstants.MY_UID) {
                txtSendMsg.visibility = View.VISIBLE
                txtRecieveMsg.visibility = View.GONE
            } else {
                txtRecieveMsg.visibility = View.VISIBLE
                txtSendMsg.visibility = View.GONE
            }

        }

    }

    inner class MessageViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgLeft: ImageView = itemView.findViewById(R.id.imgLeft)
        var imgRight: ImageView = itemView.findViewById(R.id.imgRight)

        fun bind(data: ChatMessages, position: Int) {
            imgLeft.LoadImg(data.url.toString())
            imgRight.LoadImg(data.url.toString())
            if (array[position].sender == AppConstants.MY_UID) {
                imgRight.visibility = View.VISIBLE
                imgLeft.visibility = View.GONE
            } else {
                imgRight.visibility = View.GONE
                imgLeft.visibility = View.VISIBLE
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        val type: Int = if (array[position].is_image == true) {
            IMG
        } else {
            MSG
        }
        return type
    }
}

fun ImageView.LoadImg(url: String) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.ic_img_placeholder)
        .into(this)

}
