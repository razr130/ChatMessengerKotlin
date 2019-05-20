package com.example.chatmessenger.Adapter

import com.example.chatmessenger.Model.Users
import com.example.chatmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.contact_row.view.*

class ContactItem(val user: Users): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.contact_username.text = user.username
        Picasso.get().load(user.image).into(viewHolder.itemView.contact_avatar)
    }
    override fun getLayout(): Int {
        return R.layout.contact_row
    }
}
