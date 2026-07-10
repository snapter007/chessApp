package com.chessforge.core

/**
 * A square on the board. [file] is 0..7 for a..h, [rank] is 0..7 for 1..8.
 */
data class Square(val file: Int, val rank: Int) {
    init {
        require(file in 0..7 && rank in 0..7) { "Square out of bounds: file=$file rank=$rank" }
    }

    val algebraic: String
        get() = "${'a' + file}${rank + 1}"

    override fun toString(): String = algebraic

    companion object {
        /** Parses algebraic notation like "e4" into a Square. */
        fun of(algebraic: String): Square {
            require(algebraic.length == 2) { "Invalid square: $algebraic" }
            val file = algebraic[0].lowercaseChar() - 'a'
            val rank = algebraic[1] - '1'
            return Square(file, rank)
        }
    }
}
