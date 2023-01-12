package com.project.sendit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddFriend : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_friend)

        supportActionBar?.hide()

        val buttonAddFriend = findViewById<Button>(R.id.buttonAddFriend)
        val editTextFriendId = findViewById<EditText>(R.id.editTextFriendId)
        val goBackButton = findViewById<Button>(R.id.buttonGoBack)

        goBackButton.setOnClickListener{
            val intent = Intent(this, MainScreen::class.java)
            finish()
            startActivity(intent)
        }

        buttonAddFriend.setOnClickListener{

            Toast.makeText(baseContext, "User not found", Toast.LENGTH_SHORT).show()
        }
    }
}