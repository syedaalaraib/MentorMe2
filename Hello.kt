package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import mentors_adapter

class Hello : AppCompatActivity() {

    private lateinit var toprv: RecyclerView
    private lateinit var educationrv: RecyclerView
    private lateinit var recentrv: RecyclerView
    val mentorsList = mutableListOf<Item_RV>()
    val adapter = mentors_adapter(mentorsList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        val f1 = findViewById<ImageView>(R.id.home)

        f1.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }


        val Button11 = findViewById<ImageView>(R.id.profile)

        // Set OnClickListener for the Button
        Button11.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        val Button112 = findViewById<TextView>(R.id.profile_)

        // Set OnClickListener for the Button
        Button112.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }



        val Button1 = findViewById<ImageView>(R.id.chat)

        // Set OnClickListener for the Button
        Button1.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, chats::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<ImageView>(R.id.search)

        // Set OnClickListener for the Button
        loginButton.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, letsfind::class.java)
            startActivity(intent)
        }

//        val Button = findViewById<ImageView>(R.id.john)
//
//        // Set OnClickListener for the Button
//        Button.setOnClickListener {
//            // Navigate to a new page here
//            val intent = Intent(this, john::class.java)
//            startActivity(intent)
//        }


        val B = findViewById<ImageView>(R.id.plus)

        // Set OnClickListener for the Button
        B.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, newmentor::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerViews
        toprv = findViewById(R.id.top_mentors_rv)
        toprv.setHasFixedSize(true)
        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        educationrv = findViewById(R.id.education_mentors_rv)
        educationrv.setHasFixedSize(true)
        educationrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recentrv = findViewById(R.id.recent_mentors_rv)
        recentrv.setHasFixedSize(true)
        recentrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Create list of mentors


//        for (i in 1..10) {
//            mentorsList.add(
//                Item_RV(
//                    image1 = R.drawable.op_grey,
//                    image2 = R.drawable.opaque,
//                    heart = R.drawable.grey_heart,
//                    name = "Mentor $i",
//                    description = "Description $i",
//                    price = "$10",
//                    status = "Available"
//                )
//            )
//
//        }
        // Create and set adapter
        retrieveMentorsFromRealtimeDatabase()
        toprv.adapter = adapter
        educationrv.adapter = adapter
        recentrv.adapter = adapter


    }
    private fun retrieveMentorsFromRealtimeDatabase() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("mentors")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mentorsList.clear()
                for (snapshot in dataSnapshot.children) {
                    val mentor = snapshot.getValue(Item_RV::class.java)
                    mentor?.let { mentorsList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@Hello,
                    "Error retrieving data: ${databaseError.message} ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}