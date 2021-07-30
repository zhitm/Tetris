package com.example.tetris

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.IBinder

class BackGroundSoundManager : Service() {
    internal lateinit var player : MediaPlayer
    override fun onBind(arg : Intent): IBinder? {
        return null
    }

    override fun onCreate(){
        super.onCreate()
        var player = MediaPlayer.create(applicationContext, R.raw.song)
        player.isLooping = true
        player.setVolume(100f,100f)
        player.start()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        player.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        player.stop()
        player.release()
    }
}