package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class communitymessage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communitymessage)

        val f = findViewById<ImageView>(R.id.back)

        f.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }

        val f1 = findViewById<ImageView>(R.id.home)

        f1.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }

        val f2 = findViewById<ImageView>(R.id.search)

        f2.setOnClickListener {
            val intent = Intent(this, letsfind::class.java)
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
    }
}