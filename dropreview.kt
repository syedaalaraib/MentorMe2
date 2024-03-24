package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class dropreview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dropreview)


        val f = findViewById<ImageView>(R.id.back)

        f.setOnClickListener {
            val intent = Intent(this, john::class.java)
            startActivity(intent)
        }

        val f5 = findViewById<TextView>(R.id.submit)

        f5.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }
    }
}