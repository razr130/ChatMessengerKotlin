package com.example.chatmessenger.Adapter

import com.example.chatmessenger.Model.Users
import com.example.chatmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.contact_row.view.*

class ChatFromAdapter: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToAdapter: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}