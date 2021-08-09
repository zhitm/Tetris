package com.example.tetris

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BackGroundSoundService : Service() {
    internal lateinit var player : MediaPlayer
    private val binder = MyBinder()
    var isMusicOnPause = false

    inner class MyBinder : Binder() {
        fun getService() = this@BackGroundSoundService
    }
    override fun onBind(arg : Intent): IBinder {
        Log.e("myservice", "BINDED")
        continueMusic()
        return binder
    }

    override fun onCreate(){
        super.onCreate()
        Log.e("myservice", "CREATED SERVICE")
        player = MediaPlayer.create(applicationContext, R.raw.song1)
        player.isLooping = true
        player.setVolume(100f,100f)
        player.start()
    }

    override fun onDestroy() {
        Log.e("myservice", "DESTROYED SERVICE")
        player.stop()
        player.release()
    }
    fun setOnPause(){
        isMusicOnPause = true
        player.pause()
        Log.e("myservice", "pause")
    }
    fun continueMusic(){
        isMusicOnPause = false
        player.start()
        Log.e("myservice", "continue")

    }
}