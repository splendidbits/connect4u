package com.splendidbits.connect4u.helper

import android.util.Log
import com.splendidbits.connect4u.model.BoardState
import com.splendidbits.connect4u.model.MatchResult
import com.splendidbits.connect4u.model.Position
import com.splendidbits.connect4u.model.PositionValue
import java.util.*

class MatchHelper {

    /**
     * Return whether it is the local player's turn.
     */
    fun isLocalPlayerTurn(totalColumns: Int = 4, totalRows: Int = 4, moves: List<Int> = listOf(), wonToss: Boolean): Boolean {
        // Don't do any more calculations if the board is full
        if (totalColumns * totalRows == moves.size) {
            return false
        }

        return (moves.size % 2 == 0) == wonToss
    }

    /**
     * Find the value of any board position (either [PositionValue.POSITION_BLANK],
     * [PositionValue.POSITION_USER], or [PositionValue.POSITION_OPPONENT]).
     */
    fun getPositionValue(findColumn: Int, findRow: Int, totalColumns: Int = 4, totalRows: Int = 4,
                         moves: List<Int> = listOf(), wonToss: Boolean): PositionValue {

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
                return if (index % 2 == 0 == wonToss)
                    PositionValue.POSITION_USER else PositionValue.POSITION_OPPONENT
            }

            // At the end of the iteration but the stack height isn't high enough.
            if (index == moves.size - 1) {
                return PositionValue.POSITION_BLANK
            }

