package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class john : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_john)


        val loginButton = findViewById<ImageView>(R.id.writereview)

        // Set OnClickListener for the Button
        loginButton.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, dropreview::class.java)
            startActivity(intent)
        }

        val Button = findViewById<ImageView>(R.id.booksession)

        // Set OnClickListener for the Button
        Button.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, booksession::class.java)
            startActivity(intent)
        }



        val f = findViewById<ImageView>(R.id.back)

        f.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }
    }
}