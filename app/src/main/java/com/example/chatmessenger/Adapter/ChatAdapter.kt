package com.example.chatmessenger.Adapter

import com.example.chatmessenger.Model.Users
import com.example.chatmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.contact_row.view.*

class ChatFromAdapter(val text: String, val user: Users): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.itemView.from_message.text = text
        val uri = user.image
        Picasso.get().load(uri).into(viewHolder.itemView.from_avatar)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToAdapter(val text: String, val user: Users): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.to_message.text = text

        val uri = user.image
        Picasso.get().load(uri).into(viewHolder.itemView.to_avatar)
    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}