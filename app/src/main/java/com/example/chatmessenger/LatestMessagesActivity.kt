package com.example.chatmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.chatmessenger.Adapter.ContactItem
import com.example.chatmessenger.Adapter.LatestMessage
import com.example.chatmessenger.Model.ChatMessage
import com.example.chatmessenger.Model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import com.squareup.picasso.Picasso
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import com.example.chatmessenger.Prevalent.onlineuser
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import io.paperdb.Paper
import android.support.v7.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider


class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentuser: Users? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        Paper.init(this)
        currentuser = Paper.book().read<Users>("user")
        if (currentuser == null) {
            verifyuserlogin()
            //Toast.makeText(this, "taraaaiaiaiaiai", Toast.LENGTH_LONG).show()
        } else {
            //Toast.makeText(this, "taraaaoaoaoaoa", Toast.LENGTH_LONG).show()

            authenticateUser()

            if (currentuser!!.username != "" && currentuser!!.email != "" && currentuser!!.image != "") {
                if (!TextUtils.isEmpty(currentuser!!.username) && !TextUtils.isEmpty(currentuser!!.email) && !TextUtils.isEmpty(
                        currentuser!!.image
                    )
                ) {
                    //Toast.makeText(this, "taraaa", Toast.LENGTH_LONG).show()
                    DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
                        override fun set(imageView: ImageView?, uri: Uri?, placeholder: Drawable?) {
                            Picasso.get().load(uri).placeholder(placeholder!!).into(imageView)
                        }

                        override fun cancel(imageView: ImageView?) {
                            Picasso.get().cancelRequest(imageView!!)
                        }

                    })
                    //Toast.makeText(this, "making header with data", Toast.LENGTH_LONG).show()
                    val headerResult = AccountHeaderBuilder()
                        .withActivity(this)
                        .withHeaderBackground(R.drawable.header)
                        .addProfiles(

                            ProfileDrawerItem().withName(currentuser!!.username).withEmail(currentuser!!.email).withIcon(
                                currentuser!!.image
                            ).withIdentifier(100)
                        )
                        .withOnAccountHeaderListener { _, _, _ -> false }
                        .build()

                    val item2 = PrimaryDrawerItem().withIdentifier(1).withName("Edit Profile")
                    val item3 = PrimaryDrawerItem().withIdentifier(2).withName("Sign Out")
                    val item4 = PrimaryDrawerItem().withIdentifier(3).withName("Delete Account")

                    val result = DrawerBuilder()
                        .withActivity(this)
                        .withAccountHeader(headerResult)
                        .withToolbar(toolbarMain)
                        .addDrawerItems(

                            item2,
                            DividerDrawerItem(),
                            item4,
                            item3
                        )
                        .withOnDrawerItemClickListener { _, _, drawerItem ->
                            if (drawerItem != null) {
                                var intent: Intent? = null
                                if (drawerItem.identifier == 1L) {
                                    intent = Intent(this, EditProfileActivity::class.java)
                                } else if (drawerItem.identifier == 2L) {
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    onlineuser = null
                                    Paper.book().destroy()
                                } else if (drawerItem.identifier == 3L) {
                                    showDialog()

                                }
                                if (intent != null) {
                                    this@LatestMessagesActivity.startActivity(intent)
                                }
                            }
                            false
                        }
                        .build()


                    recycler_latestchat.adapter = adapter
                    recycler_latestchat.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

                    adapter.setOnItemClickListener { item, view ->
                        //            val useritem = item as ContactItem
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        val row = item as LatestMessage

                        intent.putExtra("userdata", row.chatpartner)
                        startActivity(intent)

                    }
                    fab.setOnClickListener {
                        val intent = Intent(this, NewMessageActivity::class.java)
                        startActivity(intent)
                    }

                    listenforlatestmessage()
                }
            }
            else{
                Toast.makeText(this, "data empty?", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun authenticateUser() {
        val user = FirebaseAuth.getInstance().currentUser


        val credential = EmailAuthProvider
            .getCredential(currentuser!!.email, currentuser!!.password)

        user?.reauthenticate(credential)
            ?.addOnCompleteListener { }
    }


    private fun showDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete User")
        builder.setMessage("Are you sure you want to delete your account, " + currentuser?.username + "?")


        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    FirebaseAuth.getInstance().currentUser?.delete()
                    deletedatafromdatabase()
                    onlineuser = null
                    Paper.book().destroy()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
                DialogInterface.BUTTON_NEUTRAL -> dialog.cancel()
            }
        }

        builder.setPositiveButton("YES", dialogClickListener)
        builder.setNegativeButton("NO", dialogClickListener)
        builder.setNeutralButton("CANCEL", dialogClickListener)

        dialog = builder.create()
        dialog.show()
    }

    private fun deletedatafromdatabase() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())
        ref.removeValue()
    }

    val messagemap = HashMap<String, ChatMessage?>()

    private fun listenforlatestmessage() {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("Latest-Message").child(fromid.toString())
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmessage = p0.getValue(ChatMessage::class.java)
                messagemap[p0.key!!] = chatmessage
                refreshrecyclerviewmessage()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatmessage = p0.getValue(ChatMessage::class.java)
                messagemap[p0.key!!] = chatmessage
                refreshrecyclerviewmessage()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun refreshrecyclerviewmessage() {
        adapter.clear()
        messagemap.values.forEach {
            adapter.add(LatestMessage(it))
        }
    }

    val adapter = GroupAdapter<ViewHolder>()


    private fun verifyuserlogin() {
        val uid = FirebaseAuth.getInstance().uid
        //Toast.makeText(this, "verivy user", Toast.LENGTH_LONG).show()
//        Toast.makeText(this, uid, Toast.LENGTH_LONG).show()
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}
