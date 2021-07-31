package com.example.tetris

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var mService: BackGroundSoundManager
//    private val serviceIntent: Intent = Intent(this, BackGroundSoundManager::class.java)
    var isServiceBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackGroundSoundManager.MyBinder
            mService = binder.getService()
            isServiceBound = true
        }
        override fun onServiceDisconnected(className: ComponentName) {
            isServiceBound = false
        }
    }

    private fun startGame(view: View){
        startActivity(Intent(this, GameActivity::class.java))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main)
        val bStart: Button = findViewById(R.id.start_game)
        bStart.setOnClickListener(this::startGame)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, BackGroundSoundManager::class.java), connection, BIND_AUTO_CREATE)

    }
    override fun onPause() {
        super.onPause()
        mService.setOnPause()
    }
    override fun onResume() {
        super.onResume()
//        mService.continueMusic()
    }
}