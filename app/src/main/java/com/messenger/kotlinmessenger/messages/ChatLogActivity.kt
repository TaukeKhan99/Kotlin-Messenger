package com.messenger.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.messenger.kotlinmessenger.R
import com.messenger.kotlinmessenger.models.ChatMessage
import com.messenger.kotlinmessenger.models.User
import com.messenger.kotlinmessenger.views.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.messenger.kotlinmessenger.ProfileActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLogActivity"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG,"Attempt to send message...")
            performSendMessage()
        }

    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null){
                    Log.d(TAG,chatMessage.text)
                    if (chatMessage.fromId ==FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text,currentUser!!))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun performSendMessage(){
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)

        val latestMessageReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageReference.setValue(chatMessage)

        val latestMessageToReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToReference.setValue(chatMessage)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d("ProfileActivity","Item selected")
        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_chat_log,menu)
        return super.onCreateOptionsMenu(menu)
    }
}

