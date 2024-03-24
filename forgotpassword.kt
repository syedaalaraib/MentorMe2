package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button

class forgotpassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)


        val f = findViewById<Button>(R.id.send)

        // Set OnClickListener for the Button
        f.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, resetpassword::class.java)
            startActivity(intent)
        }

        val forgotButton = findViewById<Button>(R.id.back)

        // Set OnClickListener for the Button
        forgotButton.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, LOGIN::class.java)
            startActivity(intent)
        }


        val Button = findViewById<Button>(R.id.login)

        // Set OnClickListener for the Button
        Button.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, LOGIN::class.java)
            startActivity(intent)
        }

        val content = SpannableString("Login")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        Button.text = content
    }
}