package com.laraib.i210865

import android.annotation.SuppressLint
import android.app.Activity.ScreenCaptureCallback
import android.content.Context
import android.Manifest
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import de.hdodenhof.circleimageview.CircleImageView
import messageadapter
import java.util.Date



data class User_(
    val name: String = "", // Default values for properties
    var photoLink: String = ""
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "")
}

@SuppressLint("NewApi")
class privatemessage : AppCompatActivity(){

    private val screenCaptureCallback = ScreenCaptureCallback {
        Toast.makeText(this, "screenshot detected", Toast.LENGTH_SHORT).show()
    }

    private lateinit var btnsend: Button

    private lateinit var imageView: CircleImageView
    private lateinit var username: TextView
    private lateinit var btn_send: ImageButton
    private lateinit var text_send: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var fuser: FirebaseUser
    private lateinit var database: DatabaseReference

    private lateinit var userIntent: Intent
    private lateinit var madapter: messageadapter
    private var mchat = mutableListOf<ChatRV>()
    private var imageurl: String = ""

//    private lateinit var mediaRecorder: MediaRecorder
//    var tempMediaOutput:String = ""
//    var mediaState:Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privatemessage)

/////////
//        btnsend = findViewById(R.id.btnsend)
//        tempMediaOutput =
//            Environment.getExternalStorageDirectory().absolutePath + "/tempRecording-"+ Date().time+".mp3"
//        mediaRecorder = MediaRecorder()
//        mediaRecorder.setOutputFile(tempMediaOutput)



        val Button11 = findViewById<ImageView>(R.id.video)
        Button11.setOnClickListener {
            val intent = Intent(this, videocall::class.java)
            startActivity(intent) }

        val Button112 = findViewById<ImageView>(R.id.call)
        Button112.setOnClickListener {
            val intent = Intent(this, voicecall::class.java)
            startActivity(intent) }

        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference
        fuser = FirebaseAuth.getInstance().currentUser!!

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        username = findViewById(R.id.name)
        imageView = findViewById(R.id.image)
        btn_send = findViewById(R.id.btn_send)
        text_send = findViewById(R.id.text_send)

//        text_send.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // Do nothing
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
//            {
//                if (s.toString().trim().isNotEmpty()) {
//                    btn_send.setImageResource(R.drawable.ic_mic)
//                    btnsend.tag = R.drawable.ic_mic
//                } else {
//                    btn_send.setImageResource(R.drawable.ic_mic)
//                    btnsend.tag = R.drawable.ic_mic
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (text_send.text.isEmpty()){
//                    btn_send.setImageResource(R.drawable.ic_mic)
//                    btn_send.tag = R.drawable.ic_mic
//                }else{
//                    btn_send.setImageResource(R.drawable.ic_stop)
//                    btn_send.tag = R.drawable.ic_stop
//                }
//                // Do nothing
//
//            }
//        })

        userIntent = intent
        val receiverId = userIntent.getStringExtra("currentuserid")

        // Retrieve receiver's information from Firebase
        if (receiverId != null) {
            val refProfile = FirebaseDatabase.getInstance().getReference("users").child(receiverId)
            refProfile.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User_::class.java)
                    if (user != null) {
                        // Display receiver's name
                        username.text = user.name
                        // Load receiver's image using Glide
                        imageurl = user.photoLink
                        if (!imageurl.isNullOrEmpty()) {
                            Glide.with(this@privatemessage)
                                .load(user.photoLink)
                                .placeholder(R.drawable.customer)
                                .error(R.drawable.baseline_circle_24)
                                .into(imageView)
                        }
                    } else {
                        Toast.makeText(this@privatemessage, "Receiver data is null", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@privatemessage, "Failed to retrieve receiver data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@privatemessage, "Receiver ID is null", Toast.LENGTH_SHORT).show()
        }

        btn_send.setOnClickListener {
            // Send message
            val msg = text_send.text.toString()
            if (msg.isNotBlank()) {
                if (receiverId != null) {
                    sendmessage(fuser.uid, receiverId, msg)
                } else {
                    Toast.makeText(this@privatemessage, "Receiver ID is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@privatemessage, "You can't send an empty message", Toast.LENGTH_SHORT).show()
            }
            text_send.text = ""

            //audio recording
//            if(btn_send.tag== R.drawable.ic_mic){
//                Dexter.withContext(this)
//                    .withPermissions(
//                        Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ).withListener(object : MultiplePermissionsListener {
//                        @SuppressLint("ResourceType")
//                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
//                            if (p0?.areAllPermissionsGranted()==true) {
//                                try{
//                                    btn_send.tag = R.drawable.ic_stop
//                                    btn_send.setImageResource(R.drawable.ic_stop)
//
//                                    btn_send.backgroundTintList = resources.getColorStateList(R.drawable.stop_recording)
//
//                                    mediaRecorder = MediaRecorder()
//                                    mediaRecorder.setOutputFile(tempMediaOutput)
//                                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
//                                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//                                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//                                    mediaRecorder.prepare()
//                                    mediaRecorder.start()
//                                    text_send.isEnabled = false
//                                    mediaState = true
//                                } catch (e: Exception){
//                                    e.printStackTrace()
//                                    Toast.makeText(this@privatemessage, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                                }
//                            }else
//                            {
//                                Toast.makeText(this@privatemessage, "Permission Denied", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                        override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
//                            p1?.continuePermissionRequest()
//                        }
//                    }).check()
//
//            }
        }

        readmessage(fuser.uid, receiverId ?: "",imageurl)
    }

    override fun onStart() {
        super.onStart()
        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)

        //screenshotDetectionDelegate.startScreenshotDetection()
    }

    override fun onStop() {
        super.onStop()
        unregisterScreenCaptureCallback(screenCaptureCallback)
//        screenshotDetectionDelegate.stopScreenshotDetection()
    }

    private fun sendmessage(sender: String, receiver: String, message: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        reference.push().setValue(hashMap)
    }

    private fun readmessage(myid: String, userid: String, imageurl: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mchat.clear()
                for (snapshot in snapshot.children) {
                    val chat = snapshot.getValue(ChatRV::class.java)
                    if (chat != null) {
                        if (chat.receiver == myid && chat.sender == userid || chat.receiver == userid && chat.sender == myid) {
                            mchat.add(chat)
                        }
                    }
                }
                madapter = messageadapter(this@privatemessage, mchat, imageurl)
                recyclerView.adapter = madapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@privatemessage, "Failed to retrieve chat data", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
