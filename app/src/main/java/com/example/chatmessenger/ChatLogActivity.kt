package com.example.chatmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatmessenger.Adapter.ChatFromAdapter
import com.example.chatmessenger.Adapter.ChatToAdapter
import com.example.chatmessenger.Model.ChatMessage
import com.example.chatmessenger.Model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    var touser: Users? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recycler_chat.adapter = adapter

        touser = intent.getParcelableExtra("userdata")
        supportActionBar?.title = touser?.username

        listenformessage()

        BtnSend.setOnClickListener {
            performsendmessage()
        }
    }


    private fun performsendmessage() {
        val text = TxtNewMessage.text.toString()
        val fromid = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<Users>("userdata")
        val toid = user.uid

        if (fromid == null) return

        val ref = FirebaseDatabase.getInstance().getReference("User-Messages").child(fromid.toString()).child(toid).push()
        val toref = FirebaseDatabase.getInstance().getReference("User-Messages").child(toid).child(fromid.toString()).push()
        val chatmessage = ChatMessage(ref.key!!, text, fromid, toid, System.currentTimeMillis() / 1000)
        ref.setValue(chatmessage)
            .addOnSuccessListener {
                TxtNewMessage.text.clear()
                recycler_chat.scrollToPosition(adapter.itemCount-1)
            }
        toref.setValue(chatmessage)
            .addOnSuccessListener { }
        val latestmessageref = FirebaseDatabase.getInstance().getReference("Latest-Message").child(fromid.toString()).child(toid)
        latestmessageref.setValue(chatmessage)
        val latestmessagetoref = FirebaseDatabase.getInstance().getReference("Latest-Message").child(toid).child(fromid.toString())
        latestmessagetoref.setValue(chatmessage)
    }

    private fun listenformessage() {
        val fromid = FirebaseAuth.getInstance().uid
        val toid = touser?.uid
        Toast.makeText(this@ChatLogActivity,"id to : " + touser?.image, Toast.LENGTH_LONG).show()
        val ref = FirebaseDatabase.getInstance().getReference("User-Messages").child(fromid.toString()).child(toid.toString())
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chat = p0.getValue(ChatMessage::class.java)
//                Toast.makeText(this@ChatLogActivity,"id to : " + touser?.uid, Toast.LENGTH_LONG).show()
                if(chat != null){
                    if(chat.formId == FirebaseAuth.getInstance().uid){
                        val currentuser = LatestMessagesActivity.currentuser
                        adapter.add(ChatToAdapter(chat.text, currentuser!!))
                    }
                    else
                    {
                            adapter.add(ChatFromAdapter(chat.text, touser!!))
                    }
                }
                recycler_chat.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }
}
