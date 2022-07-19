package com.example.videocalldemo

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.videocalldemo.databinding.ActivityMainBinding
import com.example.videocalldemo.one2One.VideoCallActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var requestCode = 22

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_VideoCallDemo)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
        setOnClickListener()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            requestCode
        )
    }

    private fun setOnClickListener() {
        binding.joinCall.setOnClickListener {
            val channelName = binding.channel.text.toString()
            startActivity(
                Intent(
                    this@MainActivity,
                    VideoCallActivity::class.java
                ).putExtra("channelName", channelName).putExtra("userRole", 1)
            )
        }
    }
}