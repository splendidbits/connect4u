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

        return (moves.size % 2 == 0) == playedFirst
    }

    /**
     * Find the value of any board position (either [PositionValue.POSITION_BLANK],
     * [PositionValue.POSITION_USER], or [PositionValue.POSITION_OPPONENT]).
     */
    fun getPositionValue(findColumn: Int, findRow: Int, totalColumns: Int = 4, totalRows: Int = 4,
                         moves: List<Int> = listOf(), playedFirst: Boolean): PositionValue {

        // If the position request was outside the available grid.
        if (findColumn > totalColumns - 1 || findRow > totalRows - 1) {
            return PositionValue.POSITION_UNKNOWN
        }

        // If there has not been any drops for the column
        if (!moves.contains(findColumn)) {
            return PositionValue.POSITION_BLANK
        }

        var columnStackHeight = 0
        for ((index, currentColumn) in moves.withIndex()) {
            /*
             * Steps through each of the moves made by each user, and counts
             * each column.
             *
             * When the column count (or column stack height) has been reached,
             * work out if it's a user move or player move based on who went first
             * and if the index of this connect-4 column drop is odd or even.
             */
            val foundColumn = findColumn == currentColumn
            if (foundColumn && findRow == columnStackHeight) {
                return if (index % 2 == 0 == playedFirst)
                    PositionValue.POSITION_USER else PositionValue.POSITION_OPPONENT
            }

            // At the end of the iteration but the stackheight isn't high enough.
            if (index == moves.size - 1) {
                return PositionValue.POSITION_BLANK
            }

            // If the column found matched the find column, increment the height.
            columnStackHeight += if (foundColumn) 1 else 0
        }

        return PositionValue.POSITION_UNKNOWN
    }

    /**
     * Find out if the game was a win, loss, draw, or still playing based on the grid-size of a game-board
     * ([totalColumns] and [totalRows]) and the [moves] that have taken place in the game.
     *
     * Returns a [MatchResult] state value.
     */
    fun getBoardState(totalColumns: Int = 4, totalRows: Int = 4, moves: List<Int> = listOf(), playedFirst: Boolean): BoardState {
        return checkStraightWins(totalColumns, totalRows, moves, playedFirst)
    }

    /**
     * Checks the board for two types of cascading diagonal wins.
     */
    fun checkDiagonalWins(columns: Int, rows: Int, moves: List<Int>, playedFirst: Boolean): BoardState {
        val size = 4

        // TODO: Incomplete. I'm still working on this as I ran ot of time,
        // but can talk through what I think my approach would be.

        for (outerIndex in 0 until size) {
            System.out.print("\n[0,$outerIndex]\t")

            for (innerIndex in 1..outerIndex) {
                System.out.print("[$innerIndex,${outerIndex-innerIndex}]\t")
            }
        }

        for (outerIndex in size until 0) {
            System.out.print("\n[0,$outerIndex]\t")

            for (innerIndex in 1..outerIndex) {
                System.out.print("[$innerIndex,${outerIndex-innerIndex}]\t")
            }
        }

        return BoardState(MatchResult.RESULT_PENDING, arrayListOf())
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

    /**
     * Get the height of all the played pieces for a column
     */
    fun getColumnStackHeight(findColumn: Int, moves: List<Int>, playedFirst: Boolean): Int {
        if (!moves.contains(findColumn)) {
            return 0
        }

        var columnStackHeight = 0
        for (currentColumn in moves) {
            if (findColumn == currentColumn) {
                columnStackHeight++
            }
        }
        return columnStackHeight
    }


    fun <K, V> MutableMap<K, MutableList<V>>.add(k: K, v: V) = get(k)?.add(v)
            ?: put(k, mutableListOf(v))

    fun <K> MutableMap<K, MutableList<Position>>.lastContains(k: K, v: PositionValue, amount: Int): Boolean =
            get(k)?.lastContains(v, amount) ?: false

    fun MutableList<Position>.lastContains(v: PositionValue, amount: Int): Boolean =
            size == amount && this.takeLast(amount).all { it.value == v }
}

