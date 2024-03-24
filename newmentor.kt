package com.laraib.i210865
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.*

// Define a class to represent a mentor
data class Mentor(
    val name: String,
    val description: String,
    val status: String,
    val chargesPerSession: Double,
    var photoLink: String
)

class newmentor : AppCompatActivity() {

    lateinit var chooseimg: ImageView
    var fileuri: Uri? = null
//    private var storageRef = FirebaseStorage.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newmentor)

        chooseimg = findViewById(R.id.camera)
        chooseimg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
        }

        val countrySpinner: Spinner = findViewById(R.id.statusSpinner)
        val countries = listOf("Select Status", "Available", "Busy", "On leave")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter

        val uploadButton = findViewById<TextView>(R.id.upload)
        uploadButton.setOnClickListener {

            // Check if an image is selected
            if (fileuri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get mentor data from input fields
            val nameText = findViewById<EditText>(R.id.entername).text.toString()
            val descriptionText = findViewById<EditText>(R.id.enterdescription).text.toString()
            val chargesText = findViewById<EditText>(R.id.entercash).text.toString().toDoubleOrNull() ?: 0.0
            val statusText = countrySpinner.selectedItem.toString()


            // Create mentor object
            val mentor = Mentor(nameText, descriptionText, statusText, chargesText, fileuri.toString())


            // Save image to Firebase Storage
            uploadImage(mentor)
        }
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

    private fun uploadImage(mentor: Mentor) {
        val ref = FirebaseStorage.getInstance().getReference().child("mentorImages/" + UUID.randomUUID().toString())
        ref.putFile(fileuri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL from the task snapshot
                ref.downloadUrl.addOnSuccessListener { uri ->
                    mentor.photoLink = uri.toString()
                    saveMentorData(mentor)
                }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to retrieve download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun saveMentorData(mentor: Mentor) {
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance()
        val mentorsRef = database.getReference("mentors")
        mentorsRef.push().setValue(mentor)
            .addOnSuccessListener {
                Toast.makeText(this, "Mentor data saved successfully", Toast.LENGTH_SHORT).show()
                // Navigate back to the previous activity
                val intent = Intent(this, Hello::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save mentor data", Toast.LENGTH_SHORT).show()
            }
    }
}