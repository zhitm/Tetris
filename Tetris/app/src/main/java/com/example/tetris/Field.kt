package com.example.tetris

class Field {
    val width = 10
    val height = 20
    val table: Array<Array<Int>> = Array(height, {Array(width, {0})})
    val colorTable: Array<Array<Int>> = Array(height, {Array(width, {0})})

    fun stopFigure(fig: Figure){
        for (row in 0 until fig.height){
            for (col in 0 until fig.width){
                if (fig.table[row][col] == 1) {
                    table[row+fig.y][col+fig.x] = 1
                    colorTable[row+fig.y][col+fig.x] = fig.getColour()
                }
            }
        }
    }
    fun containsPoint(point : Vec2): Boolean{
        return ((point.x in 0 until width) && (point.y in 0 until height))
    }
    fun getRowsToClearCnt() : Int{
        var cnt = 0
        for (row in table.indices){
            var hasEmptyCell = false
            for (el in table[row]){
                if (el==0) hasEmptyCell = true
            }
            if (!hasEmptyCell) cnt++
        }
        return cnt
    }
    fun checkRowsAndClear(){
        for (row in table.indices){
            var hasEmptyCell = false
            for (el in table[row]){
                if (el==0) hasEmptyCell = true
            }
            if (!hasEmptyCell) shiftRows(row)
        }
    }
    private fun shiftRows(erasedRow : Int){
        table[erasedRow] = Array(width, {0})
        for (i in erasedRow downTo 0){
            if (i!=0) {
                table[i]=table[i-1]
                colorTable[i]=colorTable[i-1]
            }
            else {
                table[i]= Array(width, {0})
                colorTable[i]=Array(width, {0})
            }
        }
    }
}