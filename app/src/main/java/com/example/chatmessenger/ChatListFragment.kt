package com.example.chatmessenger

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatmessenger.Adapter.LatestMessage
import com.example.chatmessenger.Model.ChatMessage
import com.example.chatmessenger.Model.Users
import com.example.chatmessenger.Prevalent.onlineuser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import io.paperdb.Paper
import kotlinx.android.synthetic.main.fragment_chat_list.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChatListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ChatListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ChatListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    companion object {
        var currentuser: Users? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val chatlist = inflater.inflate(R.layout.fragment_chat_list, container, false)
        Paper.init(this@ChatListFragment.context)
        currentuser = Paper.book().read<Users>("user")
        verifyuserlogin()

        listenforlatestmessage()

        var recyclerlatestchat = chatlist.findViewById(R.id.recycler_chat) as? RecyclerView
        recyclerlatestchat?.layoutManager = LinearLayoutManager(activity)
        Toast.makeText(this@ChatListFragment.context, "haaa",Toast.LENGTH_LONG).show()


        recyclerlatestchat?.addItemDecoration(
            DividerItemDecoration(
                this@ChatListFragment.context,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setOnItemClickListener { item, view ->
            //            val useritem = item as ContactItem
            val intent = Intent(view.context, ChatLogActivity::class.java)
            val row = item as LatestMessage

            intent.putExtra("userdata", row.chatpartner)
            startActivity(intent)

        }
        var fab = chatlist.findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val intent = Intent(this@ChatListFragment.context, NewMessageActivity::class.java)
            startActivity(intent)
        }



        return chatlist
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
                recycler_latestchat.adapter = adapter
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatmessage = p0.getValue(ChatMessage::class.java)
                messagemap[p0.key!!] = chatmessage
                refreshrecyclerviewmessage()
                recycler_latestchat.adapter = adapter
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
        Toast.makeText(this@ChatListFragment.context, "refresh",Toast.LENGTH_LONG).show()
        messagemap.values.forEach {
            adapter.add(LatestMessage(it))
        }

    }

    val adapter = GroupAdapter<ViewHolder>()
    private fun verifyuserlogin() {

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this@ChatListFragment.context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun showDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this@ChatListFragment.context!!)

        builder.setTitle("Delete User")
        builder.setMessage("Are you sure you want to delete your account, " + currentuser?.username + "?")


        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    FirebaseAuth.getInstance().currentUser?.delete()
                    deletedatafromdatabase()
                    onlineuser = null
                    Paper.book().destroy()
                    val intent = Intent(this@ChatListFragment.context, LoginActivity::class.java)
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


    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


}
