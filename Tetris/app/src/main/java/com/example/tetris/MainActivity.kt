package com.example.tetris

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.lang.Thread.sleep


class MainActivity : AppCompatActivity() {
    private lateinit var mService: BackGroundSoundService
    var isServiceBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackGroundSoundService.MyBinder
            mService = binder.getService()
            isServiceBound = true
        }
        override fun onServiceDisconnected(className: ComponentName) {
            isServiceBound = false
        }
    }

    private fun startGame(view: View){
//        unbindService(connection)
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
        bindService(Intent(this, BackGroundSoundService::class.java), connection, BIND_AUTO_CREATE)

    }
    override fun onPause() {
        super.onPause()
        if (isServiceBound){
        mService.setOnPause()
        mService.isMusicOnPause = true
        }
    }
    override fun onResume() {
        super.onResume()
        if (isServiceBound)
        if (mService.isMusicOnPause) {
            mService.continueMusic()
            mService.isMusicOnPause = false
        }
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound)
        if (mService.isMusicOnPause) {
            mService.continueMusic()
            mService.isMusicOnPause = false
        }
    }
}