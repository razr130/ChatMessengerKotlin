package com.example.chatmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatmessenger.Prevalent.onlineuser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        TxtUsernameEdit.setText(onlineuser?.username)
        Toast.makeText(this, onlineuser?.image, Toast.LENGTH_LONG).show()
        Picasso.get().load(onlineuser?.image).into(pic_edit)

    }
}
