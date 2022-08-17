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
import com.example.cometchatdemo.dataclass.ChatMessages
import java.text.SimpleDateFormat

class chatAdapter(var array: ArrayList<ChatMessages>, var b: Boolean) :
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
        var txtRecieveMsgName: TextView = itemView.findViewById(R.id.txtRecieveMsgName)
        var txtSendMsgName: TextView = itemView.findViewById(R.id.txtSendMsgName)

        fun bind(data: ChatMessages, position: Int) {
            txtSendMsg.text = data.msg
            txtRecieveMsg.text = data.msg


            val format = SimpleDateFormat("hh:mm a")
            if (b) {
                txtRecieveMsgName.text =
                    data.sender_name + "\n" + format.format((data.message_time!! + "000").toLong())
                txtSendMsgName.text =
                    data.sender_name + "\n" + format.format((data.message_time!! + "000").toLong())
            } else {
                txtRecieveMsgName.text = format.format(data.message_time!!.toLong())
                txtSendMsgName.text = format.format(data.message_time.toLong())
            }
            if (array[position].sender == AppConstants.MY_UID) {
                txtSendMsg.visibility = View.VISIBLE
                txtRecieveMsg.visibility = View.GONE
                txtRecieveMsgName.visibility = View.GONE
                txtSendMsgName.visibility = View.VISIBLE


            } else {
                txtRecieveMsg.visibility = View.VISIBLE
                txtSendMsg.visibility = View.GONE
                txtSendMsgName.visibility = View.GONE
                txtRecieveMsgName.visibility = View.VISIBLE

            }

        }

    }

    inner class MessageViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgLeft: ImageView = itemView.findViewById(R.id.imgLeft)
        var imgRight: ImageView = itemView.findViewById(R.id.imgRight)
        var txtRecieveMsgName: TextView = itemView.findViewById(R.id.txtRecieveMsgName)
        var txtSendMsgName: TextView = itemView.findViewById(R.id.txtSendMsgName)
        fun bind(data: ChatMessages, position: Int) {
            imgLeft.LoadImg(data.url.toString())
            imgRight.LoadImg(data.url.toString())
            val format = SimpleDateFormat("hh:mm a")
            if (b) {
                txtRecieveMsgName.text =
                    data.sender_name + "\n" + format.format((data.message_time!! + "000").toLong())
                txtSendMsgName.text =
                    data.sender_name + "\n" + format.format((data.message_time!! + "000").toLong())
            } else {
                txtRecieveMsgName.text = format.format(data.message_time!!.toLong())
                txtSendMsgName.text = format.format(data.message_time.toLong())
            }
            if (array[position].sender == AppConstants.MY_UID) {
                imgRight.visibility = View.VISIBLE
                imgLeft.visibility = View.GONE
                txtRecieveMsgName.visibility = View.GONE
                txtSendMsgName.visibility = View.VISIBLE
            } else {
                txtSendMsgName.visibility = View.GONE
                txtRecieveMsgName.visibility = View.VISIBLE
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
