package com.example.chatmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.RecyclerView
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_chat -> {
                setfragments(ChatListFragment())
                TxtToolbarTitle.setText("Chat")
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_friend -> {
                setfragments(FriendFragment())
                TxtToolbarTitle.setText("Friend List")
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_setting -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        main_bottom_navbar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setfragments(ChatListFragment())

    }

    private fun setfragments(frags: Fragment) {
        val fragmenttransaction = supportFragmentManager.beginTransaction()
        fragmenttransaction.replace(R.id.the_frame, frags)
        fragmenttransaction.commit()
    }
}
