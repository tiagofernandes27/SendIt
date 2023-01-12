package com.project.sendit

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    private lateinit var mDbRef: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        supportActionBar?.hide()

        val buttonLoginRegister = findViewById<Button>(R.id.buttonLoginRegister)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val editTextName=findViewById<EditText>(R.id.editTextName)
        val editTextEmailRegister = findViewById<EditText>(R.id.editTextEmailRegister)
        val editTextPasswordRegister = findViewById<EditText>(R.id.editTextPasswordRegister)
        val editTextPasswordRegisterConfirm = findViewById<EditText>(R.id.editTextPasswordRegisterConfirm)

        buttonLoginRegister.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            finish()
            startActivity(intent)
        }

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmailRegister.text.toString()
            val password = editTextPasswordRegister.text.toString()
            val passwordConf = editTextPasswordRegisterConfirm.text.toString()

            if(password!=passwordConf){
                Toast.makeText(
                    baseContext, "Password is incorrect.",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Register success, update UI with the signed-in user's information
                            val user = FirebaseAuth.getInstance().currentUser
                            addUserToDatabase(name, email, user?.uid!!)
                            val intent = Intent(this, MainScreen::class.java)
                            finish()
                            startActivity(intent)
                        } else {
                            // If register fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "There was a problem. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun addUserToDatabase(name: String, email:String, uid:String){
        mDbRef = Firebase.database("https://chat-b06e6-default-rtdb.europe-west1.firebasedatabase.app/")
        mDbRef.getReference("users").child(uid).setValue(User(name, email, uid))
    }
}
