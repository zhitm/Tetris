package com.example.tetris

import android.graphics.Paint
import android.view.View
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.widget.Toast

class TetrisView : View {

    private val paint = Paint()
    private var game : Game? = null
    private val viewHandler = ViewHandler(this)
    private var activity: GameActivity? = null
    private var lastMoveDown: Long = 0
    private var lastMoveNotDown: Long = 0

    override fun onDraw(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        super.onDraw(canvas)
        drawGrid(canvas)
        drawContour(canvas)
        drawField(canvas)
        drawFocusedFigure(canvas)

    }
    fun setGame(game: Game){
        this.game = game
    }
    fun setActivity(activity: GameActivity){
        this.activity = activity
    }

    private fun drawFocusedFigure(canvas: Canvas){
        val x = game!!.focusedFigure.x
        val y = game!!.focusedFigure.y

        for (rowInd in game!!.focusedFigure.table.indices){
            for (colInd in 0 until game!!.focusedFigure.width){
                if (game!!.focusedFigure.table[rowInd][colInd]==1) {
                    drawCell(canvas, x+colInd, y+rowInd, game!!.focusedFigure.getColour())
                }
            }
        }
    }
    private fun drawContour(canvas: Canvas){
        paint.style = Paint.Style.STROKE
        paint.color = MyColor.YELLOW.rgbValue
        val topX = 0f
        val topY = 0f
        val botX = game!!.field.width * BLOCK_SIZE.toFloat()
        val botY = game!!.field.height * BLOCK_SIZE.toFloat()
        val rectangle = RectF(topX, topY, botX, botY)
        rectangle.offset(OFFSET_X, OFFSET_Y)
        canvas.drawRoundRect(rectangle, 4F, 4F, paint)
    }
    private fun drawGrid(canvas: Canvas){
        paint.color = MyColor.GRAY.rgbValue
        val height = game!!.field.height
        val width = game!!.field.width
        for (i in 0..height){
            canvas.drawLine(0f+ OFFSET_X,i* BLOCK_SIZE.toFloat()+ OFFSET_Y,
                width* BLOCK_SIZE.toFloat()+ OFFSET_X,i* BLOCK_SIZE.toFloat()+ OFFSET_Y,
                paint)
        }
        for (i in 0..width){
            canvas.drawLine(i* BLOCK_SIZE.toFloat()+ OFFSET_X,  0f+ OFFSET_Y,
                i* BLOCK_SIZE.toFloat()+ OFFSET_X, height* BLOCK_SIZE.toFloat()+ OFFSET_Y,
                paint)
        }
    }
    private fun drawField(canvas: Canvas){
        for (row in 0 until game!!.field.height){
            for (col in 0 until game!!.field.width){
                if (game!!.field.table[row][col]==1)
                    drawCell(canvas, col, row, game!!.field.colorTable[row][col])
            }
        }
    }
    private fun drawCell(canvas: Canvas, x: Int, y: Int, color : Int){
        paint.style = Paint.Style.FILL
        paint.color = color
        val topY = (y*BLOCK_SIZE).toFloat()
        val topX = (x*BLOCK_SIZE).toFloat()
        val botY = ((y+1)*BLOCK_SIZE).toFloat()
        val botX = ((x+1)*BLOCK_SIZE).toFloat()
        val rectangle = RectF(topX, topY, botX, botY)
        rectangle.offset(OFFSET_X, OFFSET_Y)
        canvas.drawRoundRect(rectangle, 4F, 4F, paint)
        paint.style = Paint.Style.STROKE
        paint.color = MyColor.BLACK.rgbValue
        canvas.drawRoundRect(rectangle, 4F, 4F, paint)

    }
    fun setGameCommand(move : Game.Motions){
        if (game != null && game!!.isActive()){
            if (Game.Motions.DOWN == move){
                game?.updateField(move)
                invalidate()
                return
            }
            setGameCommandWithDelay(move)
        }
    }
    fun setGameCommandWithDelay(move: Game.Motions){
        decreaseDelayIfBigScore()
        val now = System.currentTimeMillis()
        if (move == Game.Motions.DOWN) {
            if (now - lastMoveDown > DELAY_DOWN) {
                game?.updateField(move)
                invalidate()
                lastMoveDown = now
                activity?.updateScore()
            }
            viewHandler.sleep(DELAY_DOWN.toLong())
        }
        else {
            if (now - lastMoveNotDown > DELAY) {
                game?.updateField(move)
                activity?.updateScore()
                invalidate()
                lastMoveNotDown = now
            }
        }
    }

    private fun decreaseDelayIfBigScore(){
        when {
            game!!.score>=500 -> DELAY_DOWN = 1100
            game!!.score>=1000 -> DELAY_DOWN = 1000
            game!!.score>=1500 -> DELAY_DOWN = 900
            game!!.score>=2000 -> DELAY_DOWN = 800
            game!!.score>=2500 -> DELAY_DOWN = 700
            game!!.score>=3000 -> DELAY_DOWN = 600
            game!!.score>=3500 -> DELAY_DOWN = 550
            game!!.score>=4000 -> DELAY_DOWN = 500

        }

    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private class ViewHandler(private val owner: TetrisView) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0){
                if (owner.game!=null){
                    if (owner.game!!.isActive() || owner.game!!.isOnPause() || owner.game!!.isOver()){
                        owner.setGameCommandWithDelay(Game.Motions.DOWN)
                    }
                    if (owner.game!!.isOver()){
                        Toast.makeText(owner.activity, "Game over", Toast.LENGTH_LONG).show()
                        owner.game!!.setState(Game.GameState.AWAITING_START)
                    }
                }
            }
        }
        fun sleep(delay: Long) {
            this.removeMessages(0)
            sendMessageDelayed(obtainMessage(0), delay)
        }
    }
    companion object{
        private var DELAY_DOWN = 1200
        private const val DELAY = 20
        private const val BLOCK_SIZE = 75
        const val OFFSET_X = 50f
        private const val OFFSET_Y = 50f
    }
}