//package com.laraib.i210865
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//
//class voicecall : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_voicecall)
//    }
//}


package com.laraib.i210865

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.laraib.i210865.databinding.ActivityMainBinding
import com.laraib.i210865.databinding.ActivityVideocallBinding // Import the correct binding
import com.laraib.i210865.databinding.ActivityVoicecallBinding
import com.laraib.i210865.media.RtcTokenBuilder2
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

class voicecall : AppCompatActivity() {
    private lateinit var binding: ActivityVoicecallBinding // Correct binding reference

    private val APP_ID = "4bc1199984de418bb7f18bb3fe07270e"
    private val CHANNEL_ID = "channel2"
    private var TOKEN:String? = null
    private val appCertificate ="d71b6d237fc1407f96b191e8d2db03d6"

    private val USER_ID = 0

    private var isjoined = false
    private var agoraEngine : RtcEngine? = null

    private val PERMISSION_ID = 12
    private val REQUESTED_PERMISSION = arrayOf(
        android.Manifest.permission.RECORD_AUDIO
    )

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSION[0]) == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpVoiceSdkEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = this
            config.mAppId = APP_ID
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine!!.enableAudio() // Enable audio only
        } catch (e: Exception) {
            Toast.makeText(this, "Error in setting up voice SDK engine", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoicecallBinding.inflate(layoutInflater)
        setContentView(binding.root)

         val tokenBuilder = RtcTokenBuilder2()
        val timestamp = (System.currentTimeMillis() / 1000 + 60).toInt()
        TOKEN = tokenBuilder.buildTokenWithUid(
            APP_ID,
            appCertificate,
            CHANNEL_ID,
            USER_ID,
            RtcTokenBuilder2.Role.ROLE_PUBLISHER,
            timestamp,
            timestamp
        )


        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSION, PERMISSION_ID)
        }
        setUpVoiceSdkEngine()

//        binding.joincall.setOnClickListener {
            joinCall()
//        }
        binding.leavecall.setOnClickListener {
            leaveCall()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.startPreview()
        agoraEngine!!.leaveChannel()

        Thread{
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun leaveCall() {
        if (!isjoined) {
            Toast.makeText(this, "Join a channel first", Toast.LENGTH_SHORT).show()
        } else {
            agoraEngine?.leaveChannel()
            Toast.makeText(this, "You left the channel", Toast.LENGTH_SHORT).show()
            isjoined = false
        }
    }

    private fun joinCall() {
        if (checkPermission()) {
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            agoraEngine?.joinChannel(TOKEN, CHANNEL_ID, USER_ID, option)
            isjoined = true
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            Toast.makeText(this@voicecall, "Remote user joined", Toast.LENGTH_SHORT).show()
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            isjoined = true
            Toast.makeText(this@voicecall, "Remote user joined $CHANNEL_ID", Toast.LENGTH_SHORT).show()
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Toast.makeText(this@voicecall, "User offline", Toast.LENGTH_SHORT).show()
        }
    }
}

