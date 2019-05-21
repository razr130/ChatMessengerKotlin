package com.example.chatmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        BtnLogin.setOnClickListener {
            val email_login = TxtEmailLogin.text.toString()
            val password_login = TxtPasswordLogin.text.toString()

            if (email_login != "" && password_login != "") {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email_login, password_login)
                    .addOnCompleteListener {
                        openlatestmessage()
                    }
            } else {
                Toast.makeText(this, "Please fill your email and password", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun openlatestmessage() {
        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
