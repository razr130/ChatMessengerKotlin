package com.example.chatmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        BtnLogin.setOnClickListener {
            val email_login = TxtEmailLogin.text.toString()
            val password_login = TxtPasswordLogin.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email_login,password_login)
                .addOnCompleteListener {  }
        }
    }
}
