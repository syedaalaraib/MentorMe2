package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class photo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)


        val loginButton = findViewById<ImageView>(R.id.video)

        // Set OnClickListener for the Button
        loginButton.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, video::class.java)
            startActivity(intent)
        }
    }
}