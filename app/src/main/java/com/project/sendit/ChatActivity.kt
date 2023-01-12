package com.project.sendit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList : ArrayList<Message>
    private lateinit var mDbRef : FirebaseDatabase

    var senderUid : String = FirebaseAuth.getInstance().currentUser?.uid.toString()



    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val userName = findViewById<TextView>(R.id.textViewUserName)

        //senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        mDbRef = Firebase.database("https://chat-b06e6-default-rtdb.europe-west1.firebasedatabase.app/")

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.hide()
        userName.text = name

        userName.setOnClickListener {
            val intent = Intent(this, MainScreen::class.java)
            finish()
            startActivity(intent)
        }

        chatRecyclerView = findViewById(R.id.recyclerViewChat)
        messageEditText = findViewById(R.id.editTextMessage)
        sendButton = findViewById(R.id.imageViewSendButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter


        mDbRef.getReference("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)

                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        sendButton.setOnClickListener {

            val message = messageEditText.text.toString()
            val messageObject = Message(message, senderUid)

            if (message == "!play" || message == "!game"){

                val gameIntent = Intent(this, MainActivity::class.java)
                startActivity(gameIntent)

            } else {
                if (message != "") {
                    mDbRef.getReference("chats").child(senderRoom!!).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener {
                            mDbRef.getReference("chats").child(receiverRoom!!).child("messages")
                                .push()
                                .setValue(messageObject)
                        }
                    messageEditText.setText("")
                }
            }
        }
    }
}