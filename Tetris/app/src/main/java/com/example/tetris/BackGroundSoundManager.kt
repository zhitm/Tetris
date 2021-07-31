package com.example.tetris

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BackGroundSoundManager : Service() {
    internal lateinit var player : MediaPlayer
    private val binder = MyBinder()

    inner class MyBinder : Binder() {
        fun getService() = this@BackGroundSoundManager
    }
    override fun onBind(arg : Intent): IBinder {
        return binder
    }

    override fun onCreate(){
        super.onCreate()
        Log.e("myservice", "CREATED SERVICE")
        player = MediaPlayer.create(applicationContext, R.raw.song)
        player.isLooping = true
        player.setVolume(100f,100f)
        player.start()
    }

    override fun onDestroy() {
//        player.stop()
//        player.release()
    }
    fun setOnPause(){
        player.stop()
        Log.e("myservice", "music is on pause")
    }
    fun continueMusic(){
//        player.start()
    }
}