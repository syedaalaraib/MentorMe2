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
import com.laraib.i210865.databinding.ActivityVideocallBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.IRtcEngineEventHandler.PERMISSION
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class videocall : AppCompatActivity() {
    private lateinit var binding: ActivityVideocallBinding

    private val APP_ID = "3d26b3dd23134b7582987e8d070f1000"
    private val CHANNEL_ID = "channel1"
    private val TOKEN = "007eJxTYFCZE1lWZcbSYWtz9EYZv6nPuyn6U6UzJp4tcIw/vSryxB8FBuMUI7Mk45QUI2NDY5Mkc1MLI0sL81SLFANzgzRDAwMDPsffqQ2BjAxJYtuZGBkgEMTnYEjOSMzLS80xZGAAAC0THoM="
    private val USER_ID = 0

    private var isjoined = false
    private var agoraEngine : RtcEngine? = null
    private var localSurfaceView : SurfaceView? = null
    private var remoteSurfaceView : SurfaceView? = null


    private val PERMISSION_ID = 12
    private val REQUESTED_PERMISSION = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )
    private fun checkPermission(): Boolean {
       return !(ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSION[0])
               != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSION[1])
               != PackageManager.PERMISSION_GRANTED)

    }

    private fun setUpVideoSdkEngine(){
        try{
            val config = RtcEngineConfig()
            config.mContext = this
            config.mAppId = APP_ID
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine!!.enableVideo()
        } catch (e: Exception){
            Toast.makeText(this, "error in setting up video sdk engine", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideocallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!checkPermission()){
            ActivityCompat.requestPermissions(this,REQUESTED_PERMISSION, PERMISSION_ID)
        }
        setUpVideoSdkEngine()

//        binding.joincall.setOnClickListener {
            joincall()
//        }
        binding.leavecall.setOnClickListener {
            leavecall()
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

    private fun leavecall() {
        if (!isjoined){
            Toast.makeText(this, "join a channel first", Toast.LENGTH_SHORT).show()
        }
        else{
            agoraEngine!!.leaveChannel()
            Toast.makeText(this, "you left the channel", Toast.LENGTH_SHORT).show()
            if (remoteSurfaceView != null){
                remoteSurfaceView!!.visibility = GONE
            }
            if (localSurfaceView != null){
                localSurfaceView!!.visibility = GONE
            }
            isjoined = false
        }
    }

    private fun joincall() {
        if (checkPermission()) {
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo() // Make sure setupLocalVideo is called to initialize localSurfaceView
            localSurfaceView?.visibility = View.VISIBLE // Use null-safe call to prevent NullPointerException
            agoraEngine?.startPreview()
            agoraEngine?.joinChannel(TOKEN, CHANNEL_ID, USER_ID, option)
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun joincall() {
//        if (checkPermission()){
//            val option = ChannelMediaOptions()
//            option.channelProfile= Constants.CHANNEL_PROFILE_COMMUNICATION
//            option.clientRoleType= Constants.CLIENT_ROLE_BROADCASTER
//            setupLocalVideo()
//            localSurfaceView!!.visibility = View.VISIBLE
//            agoraEngine!!.startPreview()
//            agoraEngine!!.joinChannel(TOKEN, CHANNEL_ID, USER_ID, option)
//
//        }
//        else{
//            Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show()
//        }
//    }

    private val mRtcEventHandler: IRtcEngineEventHandler =
        object : IRtcEngineEventHandler(){
            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                Toast.makeText(this@videocall, "remote user joined", Toast.LENGTH_SHORT).show()
            }

            override fun onUserJoined(uid: Int, elapsed: Int) {
                isjoined = true
                Toast.makeText(this@videocall, "remote user joined $CHANNEL_ID", Toast.LENGTH_SHORT).show()
                runOnUiThread{
                    setupRemoteVideo(uid)
                }

            }

            override fun onUserOffline(uid: Int, reason: Int) {
                Toast.makeText(this@videocall, "user offline", Toast.LENGTH_SHORT).show()
                runOnUiThread{
                    remoteSurfaceView!!.visibility = GONE
                }
            }

    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteSurfaceView)
        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        remoteSurfaceView!!.visibility = View.VISIBLE
    }

    private fun setupLocalVideo() {
        // Remove the local declaration of localSurfaceView
        localSurfaceView = SurfaceView(baseContext)
        binding.localVideoViewContainer.addView(localSurfaceView)
        agoraEngine?.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }


//    private fun setupLocalVideo(){
//        val localSurfaceView = SurfaceView(baseContext)
//        binding.localVideoViewContainer.addView(localSurfaceView)
//        agoraEngine!!.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
//    }
}