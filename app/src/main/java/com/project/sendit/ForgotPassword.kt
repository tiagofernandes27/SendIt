package com.project.sendit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ForgotPassword : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    val CHANNEL_ID = "channel01"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        supportActionBar?.hide()

        val email = findViewById<EditText>(R.id.editTextEmailAddressRecoverPassword)
        val resetPasswordBtn = findViewById<Button>(R.id.buttonReset)

        resetPasswordBtn.setOnClickListener()
        {
            sendPasswordReset(email.text.toString())
            //showNotification()
        }
        firebaseAuth = Firebase.auth
    }

    private fun sendPasswordReset(email: String)
    {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(baseContext, "Reset password failed. Please try again", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showNotification(){
        createNotificationChannel()

        val data = Date()
        val notificationId = SimpleDateFormat("ddHHmmss", Locale.FRENCH).format(data).toInt()

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)

        notificationBuilder.setContentTitle("SendIt")
        notificationBuilder.setContentText("Reset link sent to your email.")
        notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        notificationBuilder.setAutoCancel(true)

        notificationBuilder.setContentIntent(pendingIntent)

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name: String = "MyNotification"
            val description = "My notification channel description"

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationChannel.description = description
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}

