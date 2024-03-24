package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.PHONE_SIGN_IN_METHOD
import java.util.concurrent.TimeUnit
import java.util.logging.Handler

class verifyphone : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var verifybutton: Button
    private lateinit var inputOTP1: TextView
    private lateinit var inputOTP2: TextView
    private lateinit var inputOTP3: TextView
    private lateinit var inputOTP4: TextView
    private lateinit var inputOTP5: TextView
    private lateinit var inputOTP6: TextView
    private lateinit var resend: TextView

    private lateinit var OTP: String
    private lateinit var phoneNumber: String
    private lateinit var resendingToken: ForceResendingToken


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifyphone)

//       Retrieve the phone number passed from the previous activity
        val phoneNumberTextView = findViewById<TextView>(R.id.setphone)
        phoneNumber = intent.getStringExtra("phone").toString()!!
        OTP = intent.getStringExtra("OTP").toString()
        resendingToken = intent.getParcelableExtra("token")!!

        auth = FirebaseAuth.getInstance()
        verifybutton = findViewById(R.id.verifybutton)
        inputOTP1 = findViewById(R.id.inputOTP1)
        inputOTP2 = findViewById(R.id.inputOTP2)
        inputOTP3 = findViewById(R.id.inputOTP3)
        inputOTP4 = findViewById(R.id.inputOTP4)
        inputOTP5 = findViewById(R.id.inputOTP5)
        inputOTP6 = findViewById(R.id.inputOTP6)
        resend = findViewById(R.id.resend)

        addTextChangeListeners()
        resetOTPInputs()

        // Set the phone number to the TextView
        phoneNumberTextView.text = phoneNumber
        if (phoneNumber != null) {
            // If phone number is not null, log it
            println("Phone number received: $phoneNumber")
        }

        val signUpButton = findViewById<Button>(R.id.back)
        signUpButton.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }

        verifybutton.setOnClickListener {
            //collect otp
            val typedOTP = inputOTP1.text.toString() + inputOTP2.text.toString() + inputOTP3.text.toString() + inputOTP4.text.toString() + inputOTP5.text.toString() + inputOTP6.text.toString()
            if (typedOTP.isNotEmpty()) {
                if(typedOTP.length == 6){
                    sendtonext()
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(OTP, typedOTP)
                    signInWithPhoneAuthCredential(credential)
                    println("Correct OTP")
                }
                else{
                    Toast.makeText(this, "Please enter correct OTP", Toast.LENGTH_SHORT).show()
                    println("Incorrect OTP")
                }
            } else {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
                println("khali OTP")
            }
        }
        resend.setOnClickListener {
            resendVerificationCode()
            resetOTPInputs()
        }
    }
    private fun resetOTPInputs() {
        inputOTP1.text = null
        inputOTP2.text = null
        inputOTP3.text = null
        inputOTP4.text = null
        inputOTP5.text = null
        inputOTP6.text = null
        inputOTP1.requestFocus()
    }
    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendingToken) // ForceResendingToken from callbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(baseContext, "Request invalid", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(baseContext, "sms quota exceeded", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Toast.makeText(baseContext, "reCAPTCHA verification attempted with null Activity", Toast.LENGTH_SHORT).show()
                // reCAPTCHA verification attempted with null Activity
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            OTP=verificationId
            resendingToken=token


        }
    }
    private fun sendtonext() {
        val intent = Intent(this, Hello::class.java)
        startActivity(intent)
    }
    private fun addTextChangeListeners() {
        inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
        inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
        inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
        inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
        inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
        inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))
    }
    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Do nothing
        }

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            if (s.toString().isNotEmpty()) {
                when (view.id) {
                    R.id.inputOTP1 -> if (text.length == 1) inputOTP2.requestFocus()
                    R.id.inputOTP2 -> if (text.length == 1) inputOTP3.requestFocus() else if (text.isEmpty()) inputOTP1.requestFocus()
                    R.id.inputOTP3 -> if (text.length == 1) inputOTP4.requestFocus() else if (text.isEmpty()) inputOTP2.requestFocus()
                    R.id.inputOTP4 -> if (text.length == 1) inputOTP5.requestFocus() else if (text.isEmpty()) inputOTP3.requestFocus()
                    R.id.inputOTP5 -> if (text.length == 1) inputOTP6.requestFocus() else if (text.isEmpty()) inputOTP4.requestFocus()
                    R.id.inputOTP6 -> if (text.isEmpty()) inputOTP5.requestFocus()


                }
            }
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "authenticate successful", Toast.LENGTH_SHORT).show()
                    sendtonext()
                } else {
                    // Sign in failed, display a message and update the UI

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

}