package com.example.tetris

import android.graphics.Color
import android.util.Log


class Figure(var table:  MutableList<MutableList<Int>>, private var field: Field) {
    init {
        setColor()
    }
    var x = 3
    var y = 0 //left top angle coords
    var timeOfCreate = System.currentTimeMillis()
    var width = table[0].size
    var height = table.size
    private var color : Int = 0
    private fun getRotatedTable() : MutableList<MutableList<Int>>{
        val newTable: MutableList<MutableList<Int>> = ArrayList()
        for (i in 0 until width){
            val newRow: MutableList<Int> = ArrayList()
            for (j in height-1 downTo 0) newRow.add(table[j][i])
            newTable.add(newRow)
        }
        return newTable
    }

    fun rotate(){
        table = getRotatedTable()
        width = height
        height = table.size
    }
    fun move(vector: Vec2){
        x += vector.x
        y += vector.y
    }
    fun canMoveOnVector(vec : Vec2) : Boolean {
        for (_y in table.indices) {
            for (_x in table[0].indices) {
                val point = Vec2(x+_x + vec.x, y+_y + vec.y)
                if ((!field.containsPoint(point)) || (field.table[point.y][point.x] == 1 && table[_y][_x] == 1)) return false
            }
        }
        return true
    }
    fun canBeRotated() : Boolean {
        val newTable = getRotatedTable()
        for (row in 0 until width){
            for (col in 0 until height){
                if (!field.containsPoint(Vec2(x+col, y+row))
                    || (field.table[y+row][x+col]==1 && newTable[row][col]==1)) return false
            }
        }
        return true
    }
    fun getColour(): Int {
        return color
    }
    private fun getShapeByTable() : Shape {
        for (el in enumValues<Shape>()) {
            if (el.shape == table) return el
        }
        return Shape.O
    }
    private fun setColor(){
        color = getShapeByTable().color.rgbValue
    }
}
enum class MyColor(val rgbValue: Int) {
    PINK(Color.rgb(255, 105, 180)),
    GREEN(Color.rgb(0, 128, 0)),
    ORANGE(Color.rgb(255, 140, 0)),
    YELLOW(Color.rgb(255, 255, 0)),
    CYAN(Color.rgb(0, 255, 255)),
    GRAY(Color.rgb(133,133,133)),
    BLACK(Color.rgb(0,0,0)),
    BLUE(Color.rgb(0,0,255)),
    RED(Color.rgb(255,0,0))
}

enum class Shape(var shape: MutableList<MutableList<Int>>, var color: MyColor) {
    O(arrayListOf(arrayListOf(1,1), arrayListOf(1,1)), MyColor.GREEN),
    I(arrayListOf(arrayListOf(1,1,1,1)), MyColor.ORANGE),
    T(arrayListOf(arrayListOf(0,1,0), arrayListOf(1,1,1)), MyColor.YELLOW),
    L(arrayListOf(arrayListOf(0,0,1), arrayListOf(1,1,1)), MyColor.BLUE),
    J(arrayListOf(arrayListOf(1,0,0), arrayListOf(1,1,1)), MyColor.RED),
    S(arrayListOf(arrayListOf(0,1,1), arrayListOf(1,1,0)), MyColor.CYAN),
    Z(arrayListOf(arrayListOf(1,1,0), arrayListOf(0,1,1)), MyColor.PINK)
}