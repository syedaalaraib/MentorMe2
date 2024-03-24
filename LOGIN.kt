package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LOGIN : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.enteremail)
        password = findViewById(R.id.enterpassword)
        loginButton = findViewById(R.id.login)
        auth = FirebaseAuth.getInstance()


        loginButton.setOnClickListener {
            val txt_email = email.text.toString()
            val txt_password = password.text.toString()
            loginuser(txt_email, txt_password)

//            val intent = Intent(this, Hello::class.java)
//            startActivity(intent)
        }

        val forgotButton = findViewById<Button>(R.id.forgotpassword)
        forgotButton.setOnClickListener {
            val intent = Intent(this, forgotpassword::class.java)
            startActivity(intent)
        }


        val signUpButton = findViewById<Button>(R.id.signup)
        signUpButton.setOnClickListener {
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }

        val content = SpannableString("Sign up?")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        signUpButton.text = content


    }

    private fun loginuser(txtEmail: String, txtPassword: String) {
        auth.signInWithEmailAndPassword(txtEmail, txtPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    val intent = Intent(this, Hello::class.java)
                    intent.extras?.putString("userid", user?.uid)
                    startActivity(intent)

//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }

    }
}