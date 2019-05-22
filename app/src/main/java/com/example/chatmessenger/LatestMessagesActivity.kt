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

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentuser: Users? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)



        val headerResult = AccountHeaderBuilder()
            .withActivity(this)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                ProfileDrawerItem().withName(currentuser?.username).withEmail(currentuser?.email).withIcon(
                    resources.getDrawable(
                        R.drawable.user
                    )
                )
            )
            .withOnAccountHeaderListener { _, _, _ -> false }
            .build()

        val item2 = PrimaryDrawerItem().withIdentifier(1).withName("Add More Pokemons!")

//create the drawer and remember the `Drawer` result object
        val result = DrawerBuilder()
            .withActivity(this)
            .withAccountHeader(headerResult)
            .withToolbar(toolbarMain)
            .addDrawerItems(
                DividerDrawerItem(),
                item2
            )
            .withOnDrawerItemClickListener { _, _, drawerItem ->
                if (drawerItem != null) {
                    var intent: Intent? = null
                    if (drawerItem.identifier == 1L) {
                        intent = Intent(this, NewMessageActivity::class.java)
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

            intent.putExtra("userdata",row.chatpartner)
            startActivity(intent)

        }
        fab.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        listenforlatestmessage()
        fetchcurrentuser()
        verifyuserlogin()
    }

    val messagemap = HashMap<String, ChatMessage?>()

    private fun listenforlatestmessage() {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("Latest-Message").child(fromid.toString())
        ref.addChildEventListener(object: ChildEventListener{
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
        messagemap.values.forEach{
           adapter.add(LatestMessage(it))
        }
    }

    val adapter = GroupAdapter<ViewHolder>()
    private fun fetchcurrentuser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentuser = p0.getValue(Users::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyuserlogin() {
        val uid = FirebaseAuth.getInstance().uid
//        Toast.makeText(this, uid, Toast.LENGTH_LONG).show()
        if(uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when(item?.itemId){
//            R.id.menu_newmessaage ->
//            {
//                val intent = Intent(this, NewMessageActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.menu_signout ->
//            {
//                FirebaseAuth.getInstance().signOut()
//                val intent = Intent(this, RegisterActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.nav_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
}
