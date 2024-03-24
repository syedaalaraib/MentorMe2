package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import user_adapter

class chats : AppCompatActivity() {

    private lateinit var toprv: RecyclerView
    val userList = mutableListOf<User_rv>()
    val adapter = user_adapter(userList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)


        val f = findViewById<ImageView>(R.id.back)

        f.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }

        val fi = findViewById<ImageView>(R.id.search)

        fi.setOnClickListener {
            val intent = Intent(this, letsfind::class.java)
            startActivity(intent)
        }


//        val fip = findViewById<TextView>(R.id.johncooper)
//
//        fip.setOnClickListener {
//            val intent = Intent(this, privatemessage::class.java)
//            startActivity(intent)
//        }

        val fip1 = findViewById<TextView>(R.id.community)

        fip1.setOnClickListener {
            val intent = Intent(this, communitymessage::class.java)
            startActivity(intent)
        }


        val f1 = findViewById<ImageView>(R.id.home)

        f1.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }

        val f3 = findViewById<ImageView>(R.id.chat)

        f3.setOnClickListener {
            val intent = Intent(this, chats::class.java)
            startActivity(intent)
        }

        val f4 = findViewById<ImageView>(R.id.profile)

        f4.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }
        val f5 = findViewById<ImageView>(R.id.plus)

        f5.setOnClickListener {
            val intent = Intent(this, newmentor::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerViews
        toprv = findViewById(R.id.top_mentors_rv)
        toprv.setHasFixedSize(true)
        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Create and set adapter
        retrieveMentorsFromRealtimeDatabase()
        toprv.adapter = adapter
    }
    private fun retrieveMentorsFromRealtimeDatabase() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    // Log the snapshot key to check if it contains the user ID
                    Log.d("SnapshotKey", "Snapshot key: ${snapshot.key}")

                    val mentor = snapshot.getValue(User_rv::class.java)

                    // Get the user ID from the snapshot key
                    val userId = snapshot.key

                    // Set the user ID to the mentor object if not null
                    mentor?.let {
                        it.uid = userId
                        userList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@chats,
                    "Error retrieving data: ${databaseError.message} ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


}