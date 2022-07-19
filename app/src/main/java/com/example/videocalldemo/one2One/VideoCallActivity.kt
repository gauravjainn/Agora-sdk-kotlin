package com.example.videocalldemo.one2One

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.isVisible
import com.example.videocalldemo.R
import com.example.videocalldemo.databinding.ActivityVideoCallBinding
import com.example.videocalldemo.utils.APP_ID
import com.example.videocalldemo.utils.token
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import java.lang.Exception

class VideoCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoCallBinding

    private var channelName: String = ""

    private lateinit var mRtcEngine: RtcEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initializeAgoraEngineJoinCall()
        setOnClickListener()
    }


    private fun init() {
        channelName = intent.getStringExtra("channelName").toString()
    }

    private fun initializeAgoraEngineJoinCall() {
        initializeAgoraEngine()
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine.setClientRole(1)
        mRtcEngine.enableVideo()
        mRtcEngine.enableAudio()
        setUpLocalVideo()

        joinChannel()
    }

    private fun joinChannel() {
        mRtcEngine.joinChannel(token, channelName, null, 0)
    }

    private fun setOnClickListener() {
        binding.mic.setOnClickListener { onLocalAudioMuteClicked(binding.mic) }
        binding.switchCamera.setOnClickListener { onSwitchCameraClicked() }
        binding.endCall.setOnClickListener { onEndCallClicked() }
    }


    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
        } catch (e: Exception) {
            println("Exception: $e")
        }
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                setUpRemoteVideo(uid)
            }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread { println("Join Channel success:: $uid") }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                onRemoteUserLeft()
            }
        }
    }

    private fun setUpRemoteVideo(uid: Int) {
        if (binding.remoteVideoView.childCount >= 1) return

        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        binding.remoteVideoView.addView(surfaceView)
        mRtcEngine.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FILL, uid))
        surfaceView.tag = uid
    }

    private fun setUpLocalVideo() {
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        binding.localVideoView.addView(surfaceView)
        mRtcEngine.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FILL, 0))
    }

    private fun onRemoteUserLeft() {
        binding.remoteVideoView.removeAllViews()
    }

    private fun onLocalAudioMuteClicked(view: View) {
        val iv = view as ImageFilterView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.teal_200), PorterDuff.Mode.MULTIPLY)
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected)
    }

    private fun onSwitchCameraClicked() {
        mRtcEngine.switchCamera()
    }

    private fun onEndCallClicked() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine.leaveChannel()
        RtcEngine.destroy()
    }
}