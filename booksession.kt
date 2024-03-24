package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class booksession : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booksession)


        val f = findViewById<ImageView>(R.id.back)

        f.setOnClickListener {
            val intent = Intent(this, john::class.java)
            startActivity(intent)
        }

        val fi = findViewById<ImageView>(R.id.book)

        fi.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }

        val f1 = findViewById<ImageView>(R.id.message)

        f1.setOnClickListener {
            val intent = Intent(this, privatemessage::class.java)
            startActivity(intent)
        }

        val f2 = findViewById<ImageView>(R.id.call)

        f2.setOnClickListener {
            val intent = Intent(this, voicecall::class.java)
            startActivity(intent)
        }
        val f3 = findViewById<ImageView>(R.id.videocall)

        f3.setOnClickListener {
            val intent = Intent(this, videocall::class.java)
            startActivity(intent)
        }

    }
}