package com.example.chatmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.chatmessenger.Adapter.ContactItem
import com.example.chatmessenger.Model.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select Contact"

        fetchuser()
    }

    private fun fetchuser() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            val adapter = GroupAdapter<ViewHolder>()
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(Users::class.java)
                    if (user != null) {
                        adapter.add(ContactItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val useritem = item as ContactItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra("userdata",useritem.user)
                    startActivity(intent)
                    finish()
                }
                recycler_contact.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
