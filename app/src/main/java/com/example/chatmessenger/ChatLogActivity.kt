package com.example.chatmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.chatmessenger.Adapter.ChatFromAdapter
import com.example.chatmessenger.Adapter.ChatToAdapter
import com.example.chatmessenger.Model.Users
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<Users>("userdata")
        supportActionBar?.title = user.username

        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(ChatToAdapter())
        adapter.add(ChatFromAdapter())
        adapter.add(ChatToAdapter())
        adapter.add(ChatFromAdapter())

        recycler_chat.adapter = adapter
    }
}
