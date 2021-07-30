package com.example.tetris

import android.content.Context
import android.content.SharedPreferences

class RecordsSaver(ctx: Context) {
    var record: SharedPreferences = ctx.getSharedPreferences("record", Context.MODE_PRIVATE)
    fun saveHighScore(highScore: Int){
        record.edit().putInt("HIGH_SCORE", highScore).apply()
    }
    fun getHighScore(): Int{
        return record.getInt("HIGH_SCORE", 0)
    }
    fun clearHighScore(){
        record.edit().putInt("HIGH_SCORE", 0).apply()
    }
}