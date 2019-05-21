package com.example.chatmessenger.Adapter

import com.example.chatmessenger.Model.ChatMessage
import com.example.chatmessenger.Model.Users
import com.example.chatmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.contact_row.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessage(val chatmessage: ChatMessage?) : Item<ViewHolder>() {
    var chatpartner: Users? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.TxtLatestMessage.text = chatmessage?.text
        val chatpartnerid: String
        if (chatmessage?.formId == FirebaseAuth.getInstance().uid) {
            chatpartnerid = chatmessage?.toId.toString()
        } else {
            chatpartnerid = chatmessage?.formId.toString()
        }

        val ref = FirebaseDatabase.getInstance().getReference("Users").child(chatpartnerid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatpartner = p0.getValue(Users::class.java)
                viewHolder.itemView.TxtusernameLatest.text = chatpartner?.username
                Picasso.get().load(chatpartner?.image).into(viewHolder.itemView.image_latest)
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}
