package com.example.chatmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.chatmessenger.Model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        BtnRegis.setOnClickListener {
            regisuser()
        }

        TxtHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        BtnUploadPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectorphotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectorphotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectorphotoUri)
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            BtnUploadPhoto.setBackgroundDrawable(bitmapDrawable)
            BtnCircleImage.setImageBitmap(bitmap)
            BtnUploadPhoto.alpha = 0f
        }
    }

    private fun regisuser() {
        val email_regis = TxtEmailRegis.text.toString()
        val password_regis = TxtPasswordRegis.text.toString()

        if (email_regis.isEmpty() || password_regis.isEmpty()) {
            Toast.makeText(this, "Please fill your email and password", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email_regis, password_regis)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                } else {
                    Toast.makeText(this, "Registration succesfull. UID: ${it.result?.user?.uid}", Toast.LENGTH_SHORT)
                        .show()
                    uploadimage()
                }
            }.addOnFailureListener {
                Log.d("Main", "Failed to create user : ${it.message}")
            }
    }

    private fun uploadimage() {

        if (selectorphotoUri == null) {
            Toast.makeText(this, "image empty", Toast.LENGTH_LONG).show()
            return
        } else {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectorphotoUri!!)
                .addOnSuccessListener {
                    Toast.makeText(this, "upload photo success!", Toast.LENGTH_LONG).show()
                    ref.downloadUrl.addOnSuccessListener {
                        saveuserdatatodatabase(it.toString())
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "upload photo error!", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveuserdatatodatabase(profileimage: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())
        val user = Users(
            uid.toString(),
            TxtUsernameRegis.text.toString(),
            TxtEmailRegis.text.toString(),
            TxtPasswordRegis.text.toString(),
            profileimage
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "success adding to database", Toast.LENGTH_LONG).show()
                openlatestmessage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "error adding to database", Toast.LENGTH_LONG).show()
            }
    }

    private fun openlatestmessage() {
        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
