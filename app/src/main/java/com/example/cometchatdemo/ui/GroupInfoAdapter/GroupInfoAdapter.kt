package com.example.cometchatdemo.ui.GroupInfoAdapter


import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.models.GroupMember
import com.example.cometchatdemo.R
import com.example.cometchatdemo.constants.AppConstants


class GroupInfoAdapter(
    var context: Context,
    var isAdmin: Boolean,
    var owner: String,
    var array: ArrayList<GroupMember>,
    var listener: ItemClick
) :
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


            if (owner == data.uid) {
                ivAdminIndicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                ivAdminIndicator.visibility = View.VISIBLE
            } else if (data.scope.toString() == CometChatConstants.SCOPE_ADMIN) {
                ivAdminIndicator.visibility = View.VISIBLE
            } else if (data.scope.toString() == CometChatConstants.SCOPE_MODERATOR) {
                ivAdminIndicator.visibility = View.VISIBLE
                ivAdminIndicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.grey),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else {
                ivAdminIndicator.visibility = View.GONE
            }
            txtName.text = data.name
            itemView.setOnLongClickListener {
                if (data.uid != AppConstants.MY_UID) {
                    if (isAdmin) {
                        setDialog(context, data)
                    }

                }

                true
            }
            itemView.setOnClickListener {
                if (data.uid != AppConstants.MY_UID) {
                    if (isAdmin) {
                        showMenu(context, data, itemView)
                    }
                }
            }
        }
    }

    private fun setDialog(context: Context, data: GroupMember) {
        val dialog = Dialog(
            context,
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.chnage_member_scop_dialog_layout)
        dialog.setCancelable(true)

        val tvQ = dialog.findViewById<TextView>(R.id.tvQ)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        val btnSub = dialog.findViewById<AppCompatButton>(R.id.btnSub)
        val rbAdmin = dialog.findViewById<RadioButton>(R.id.rbAdmin)
        val rbModerator = dialog.findViewById<RadioButton>(R.id.rbModerator)
        val rbParticipant = dialog.findViewById<RadioButton>(R.id.rbParticipant)
        tvQ.text = "Do you want to change scope of " + data.name + "?"
        when (data.scope) {
            CometChatConstants.SCOPE_ADMIN -> {
                rbAdmin.isChecked = true
            }
            CometChatConstants.SCOPE_MODERATOR -> {
                rbModerator.isChecked = true
            }
        }

        btnSub.setOnClickListener {
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(context, "PLease select one of them", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
                when (radioGroup.checkedRadioButtonId) {
                    R.id.rbAdmin -> {
                        listener.onScopChange(data, CometChatConstants.SCOPE_ADMIN)
                    }
                    R.id.rbModerator -> {
                        listener.onScopChange(data, CometChatConstants.SCOPE_MODERATOR)
                    }
                    R.id.rbParticipant -> {
                        listener.onScopChange(data, CometChatConstants.SCOPE_PARTICIPANT)
                    }
                }
            }

        }

        dialog.show()
    }


    private fun showMenu(context: Context, data: GroupMember, itemView: View) {
        val popupMenu = PopupMenu(context, itemView)

        popupMenu.menuInflater.inflate(
            R.menu.popup_menu_2,
            popupMenu.menu
        )
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.removeMember -> {
                    listener.onRemoveClick(data)
                }
                R.id.banMember -> {
                    listener.onBanClick(data)
                }
            }

            true
        }

        popupMenu.show()
    }

    interface ItemClick {
        fun onRemoveClick(data: GroupMember)
        fun onBanClick(data: GroupMember)
        fun onScopChange(data: GroupMember, new_scop: String)
    }

    fun ImageView.LoadImg(url: String) {
        Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_img_placeholder)
            .into(this)

    }

}