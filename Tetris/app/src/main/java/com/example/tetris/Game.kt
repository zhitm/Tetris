package com.example.tetris

import android.util.Log

class Game {

    enum class GameState {
        ON_PAUSE, AWAITING_START, ACTIVE, OVER
    }
    enum class Motions {
        LEFT, RIGHT, DOWN, ROTATE, PASS
    }

    var score : Int = 0
    var field: Field = Field()
    private var state = GameState.AWAITING_START
    var focusedFigure = Figure(arrayListOf(arrayListOf()), field)
    private fun addFigure(){
        val shape = getRandomShape()
        val fig = Figure(shape.shape, field)
        focusedFigure = fig
        tryEndGame()
    }
    fun isOver() : Boolean{
        return state == GameState.OVER
    }
    fun isOnPause(): Boolean{
        return state == GameState.ON_PAUSE
    }
    fun isAwaitingStart(): Boolean{
        return state == GameState.AWAITING_START
    }
    fun isActive(): Boolean{
        return state == GameState.ACTIVE
    }
    fun setState(state: GameState){
        this.state = state
    }
    fun setOnPause(){
        state = GameState.ON_PAUSE
    }
    private fun tryEndGame(){
        if (!(focusedFigure.canMoveOnVector(DOWN_VEC))) {
            state = GameState.OVER
        }
    }
    private fun boostScore(rowsFilled : Int){
        score+=100*rowsFilled
    }
    private fun getRandomShape() : Shape {
        return enumValues<Shape>().toList().shuffled().first()
    }

    fun startGame(){
        score = 0
        field = Field()
        state = GameState.ACTIVE
        addFigure()
    }
    fun endGame(){
        score = 0
        field = Field()
        state = GameState.OVER
    }

    fun updateField(move : Motions){
        Log.e("my", move.name)
        if (isActive()) {
            when (move) {
                Motions.DOWN -> {
                    if (focusedFigure.canMoveOnVector(DOWN_VEC))
                        focusedFigure.move(DOWN_VEC)
                    else {
                        field.stopFigure(focusedFigure)
                        addFigure()
                    }
                }
                Motions.LEFT -> tryMove(LEFT_VEC)
                Motions.RIGHT -> tryMove(RIGHT_VEC)
                Motions.ROTATE -> if (focusedFigure.canBeRotated())
                    focusedFigure.rotate()
            }
            boostScore(field.getRowsToClearCnt())
            field.checkRowsAndClear()
        }
    }

    private fun tryMove(vec: Vec2) {
        if (focusedFigure.canMoveOnVector(vec))
            focusedFigure.move(vec)
    }


    companion object{
        private val DOWN_VEC = Vec2(0,1)
        private val LEFT_VEC = Vec2(-1,0)
        private val RIGHT_VEC = Vec2(1,0)

    }
}