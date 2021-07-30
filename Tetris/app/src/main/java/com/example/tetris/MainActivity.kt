package com.example.tetris

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.FileDescriptor


class MainActivity : AppCompatActivity() {
    private fun startGame(view: View){
        startActivity(Intent(this, GameActivity::class.java))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main)
        val bStart: Button = findViewById(R.id.start_game)
        bStart.setOnClickListener(this::startGame)
//        val musicManager: BackGroundSoundManager = BackGroundSoundManager()
//        val svc = Intent(this, BackGroundSoundManager::class.java)

//        musicManager.onStartCommand(svc, 0,0)
//        startService(svc)
//        val file = applicationContext.assets.openFd("song.mp3" ) as AssetFileDescriptor

//        val player = MediaPlayer()
//        player.setDataSource(file.fileDescriptor)
        var player = MediaPlayer.create(applicationContext, R.raw.song)
        player.isLooping = true
        player.setVolume(100f,100f)
        player.start()
    }
}