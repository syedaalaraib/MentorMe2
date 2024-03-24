
package com.laraib.i210865

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*

import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

data class User(
    val name: String,
    val city: String,
    val country: String,
    val email: String,
    var phone: String,
    var photoLink: String
)

class editprofile : AppCompatActivity() {

    lateinit var chooseimg: ImageView
    var fileuri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show()
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        chooseimg = findViewById(R.id.profilepic)
        chooseimg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
        }

        // Find the Spinners by ID
        val countrySpinner = findViewById<Spinner>(R.id.countrySpinner)
        val citySpinner = findViewById<Spinner>(R.id.citySpinner)

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

        // Set the OnItemSelectedListener for the citySpinner
        auth = FirebaseAuth.getInstance()
        val fbuser = auth.currentUser
        showProfile(fbuser)

        val f5 = findViewById<TextView>(R.id.update)
        f5.setOnClickListener {

            if (fileuri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser

            val nameText = findViewById<TextView>(R.id.name).text.toString()
            val emailText = findViewById<TextView>(R.id.email).text.toString()
            val phoneText = findViewById<TextView>(R.id.number).text.toString()
            val countryText = countrySpinner.selectedItem.toString()
            val cityText = citySpinner.selectedItem.toString()

            val editmap = mapOf(
                "name" to nameText,
                "email" to emailText,
                "phone" to phoneText,
                "country" to countryText,
                "city" to cityText
            )

            updateProfile(fbuser, editmap)

            val user = User(nameText, cityText, countryText, emailText, phoneText, fileuri.toString())
            uploadImage(user)
        }
    }

    private fun updateProfile(fbUser: FirebaseUser?, editMap: Map<String, String>) {
        val nameText = findViewById<TextView>(R.id.name).text.toString()
        val currentUser = FirebaseAuth.getInstance().currentUser

        val writeUserDetails = User(
            editMap["name"] ?: "",
            editMap["city"] ?: "",
            editMap["country"] ?: "",
            editMap["email"] ?: "",
            editMap["phone"] ?: "",
            editMap["photoLink"] ?: ""
        )

        val uid = fbUser?.uid
        if (uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("users").child(uid)
            ref.setValue(writeUserDetails).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nameText)
                        .build()
                    currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            Toast.makeText(this, "Your data is updated successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Hello::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Failed to update user profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProfile(fbUser: FirebaseUser?) {
        val uid = fbUser?.uid
        if (uid != null) {
            val refProfile = FirebaseDatabase.getInstance().getReference("users")
            refProfile.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)
                    val phone = snapshot.child("phone").getValue(String::class.java)
                    val country = snapshot.child("country").getValue(String::class.java)
                    val city = snapshot.child("city").getValue(String::class.java)
                    val pic = snapshot.child("photoLink").getValue(String::class.java)

                    // Get references to UI elements
                    val nameText = findViewById<TextView>(R.id.name)
                    val emailText = findViewById<TextView>(R.id.email)
                    val phoneText = findViewById<TextView>(R.id.number)
                    val countrySpinner = findViewById<Spinner>(R.id.countrySpinner)
                    val citySpinner = findViewById<Spinner>(R.id.citySpinner)
                    val profilepic = findViewById<ImageView>(R.id.profilepic)

                    // Update UI with retrieved data
                    nameText.text = name
                    emailText.text = email
                    phoneText.text = phone

                    // Load image using Glide if URI is not empty
                    if (!pic.isNullOrEmpty()) {
                        Glide.with(this@editprofile)
                            .load(pic)
                            .placeholder(R.drawable.customer) // Optional placeholder image
                            .error(R.drawable.baseline_circle_24) // Optional error image
                            .into(profilepic)
                    }

                    // Find the index of country and city in their respective lists
                    val countryIndex = (countrySpinner.adapter as? ArrayAdapter<String>)?.getPosition(country)
                    val cityIndex = (citySpinner.adapter as? ArrayAdapter<String>)?.getPosition(city)

                    // Set the selection for spinners if indices are valid
                    countryIndex?.takeIf { it != -1 }?.let { countrySpinner.setSelection(it) }
                    cityIndex?.takeIf { it != -1 }?.let { citySpinner.setSelection(it) }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@editprofile, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle null user ID
            Toast.makeText(this@editprofile, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }



    // Function to update the city spinner with a new list of cities
    private fun updateCitySpinner(cities: List<String>) {
        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
            fileuri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileuri)
                chooseimg.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(user: User) {
        val ref = FirebaseStorage.getInstance().getReference().child("mentorImages/" + UUID.randomUUID().toString())
        ref.putFile(fileuri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL from the task snapshot
                ref.downloadUrl.addOnSuccessListener { uri ->
                    user.photoLink = uri.toString()
                    saveUserData(user)
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to retrieve download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserData(user: User) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            database.child("users").child(userId).setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Your data is updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Hello::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}


















//package com.laraib.i210865
//
//import android.content.Intent
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.provider.MediaStore
//import android.text.SpannableString
//import android.text.style.UnderlineSpan
//import android.view.View
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.Spinner
//import android.widget.TextView
//import android.widget.Toast
//import com.google.firebase.Firebase
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.UserProfileChangeRequest
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.database
//import com.google.firebase.storage.FirebaseStorage
//import java.util.UUID
//
//data class User(
//    val name: String,
//    val city: String,
//    val country: String,
//    val email: String,
//    var phone: String,
//    var photoLink: String
//)
//
//class editprofile : AppCompatActivity() {
//
//    lateinit var chooseimg: ImageView
//    var fileuri: Uri? = null
//    private lateinit var database: DatabaseReference
//    private lateinit var auth: FirebaseAuth
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_editprofile)
//
//
//        FirebaseApp.initializeApp(this)
//        database = Firebase.database.reference
//        auth = FirebaseAuth.getInstance()
//        chooseimg = findViewById(R.id.profilepic)
//        chooseimg.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
//        }
//
//
//        // Find the Spinners by ID
//        val countrySpinner = findViewById<Spinner>(R.id.countrySpinner)
//        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
//
//        // Define lists of countries and cities
//        val countries = listOf("Select Country", "Pakistan", "Switzerland", "Saudi Arabia")
//        val citiesMap = mapOf(
//            "Select Country" to listOf("Select City"),
//            "Pakistan" to listOf("Select City", "Islamabad", "Karachi", "Lahore"),
//            "Switzerland" to listOf("Select City", "Zurich", "Geneva", "Bern"),
//            "Saudi Arabia" to listOf("Select City", "Riyadh", "Jeddah", "Dammam")
//        )
//
//        // Create an ArrayAdapter for countries
//        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
//        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        countrySpinner.adapter = countryAdapter
//
//        // Create an ArrayAdapter for cities (initially with a default "Select City" item)
//        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Select City"))
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        citySpinner.adapter = cityAdapter
//
//        // Set the OnItemSelectedListener for the countrySpinner
//        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>,
//                selectedItemView: View?,
//                position: Int,
//                id: Long
//            ) {
//                // Get the selected country
//                val selectedCountry = parentView.getItemAtPosition(position).toString()
//
//                // Update the list of cities based on the selected country
//                val selectedCities = citiesMap[selectedCountry] ?: listOf("Select City")
//                updateCitySpinner(selectedCities)
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>) {
//                // Do nothing here
//            }
//        }
//
//        // Set the OnItemSelectedListener for the citySpinner
//        auth= FirebaseAuth.getInstance()
//        val fbuser = auth.currentUser
//        showProfile(fbuser)
//
//        val f5 = findViewById<TextView>(R.id.update)
//        f5.setOnClickListener {
//
//            if (fileuri == null) {
//                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val nameText = findViewById<TextView>(R.id.name).text.toString()
//            val emailText = findViewById<TextView>(R.id.email).text.toString()
//            val phoneText = findViewById<TextView>(R.id.number).text.toString()
//            val countryText = countrySpinner.selectedItem.toString()
//            val cityText = citySpinner.selectedItem.toString()
//            val currentUser = FirebaseAuth.getInstance().currentUser
//
//            val editmap = mapOf(
//                "name" to nameText,
//                "email" to emailText,
//                "phone" to phoneText,
//                "country" to countryText,
//                "city" to cityText
//            )
//
//            updateProfile(fbuser)
//
//
////            val profileUpdates = UserProfileChangeRequest.Builder()
////                .setDisplayName(nameText)
////                // Add other profile updates here if needed
////                .build()
////
////            currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
////                if (task.isSuccessful) {
////                    // Profile updated successfully
////                    // Now update user data in the Firebase Realtime Database
////                    database.child("users").child(currentUser.uid).updateChildren(editmap)
////                        .addOnSuccessListener {
////                            Toast.makeText(this, "Your data is updated successfully", Toast.LENGTH_SHORT).show()
////                            val intent = Intent(this, Hello::class.java)
////                            startActivity(intent)
////                        }
////                        .addOnFailureListener {
////                            Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
////                        }
////                } else {
////                    // Failed to update profile
////                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
////                }
////            }
//
////            database.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(editmap)
////                .addOnSuccessListener {
////                    Toast.makeText(this, "Your data is updated successfully", Toast.LENGTH_SHORT).show()
////                    val intent = Intent(this, Hello::class.java)
////                    startActivity(intent)
////                }
////                .addOnFailureListener {
////                    Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
////                }
//
//            val user = User(nameText,cityText,countryText, emailText, phoneText,  fileuri.toString())
////            val user = User(nameText,cityText,countryText, emailText, phoneText)
//
//            uploadImage(user)
//
////            val intent = Intent(this, Hello::class.java)
////            startActivity(intent)
//        }
//
//
//
//
//    }
//
//    private fun updateProfile(fbUser: FirebaseUser?) {
//        val nameText = findViewById<TextView>(R.id.name).text.toString()
//        val emailText = findViewById<TextView>(R.id.email).text.toString()
//        val phoneText = findViewById<TextView>(R.id.number).text.toString()
//        val countryText = (findViewById<Spinner>(R.id.countrySpinner)).selectedItem.toString()
//        val cityText = (findViewById<Spinner>(R.id.citySpinner)).selectedItem.toString()
//        val currentUser = FirebaseAuth.getInstance().currentUser
//
//        val writeUserDetails = User(nameText, cityText, countryText, emailText, phoneText, fileuri.toString())
//        val uid = fbUser?.uid
//        if (uid != null) {
//            val ref = FirebaseDatabase.getInstance().getReference("users").child(uid)
//            ref.setValue(writeUserDetails).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val profileUpdates = UserProfileChangeRequest.Builder()
//                        .setDisplayName(nameText)
//                        .build()
//                    currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
//                        if (profileTask.isSuccessful) {
//                            Toast.makeText(this, "Your data is updated successfully", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(this, Hello::class.java)
//                            startActivity(intent)
//                        } else {
//                            Toast.makeText(this, "Failed to update user profile", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } else {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//
//    private fun showProfile(fbUser: FirebaseUser?) {
//        val uid = fbUser?.uid
//        if (uid != null) {
//            val refProfile = FirebaseDatabase.getInstance().getReference("users")
//            refProfile.child(uid).addListenerForSingleValueEvent(object: ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val name = snapshot.child("name").getValue(String::class.java)
//                    val email = snapshot.child("email").getValue(String::class.java)
//                    val phone = snapshot.child("phone").getValue(String::class.java)
//                    val country = snapshot.child("country").getValue(String::class.java)
//                    val city = snapshot.child("city").getValue(String::class.java)
//                    val pic = snapshot.child("photoLink").getValue(String::class.java)
//
//                    val nameText = findViewById<TextView>(R.id.name)
//                    val emailText = findViewById<TextView>(R.id.email)
//                    val phoneText = findViewById<TextView>(R.id.number)
//                    val countrySpinner = findViewById<Spinner>(R.id.countrySpinner)
//                    val citySpinner = findViewById<Spinner>(R.id.citySpinner)
//                    val profilepic = findViewById<ImageView>(R.id.profilepic)
//                    nameText.text = name
//                    emailText.text = email
//                    phoneText.text = phone
//                    profilepic.setImageURI(Uri.parse(pic));
//
//                    // Find the index of country and city in their respective lists
//                    val countryIndex = (countrySpinner.adapter as ArrayAdapter<String>).getPosition(country)
//                    val cityIndex = (citySpinner.adapter as ArrayAdapter<String>).getPosition(city)
//
//                    // Set the selection for spinners
//                    countrySpinner.setSelection(countryIndex)
//                    citySpinner.setSelection(cityIndex)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@editprofile, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } else {
//            // Handle the case when the UID is null
//        }
//    }
//
//    // Function to update the city spinner with a new list of cities
//    private fun updateCitySpinner(cities: List<String>) {
//        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
//        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        citySpinner.adapter = cityAdapter
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
//            fileuri = data.data
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileuri)
//                chooseimg.setImageBitmap(bitmap)
//            } catch (e: Exception) {
//                Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show()
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun uploadImage(user: User) {
//        val ref = FirebaseStorage.getInstance().getReference().child("mentorImages/" + UUID.randomUUID().toString())
//        ref.putFile(fileuri!!)
//            .addOnSuccessListener { taskSnapshot ->
//                // Get the download URL from the task snapshot
//                ref.downloadUrl.addOnSuccessListener { uri ->
//                    //user.photoLink = uri.toString()
//                    //saveMentorData(user)
//                }
//                    .addOnFailureListener { exception ->
//                        Toast.makeText(this, "Failed to retrieve download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
//                    }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//
//
////    private fun saveMentorData(user: User) {
////        FirebaseApp.initializeApp(this)
////        val currentUser = FirebaseAuth.getInstance().currentUser
////
////        // Check if user is logged in
////        if (currentUser != null) {
////            // User is logged in
////            val userId = currentUser.uid
////
////            // Update the user's data in the database
////            val database = FirebaseDatabase.getInstance()
////            val mentorsRef = database.getReference("mentors")
////            mentorsRef.child(userId).setValue(user)
////                .addOnSuccessListener {
////                    Toast.makeText(this, "Your data is updated successfully", Toast.LENGTH_SHORT).show()
////                    // Navigate back to the previous activity
////                    val intent = Intent(this, Hello::class.java)
////                    startActivity(intent)
////                }
////                .addOnFailureListener {
////                    Toast.makeText(this, "Failed to update mentor data", Toast.LENGTH_SHORT).show()
////                }
////        } else {
////            // User is not logged in
////            // Handle this scenario accordingly
////        }
////    }
//
//
////    private fun saveMentorData(mentor: Mentor) {
////        FirebaseApp.initializeApp(this)
////        val currentUser = FirebaseAuth.getInstance().currentUser
////
////        // Check if user is logged in
////        if (currentUser != null) {
////            // User is logged in
////            val username = currentUser.displayName
////            currentUser.city
////
////            // Use the username as needed
////        } else {
////            // User is not logged in
////            // Handle this scenario accordingly
////        }
////
//////        val database = FirebaseDatabase.getInstance()
//////        val mentorsRef = database.getReference("mentors")
//////        mentorsRef.push().setValue(mentor)
//////            .addOnSuccessListener {
//////                Toast.makeText(this, "Mentor data saved successfully", Toast.LENGTH_SHORT).show()
//////                // Navigate back to the previous activity
//////                val intent = Intent(this, Hello::class.java)
//////                startActivity(intent)
//////            }
//////            .addOnFailureListener {
//////                Toast.makeText(this, "Failed to save mentor data", Toast.LENGTH_SHORT).show()
//////            }
////    }
//}