            // If the column found matched the find column, increment the height.
            columnStackHeight += if (foundColumn) 1 else 0
        }

        return PositionValue.POSITION_UNKNOWN
    }

    /**
     * Very very basic AI that tries to work out what the next column
     * move should be
     */
    fun getComputerMove(totalColumns: Int = 4, totalRows: Int = 4, moves: List<Int> = listOf(),
                        wonToss: Boolean, winLength: Int = 4): Int {

        val straightMoves = checkStraightWins(totalColumns, totalRows, moves, wonToss, winLength - 1)

        // If there are any moves that are about to win. (makes a mistake 25% of the time)
        val randomOneInFour = Random().nextInt(4)
        if (straightMoves.matchResult == MatchResult.RESULT_WIN && randomOneInFour != 0) {
            // Get the last position
            val position = straightMoves.winPositions[straightMoves.winPositions.size - 1]
            val previousPosition = straightMoves.winPositions[straightMoves.winPositions.size - 2]

            // Potential column win
            val columnFull = getColumnStackHeight(position.column, moves) == totalRows
            if (position.column == previousPosition.column && !columnFull) {
                Log.d("MatchHelper", "Found potential win column ${position.column}")
                return position.column
            }

            // Potential row win
            if (position.row == previousPosition.row) {
                // Check column to left
                val startColumn = straightMoves.winPositions[0].column - 1
                if (startColumn >= 0) {
                    val columnHeight = getColumnStackHeight(startColumn, moves)

                    val positionLeft = getPositionValue(startColumn, columnHeight - 1,
                            totalColumns, totalRows, moves, wonToss)
                    if (columnHeight != totalColumns && positionLeft == PositionValue.POSITION_BLANK) {
                        Log.d("MatchHelper", "Found potential row win column $startColumn")
                        return startColumn
                    }
                }

                // Check column to right
                val endColumn = position.column + 1
                if (endColumn + 1 <= totalColumns) {
                    val columnHeight = getColumnStackHeight(endColumn, moves)
                    val positionRight = getPositionValue(endColumn, columnHeight - 1,
                            totalColumns, totalRows, moves, wonToss)
                    if (columnHeight != totalColumns && positionRight == PositionValue.POSITION_BLANK) {
                        Log.d("MatchHelper", "Found potential row win column $endColumn")
                        return endColumn
                    }
                }
            }
        }

        // Get the last two cpu column positions (50% chance)
        if (moves.size > 3 && Random().nextInt(2) == 1) {
            val previousPosition = moves[moves.size - 2]
            val previousButOnePosition = moves[moves.size - 3]
            if (previousPosition == previousButOnePosition) {
                if (getColumnStackHeight(previousPosition, moves) < totalRows) {
                    Log.d("MatchHelper", "Using last cpu column $previousPosition")
                    return previousPosition
                }
            }
        }

        // Just chose a random column!
        var randomColumn = Random().nextInt(totalColumns)
        while (getColumnStackHeight(randomColumn, moves) >= totalRows) {
            randomColumn = Random().nextInt(totalColumns)
            Log.d("MatchHelper", "Finding unfilled random column $randomColumn")
        }

        return randomColumn
    }

    /**
     * Find out if the game was a win, loss, draw, or still playing based on the grid-size of a game-board
     * ([totalColumns] and [totalRows]) and the [moves] that have taken place in the game.
     *
     * Returns a [MatchResult] state value.
     */
    fun getBoardState(totalColumns: Int = 4, totalRows: Int = 4, moves: List<Int> = listOf(),
                      wonToss: Boolean, winLength: Int = 4): BoardState {
        val straightBoard = checkStraightWins(totalColumns, totalRows, moves, wonToss, winLength)
        val diagonalBoard = checkDiagonalWins(totalColumns, totalRows, moves, wonToss, winLength)
        val gameFinished = moves.size == totalColumns * totalRows

        if (straightBoard.matchResult == MatchResult.RESULT_PENDING && gameFinished &&
                diagonalBoard.matchResult == MatchResult.RESULT_PENDING && gameFinished) {
            return BoardState(MatchResult.RESULT_DRAW, listOf())

        } else if (straightBoard.matchResult == MatchResult.RESULT_WIN ||
                straightBoard.matchResult == MatchResult.RESULT_LOSS) {
            return straightBoard

        } else if (diagonalBoard.matchResult == MatchResult.RESULT_WIN ||
                diagonalBoard.matchResult == MatchResult.RESULT_LOSS) {
            return diagonalBoard
        }

        return BoardState(MatchResult.RESULT_PENDING, listOf())
    }

    /**
     * Checks the board for any straight vertical or horizontal wins or losses.
     */
    private fun checkStraightWins(columns: Int, rows: Int, moves: List<Int>, wonToss: Boolean, winLength: Int): BoardState {
        val lastColumnValues = mutableMapOf<Int, MutableList<Position>>()
        val lastRowValues = mutableMapOf<Int, MutableList<Position>>()

        for (column in 0 until columns) {
            for (row in 0 until rows) {

                val positionValue = getPositionValue(column, row, columns, rows, moves, wonToss)
                val position = Position(column, row, positionValue)

                val potentialResult = if (positionValue == PositionValue.POSITION_USER)
                    MatchResult.RESULT_WIN else MatchResult.RESULT_LOSS

                // Save the current cell position value and coordinates for comparison.
                lastColumnValues.add(column, position)
                lastRowValues.add(row, position)

                // Check if there were 3 previously stored values for the same column.
                if (lastColumnValues.hasSeriesMatch(column, winLength)) {
                    return BoardState(potentialResult, lastColumnValues.get(column)?.toList()?.takeLast(winLength)!!)
                }

                // Check if there were 3 previously stored values for the same row
                if (lastRowValues.hasSeriesMatch(row, winLength)) {
                    return BoardState(potentialResult, lastRowValues.get(row)?.toList()?.takeLast(winLength)!!)
                }
            }
        }

        return BoardState(MatchResult.RESULT_PENDING, arrayListOf())
    }

    /**
     * Checks the board for two types of cascading diagonal wins.
     */
    fun checkDiagonalWins(columns: Int, rows: Int, moves: List<Int>, wonToss: Boolean, winLength: Int): BoardState {
        val BOTTOM_LEFT = 0
        val BOTTOM_RIGHT = 1
        val TOP_LEFT = 2
        val TOP_RIGHT = 3

        for (i in 0 until columns) {
            val cornerDiagonalValues = mutableMapOf<Int, MutableList<Position>>()
            val maxIndex = columns - 1
            var positionValue = getPositionValue(0, i, columns, rows, moves, wonToss)
            cornerDiagonalValues.add(BOTTOM_LEFT, Position(0, i, positionValue))

            positionValue = getPositionValue(maxIndex, maxIndex - i, columns, rows, moves, wonToss)
            cornerDiagonalValues.add(BOTTOM_RIGHT, Position(maxIndex, maxIndex - i, positionValue))

            positionValue = getPositionValue(0, maxIndex - i, columns, rows, moves, wonToss)
            cornerDiagonalValues.add(TOP_LEFT, Position(0, maxIndex - i, positionValue))

            positionValue = getPositionValue(maxIndex - i, 0, columns, rows, moves, wonToss)
            cornerDiagonalValues.add(TOP_RIGHT, Position(maxIndex - i, 0, positionValue))

            for (j in 1..i) {
                positionValue = getPositionValue(j, i - j, columns, rows, moves, wonToss)
                cornerDiagonalValues.add(BOTTOM_LEFT, Position(j, i - j, positionValue))

                positionValue = getPositionValue(maxIndex - j, (maxIndex - i) + j, columns, rows, moves, wonToss)
                cornerDiagonalValues.add(BOTTOM_RIGHT, Position(maxIndex - j, (maxIndex - i) + j, positionValue))

                positionValue = getPositionValue(j, (maxIndex - i) + j, columns, rows, moves, wonToss)
                cornerDiagonalValues.add(TOP_LEFT, Position(j, (maxIndex - i) + j, positionValue))

                positionValue = getPositionValue((maxIndex - i) + j, j, columns, rows, moves, wonToss)
                cornerDiagonalValues.add(TOP_RIGHT, Position((maxIndex - i) + j, j, positionValue))
            }

            for (corner in 0 until 4) {
                if (cornerDiagonalValues.hasSeriesMatch(corner, winLength)) {
                    val positions = cornerDiagonalValues.get(corner)?.toList()?.takeLast(winLength)!!
                    val potentialResult = if (positions.get(0).value == PositionValue.POSITION_USER)
                        MatchResult.RESULT_WIN else MatchResult.RESULT_LOSS
                    return BoardState(potentialResult, positions)
                }
            }
        }

        return BoardState(MatchResult.RESULT_PENDING, arrayListOf())
    }

    /**
     * Get the height of all the played pieces for a column
     */
    fun getColumnStackHeight(findColumn: Int, moves: List<Int>): Int {
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

    fun MutableMap<Int, MutableList<Position>>.hasSeriesMatch(k: Int, amount: Int): Boolean {
        val lastAmount = get(k)?.takeLast(amount) ?: listOf()
        if (lastAmount.size < amount) {
            return false
        }

        for ((index, position) in lastAmount.withIndex()) {
            if (index == 0) continue
            val previousPositionValue = lastAmount.get(index - 1).value
            if (previousPositionValue != position.value || position.value == PositionValue.POSITION_BLANK) {
                return false
            }
        }
        return true
    }
}

