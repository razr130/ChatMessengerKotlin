package com.example.chatmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatmessenger.Model.Users
import com.example.chatmessenger.Prevalent.onlineuser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Paper.init(this)

        BtnLogin.setOnClickListener {
            val email_login = TxtEmailLogin.text.toString()
            val password_login = TxtPasswordLogin.text.toString()

            if (email_login != "" && password_login != "") {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email_login, password_login)
                    .addOnCompleteListener {
                        val uid = FirebaseAuth.getInstance().uid
                        val ref = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())

                        ref.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {

                                onlineuser = p0.getValue(Users::class.java)
                                Toast.makeText(this@LoginActivity, "hello " + onlineuser?.username, Toast.LENGTH_SHORT)
                                    .show()
                                if (onlineuser != null) {
                                    Paper.book().write("user", onlineuser)
                                }
                                openlatestmessage()
                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })


                    }.addOnFailureListener {
                        Toast.makeText(this, "Account didn't exist", Toast.LENGTH_SHORT).show()

                    }
            } else {
                Toast.makeText(this, "Please fill your email and password", Toast.LENGTH_SHORT).show()
            }

        }
        TxtHaveAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openlatestmessage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
