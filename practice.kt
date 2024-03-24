package com.laraib.i210865

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView


class practice : AppCompatActivity() {

    private lateinit var imageView: CircleImageView
    private lateinit var username: TextView

    private lateinit var fuser: FirebaseUser

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var btn_send: ImageButton
    private lateinit var text_send: TextView

    private lateinit var userIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)

        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        auth = FirebaseAuth.getInstance()
        val fbuser = auth.currentUser

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        username = findViewById(R.id.name)
        imageView = findViewById(R.id.image)
        btn_send = findViewById(R.id.btn_send)
        text_send = findViewById(R.id.text_send)


        showProfile(fbuser)

        btn_send.setOnClickListener {
            val msg = text_send.text.toString()
            if (msg.isNotEmpty()) {
                userIntent.getStringExtra("userid")?.let { it1 -> sendmessage(fuser.uid, it1, msg) }
            } else {
                Toast.makeText(this@practice, "You can't send empty message", Toast.LENGTH_SHORT).show()
            }
            text_send.text = ""
        }
    }

    private fun showProfile(fbUser: FirebaseUser?) {
        val uid = fbUser?.uid
        if (uid != null) {
            val refProfile = FirebaseDatabase.getInstance().getReference("users")
            refProfile.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User_::class.java)

                    if (user != null) {
                        username.text = user.name
                        if (!user.photoLink.isNullOrEmpty()) {
                            Glide.with(this@practice)
                                .load(user.photoLink)
                                .placeholder(R.drawable.customer)
                                .error(R.drawable.baseline_circle_24)
                                .into(imageView)
                        }
                    } else {
                        Toast.makeText(this@practice, "User data is null", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@practice, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@practice, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendmessage(sender: String, receiver: String, message: String) {
        val reference = FirebaseDatabase.getInstance().getReference()
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        reference.child("Chats").push().setValue(hashMap)
    }
}
