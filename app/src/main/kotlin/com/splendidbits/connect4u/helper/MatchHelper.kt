package com.splendidbits.connect4u.helper

import com.splendidbits.connect4u.model.BoardState
import com.splendidbits.connect4u.model.MatchResult
import com.splendidbits.connect4u.model.Position
import com.splendidbits.connect4u.model.PositionValue

class MatchHelper {

    /**
     * Return whether it is the local player's turn.
     */
    fun isLocalPlayerTurn(totalColumns: Int = 4, totalRows: Int = 4, moves: List<Int> = listOf(), playedFirst: Boolean): Boolean {
        // Don't do any more calculations if the board is full
        if (totalColumns * totalRows == moves.size) {
            return false
        }

        // Check that the game is still in progress
        val boardState = getBoardState(totalColumns = totalColumns, totalRows = totalRows, moves = moves, playedFirst = playedFirst)
        if (MatchResult.RESULT_PENDING != boardState.matchResult) {
            return false
        }

        return (moves.size % 2 == 0) == playedFirst
    }

    /**
     * Find the value of a game slot (either [PositionValue.POSITION_BLANK], [PositionValue.POSITION_USER],
     * or [PositionValue.POSITION_OPPONENT]) for a [findColumn] and [findRow] position.
     *
     * Note, like a graph, all positions start from 0,0 (bottom left corner of board)
     */
    fun getPositionValue(findColumn: Int, findRow: Int, totalColumns: Int = 4, totalRows: Int = 4,
                         moves: List<Int> = listOf(), playedFirst: Boolean): PositionValue {

        if (findColumn > totalColumns -1 || findRow > totalRows - 1) {
            return PositionValue.POSITION_BLANK
        }

        var columnStackHeight = 0
        for ((index, currentColumn) in moves.withIndex()) {
            if (currentColumn == findColumn) {

                /*
                 * Steps through each of the moves made by each user, and counts
                 * each column.
                 *
                 * When the column count (or column stack height) has been reached,
                 * work out if it's a user move or player move based on who went first
                 * and if the index of this connect-4 column drop is odd or even.
                 */
                val isEven = index % 2 == 0
                if (columnStackHeight == findRow && isEven == playedFirst) {
                    return PositionValue.POSITION_USER

                } else if (columnStackHeight == findRow) {
                    return PositionValue.POSITION_OPPONENT
                }
                columnStackHeight++
            }
        }

        return PositionValue.POSITION_BLANK
    }

    /**
     * Find out if the game was a win, loss, draw, or still playing based on the grid-size of a game-board
     * ([totalColumns] and [totalRows]) and the [moves] that have taken place in the game.
     *
     * Returns a [MatchResult] state value.
     */
    fun getBoardState(totalColumns: Int = 4, totalRows: Int = 4, moves: List<Int> = listOf(), playedFirst: Boolean): BoardState {
        // [1, 1, 1, 3, 1, 3, 0, 2, 0, 0, 2, 0, 2, 3, 2, 3]

        // Search for a column win or loss (vertically wins from left to right, starting at 0,0)

        return checkStraightWins(4, 4, moves, playedFirst)
//        return BoardState(MatchResult.RESULT_PENDING, listOf())
    }

    /**
     * Checks the board for any straight vertical or horizontal wins or losses.
     */
    private fun checkStraightWins(columns: Int, rows: Int, moves: List<Int>, playedFirst: Boolean): BoardState {
        val lastColumnValues = mutableMapOf<Int, MutableList<Position>>()
        val lastRowValues = mutableMapOf<Int, MutableList<Position>>()

        for (column in 0 until columns) {
            for (row in 0 until rows) {

                val positionValue = getPositionValue(column, row, columns, rows, moves, playedFirst)
                val position = Position(column, row, positionValue)

                val potentialResult = if (positionValue == PositionValue.POSITION_USER)
                    MatchResult.RESULT_WIN else MatchResult.RESULT_LOSS

                if (positionValue != PositionValue.POSITION_BLANK) {
                    // Check if there were 3 previously stored values for the same column.
                    if (lastColumnValues.lastContains(column, positionValue, 3)) {
                        lastColumnValues.add(column, position)
                        return BoardState(potentialResult, lastColumnValues.get(column)?.toList()!!)
                    }

                    // Check if there were 3 previously stored values for the same row
                    if (lastRowValues.lastContains(row, positionValue, 3)) {
                        lastRowValues.add(row, position)
                        return BoardState(potentialResult, lastRowValues.get(row)?.toList()!!)
                    }
                }

                // Save the current cell position value and coordinates for comparison.
                lastColumnValues.add(column, position)
                lastRowValues.add(row, position)
            }
        }

        return BoardState(MatchResult.RESULT_PENDING, arrayListOf())
    }

    fun checkDiagonalWins(totalColumns: Int = 4, totalRows: Int = 4, moves: List<Int> = listOf(), playedFirst: Boolean) {

    }

    fun <K, V> MutableMap<K, MutableList<V>>.add(k: K, v: V) = get(k)?.add(v) ?: put(k, mutableListOf(v))

    fun <K> MutableMap<K, MutableList<Position>>.lastContains(k: K, v: PositionValue, amount: Int): Boolean =
        get(k)?.lastContains(v, amount) ?: false

    fun MutableList<Position>.lastContains(v: PositionValue, amount: Int): Boolean =
        size == amount && this.takeLast(amount).all { it.value == v }
}

