package com.laraib.i210865

import com.laraib.i210865.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Find clickableView after setContentView
        val clickableView: View = findViewById(R.id.clickableView)

        // Set OnClickListener to navigate to a new page
        clickableView.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this@MainActivity, LOGIN::class.java)
            startActivity(intent)
        }

        Handler().postDelayed({
            val intent = Intent(this, LOGIN::class.java)
            startActivity(intent)
            finish()
        }, 5000) // 5000 milliseconds = 5 seconds
    }
}
