package com.example.tetris

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class GameActivity : AppCompatActivity() {
    private val game = Game()
    private lateinit var tetrisView: TetrisView
    private lateinit var restartB: Button
    private lateinit var pauseB: Button
    private lateinit var leftB: Button
    private lateinit var rightB: Button
    private lateinit var downB: Button
    private lateinit var rotateB: Button
    private lateinit var bestView: TextView
    private lateinit var scoreView: TextView
    private var onTouchX = 0f
    private var onTouchY = 0f
    private var downBTouchedTime = 0L
    private var onUpX = 0f
    private var onUpY = 0f
    private var touchTime = 0L
    private lateinit var recordSaver: RecordsSaver

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game)
        recordSaver = RecordsSaver(this)
        tetrisView = findViewById(R.id.view_tetris)
        restartB = findViewById(R.id.restartB)
        pauseB = findViewById(R.id.pauseB)
        leftB = findViewById(R.id.leftB)
        rightB = findViewById(R.id.rightB)
        downB = findViewById(R.id.downB)
        rotateB = findViewById(R.id.rotateB)
        bestView = findViewById(R.id.bestView)
        scoreView = findViewById(R.id.scoreView)
        tetrisView.setActivity(this)
        tetrisView.setGame(game)
        game.setState(Game.GameState.AWAITING_START)
        tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        updateBestScore()
        restartB.setOnClickListener(){
            if (game.isAwaitingStart()) {
                startGameCircle()
            }
            else {
                game.endGame()
                game.startGame()
            }
        }
        pauseB.setOnClickListener(){
            if (game.isOnPause()) game.setState(Game.GameState.ACTIVE)
            else if (game.isActive()) game.setState(Game.GameState.ON_PAUSE)
        }
        leftB.setOnClickListener(){moveFigure(Game.Motions.LEFT)}
        rightB.setOnClickListener(){moveFigure(Game.Motions.RIGHT)}
        downB.setOnTouchListener(this::onDownBTouch)
        rotateB.setOnClickListener(){moveFigure(Game.Motions.ROTATE)}

    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, BackGroundSoundService::class.java), connection, BIND_AUTO_CREATE)
        Log.e("myservice", "gameactstart")
        afterStart()

    }
    fun afterStart(){
        Log.e("myservice", "${isServiceBound} - is bound")
    }

    override fun onPause() {
        super.onPause()
        if (isServiceBound) {
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
    private fun startGameCircle(){
        game.startGame()
        tetrisView.invalidate()
        tetrisView.setGameCommandWithDelay(Game.Motions.DOWN)

    }
    private fun onDownBTouch(view: View, event: MotionEvent) : Boolean{

        val action = event.action
        val now = System.currentTimeMillis()
        when (action){
            MotionEvent.ACTION_UP -> {downBTouchedTime = System.currentTimeMillis(); moveFigure(Game.Motions.DOWN)
            }
            MotionEvent.ACTION_BUTTON_PRESS -> {if (now - downBTouchedTime >= TIME_BEFORE_FAST_MOVE) moveFigure(Game.Motions.DOWN)
            }
        }
        return true
    }
    private fun onTetrisViewTouch(view: View, event: MotionEvent) : Boolean{
        if (game.isAwaitingStart()) {
            startGameCircle()
        }
        val move = resolveTouchDirection(view, event)
        if (move != Game.Motions.PASS) moveFigure(move)
        updateScore()
        return true
    }
    private fun resolveTouchDirection(view: View, event: MotionEvent): Game.Motions {
        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                onTouchX = event.x
                onTouchY = event.y
                touchTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                val now = System.currentTimeMillis()
                if (now - touchTime > TIME_BEFORE_FAST_MOVE &&
                    now - game.focusedFigure.timeOfCreate > MIN_TIME_AFTER_FIGURE_CREATED_TO_MOVE ){
                    if (event.y - onTouchY >= MIN_DIST && abs(event.x - onTouchX) < MIN_DIST) return Game.Motions.DOWN
                }
            }
            MotionEvent.ACTION_UP -> {
                onUpX = event.x
                onUpY = event.y
                if (abs(onUpY-onTouchY) < MIN_DIST) {
                    if (abs(onUpX-onTouchX) >= MIN_DIST)
                        return if (onUpX > onTouchX) Game.Motions.RIGHT
                        else Game.Motions.LEFT
                }
                if (abs(onUpX-onTouchX) < MIN_DIST){
                    if (abs(onUpY-onTouchY) >= MIN_DIST)
                        if (onUpY > onTouchY) return Game.Motions.DOWN
                }
                if (abs(onUpY-onTouchY) < MIN_DIST && abs(onUpX-onTouchX) < MIN_DIST)
                    return Game.Motions.ROTATE
            }
        }
        return Game.Motions.PASS
    }
    private fun moveFigure(motion: Game.Motions) {
        if (game.isActive()) {
            tetrisView.setGameCommand(motion)
        }
    }
    fun updateScore(){
        scoreView.text = "Score: ${game.score}"
        if (game.score > recordSaver.getHighScore()) recordSaver.saveHighScore(game.score)
        updateBestScore()
    }
    private fun updateBestScore(){
        bestView.text = "Best score: ${recordSaver.getHighScore()}"
    }
    companion object{
        private const val MIN_DIST = 70
        private const val MIN_TIME_AFTER_FIGURE_CREATED_TO_MOVE = 300
        private const val TIME_BEFORE_FAST_MOVE = 300
    }

}