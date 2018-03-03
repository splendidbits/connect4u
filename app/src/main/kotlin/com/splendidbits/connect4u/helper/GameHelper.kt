package com.splendidbits.connect4u.helper

import com.splendidbits.connect4u.model.PositionColor

class GameHelper {

    /**
     * Helper method to ascertain whether a game is considered completed.
     */
    fun hasGameFinished(gridSizeX: Int = 4, gridSizeY: Int = 4, moves: List<Int> = listOf()): Boolean {
        return gridSizeX * gridSizeY == moves.size
    }

    /**
     * Helper method to find the height (not zero based) of the number of
     * chips stacked in a [column].
     */
    fun findColumnHeight(column: Int = 4, moves: List<Int> = listOf()): Int {
        var columnHeight = 0
        for (move in moves) {
            if (move == column) {
                columnHeight = columnHeight.inc()
            }
        }
        return columnHeight
    }

    /**
     * Find the color (either [PositionColor.POSITION_BLANK], [PositionColor.POSITION_BLUE], or
     * [PositionColor.POSITION_RED]) for a [column] and [row] board position.
     *
     * Note, like a graph, all positions start from 0,0 (bottom left corner of board)
     */
    fun findPositionColor(column: Int, row: Int, totalColumns: Int = 4, totalRows: Int = 4,
                          moves: List<Int> = listOf(), playedFirst: Boolean): PositionColor {

        if ((column > totalColumns -1) || (row > totalRows - 1)) {
            return PositionColor.POSITION_BLANK
        }

        return PositionColor.POSITION_BLANK
    }

    /**
     * Find out if the game was a win or loss based on the grid-size of a game-board
     * ([totalColumns] and [totalRows]) and the [moves] that have taken place in the game.
     *
     * Note, the local device player is always represented as [PositionColor.POSITION_BLUE]
     */
    fun wasMatchWin(totalColumns: Int = 4, totalRows: Int = 4, moves: List<Int> = listOf(),playedFirst: Boolean): Boolean {
        // Sanity check that the game is over.
        if (!hasGameFinished(totalColumns, totalRows, moves)) {
            return false
        }



        return false
    }
}