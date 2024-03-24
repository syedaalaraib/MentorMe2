package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.OnTrimMemoryProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class signup : AppCompatActivity() {
    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var phone: EditText
    private lateinit var countrySpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationcode: String
    private lateinit var Force: PhoneAuthProvider.ForceResendingToken
    private lateinit var number: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        signUpButton = findViewById(R.id.signup)
        loginButton = findViewById(R.id.login)
        name = findViewById(R.id.entername)
        email = findViewById(R.id.enteremail)
        password = findViewById(R.id.password)
        phone = findViewById(R.id.entercontactnumber)
        countrySpinner = findViewById(R.id.countrySpinner)
        citySpinner = findViewById(R.id.citySpinner)

        auth = FirebaseAuth.getInstance()
        verificationcode = ""
//        Force = PhoneAuthProvider.ForceResendingToken()


        signUpButton.setOnClickListener {
            // Navigate to a new page here
            val txt_name = name.text.toString()
            val txt_email = email.text.toString()
            val txt_password = password.text.toString()
            val txt_phone = phone.text.toString()
            number= "+92$txt_phone"
            val txt_country = countrySpinner.selectedItem.toString()
            val txt_city = citySpinner.selectedItem.toString()

            // Check if any of the fields is empty
            if (txt_name.isEmpty() || txt_email.isEmpty() || txt_password.isEmpty() || txt_phone.isEmpty() || txt_country.isEmpty() || txt_city.isEmpty())
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            else {
                ///////////
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(number) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)


                /////////////////old
                registeruser(txt_name, txt_email, txt_password, txt_phone, txt_country, txt_city)

//
            }
        }

        // Set underline for the text
        val content = SpannableString("Login")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        loginButton.text = content

        // Find the Spinners by Id

        // Define lists of countries and cities
        val countries = listOf("Select Country", "Pakistan", "Switzerland", "Saudi Arabia")
        val citiesMap = mapOf(
            "Select Country" to listOf("Select City"),
            "Pakistan" to listOf("Select City", "Islamabad", "Karachi", "Lahore"),
            "Switzerland" to listOf("Select City", "Zurich", "Geneva", "Bern"),
            "Saudi Arabia" to listOf("Select City", "Riyadh", "Jeddah", "Dammam")
        )

        // Create an ArrayAdapter for countries
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = countryAdapter

        // Create an ArrayAdapter for cities (initially with a default "Select City" item)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Select City"))
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter

        // Set the OnItemSelectedListener for the countrySpinner
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Get the selected country
                val selectedCountry = parentView.getItemAtPosition(position).toString()

                // Update the list of cities based on the selected country
                val selectedCities = citiesMap[selectedCountry] ?: listOf("Select City")
                updateCitySpinner(selectedCities)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing here
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

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "authenticate not successful", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun sendtonext() {
        val intent = Intent(this, verifyphone::class.java)
        startActivity(intent)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {

            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(baseContext, "Request invalid", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(baseContext, "sms quota exceeded", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Toast.makeText(baseContext, "reCAPTCHA verification attempted with null Activity", Toast.LENGTH_SHORT).show()
                // reCAPTCHA verification attempted with null Activity
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {

            val intent = Intent(this@signup, verifyphone::class.java).apply {
                putExtra("phone", number)
                putExtra("verificationId", verificationId)
                putExtra("token", token)
            }
            startActivity(intent)


        }
    }

//    private fun OTP(phone: String, isSend: Boolean) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phone)               // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS)  // Timeout and unit
//            .setActivity(this)                   // Activity (for callback binding)
//            .setCallbacks(getCallbacks(phone))  // OnVerificationStateChangedCallbacks
//            .build()
//
//        if (isSend) {
//            val forceResendingToken = Force
//            PhoneAuthProvider.verifyPhoneNumber(options)
//            Toast.makeText(baseContext, "OTP sent successfully", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(baseContext, "OTP failed", Toast.LENGTH_SHORT).show()
//            PhoneAuthProvider.verifyPhoneNumber(options)
//        }
//    }



//    override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }



//    private fun getCallbacks(txtPhone: String): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
//        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                val intent = Intent(this@signup, verifyphone::class.java).apply {
//                    putExtra("phone", txtPhone)
//                }
//                startActivity(intent)
//            }
//
//            override fun onVerificationFailed(e: FirebaseException) {
//                Toast.makeText(baseContext, "OTP  Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onCodeSent(
//                verificationId: String,
//                token: PhoneAuthProvider.ForceResendingToken
//            ) {
//                super.onCodeSent(verificationId, token)
//                verificationcode = verificationId
//                Force = token
//
//                Toast.makeText(baseContext, "OTP sent successfully", Toast.LENGTH_SHORT).show()
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//            }
//        }
//    }

    private fun registeruser(txtName: String, txtEmail: String, txtPassword: String, txtPhone: String, txtCountry: String, txtCity: String) {
        // Implement your user registration logic here
        auth.createUserWithEmailAndPassword(txtEmail, txtPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user!!.uid // Get the user ID

                    // Create a reference to the Firebase Realtime Database
                    val database = FirebaseDatabase.getInstance().reference
                    val userData = hashMapOf(
                        "name" to txtName,
                        "email" to txtEmail,
                        "phone" to txtPhone,
                        "country" to txtCountry,
                        "city" to txtCity,
                        "imageLink" to "@mipmap/black", // Add imageLink field with initial value
                        "favoriteMentors" to listOf<String>() // Add favoriteMentors field with empty list
                    )

                    // Save user data to the database under the email instead of user ID
//                    val userEmailKey = txtEmail.replace(".", ",") // Replace '.' with ',' for email to be used as a key
                    database.child("users").child(userId).setValue(userData)
                        .addOnSuccessListener {
                            // Data saved successfully
                            Toast.makeText(baseContext, "User registered and data saved.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Failed to save data
                            Toast.makeText(baseContext, "Failed to register user: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    // Send email verification
                    user.sendEmailVerification().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "Verification email sent.", Toast.LENGTH_SHORT).show()

                        }
                        else {
                            Toast.makeText(baseContext, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Function to update the city spinner with a new list of cities
    private fun updateCitySpinner(cities: List<String>) {
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter
    }
}




//    private fun registeruser(txtName: String, txtEmail: String, txtPassword: String, txtPhone: String, txtCountry: String, txtCity: String) {
//        // Implement your user registration logic here
//        auth.createUserWithEmailAndPassword(txtEmail, txtPassword)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    user!!.sendEmailVerification()
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                Toast.makeText(baseContext, "Verification email sent.", Toast.LENGTH_SHORT).show()
//                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                                    txtPhone, // Phone number to verify
//                                    60, // Timeout duration
//                                    java.util.concurrent.TimeUnit.SECONDS, // Unit of timeout
//                                    this, // Activity (for callback binding)
//                                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                                            // Dummy implementation
//                                        }
//
//                                        override fun onVerificationFailed(e: FirebaseException) {
//                                            // This callback is invoked in an invalid request for verification is made,
//                                            // for instance if the the phone number format is not valid.
//                                        }
//
//                                        override fun onCodeSent(
//                                            verificationId: String,
//                                            token: PhoneAuthProvider.ForceResendingToken
//                                        ) {
//                                            // The SMS verification code has been sent to the provided phone number, we
//                                            // now need to ask the user to enter the code and then construct a credential
//                                            // by combining the code with a verification ID.
//                                            Toast.makeText(baseContext, "Code sent", Toast.LENGTH_SHORT).show()
//                                            val intent = Intent(this@signup, verifyphone::class.java).apply {
//                                                putExtra("phone", txtPhone)
//                                            }
//                                            startActivity(intent)
//                                        }
//                                    }
//                                )
//
//                            }
//                        }
//                } else {
//                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
