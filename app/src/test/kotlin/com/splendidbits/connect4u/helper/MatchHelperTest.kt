package com.splendidbits.connect4u.helper

import com.splendidbits.connect4u.model.BoardState
import com.splendidbits.connect4u.model.MatchResult
import com.splendidbits.connect4u.model.PositionValue
import junit.framework.Assert.*
import org.junit.Test

class MatchHelperTest {

    private var matchHelper: MatchHelper = MatchHelper()

    @Test
    fun testFindPosition() {
        val totalColumns = 4
        val totalRows = 4

        var positionValue: PositionValue
        var moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        var wonToss = false

        positionValue = matchHelper.getPositionValue(findColumn = 2, findRow = 2, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_OPPONENT, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 1, findRow = 0, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_USER, positionValue)

        moves = listOf(1, 2, 1, 2, 1, 2, 1)
        positionValue = matchHelper.getPositionValue(findColumn = 0, findRow = 0, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_BLANK, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 1, findRow = 3, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_OPPONENT, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 2, findRow = 0, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_USER, positionValue)

        wonToss = true
        moves = listOf(0, 0, 1, 2, 0, 0, 3, 1, 1, 3, 1, 2)
        positionValue = matchHelper.getPositionValue(findColumn = 3, findRow = 1, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_OPPONENT, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 3, findRow = 2, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_BLANK, positionValue)

        wonToss = true
        moves = listOf(2, 2, 3, 2, 1)
        positionValue = matchHelper.getPositionValue(findColumn = 0, findRow = 0, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_BLANK, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 3, findRow = 0, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_USER, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 2, findRow = 2, wonToss = wonToss,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_OPPONENT, positionValue)
    }

    @Test
    fun testIsLocalPlayerTurn() {
        val totalColumns = 4
        val totalRows = 4
        val wonToss = false

        // My turn (one move remaining)
        var moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3)
        var myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)
        assertTrue(myTurn)

        // Opponent turn (two moves remaining)
        moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2)
        myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)
        assertFalse(myTurn)

        // No moves remaining
        moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)
        assertFalse(myTurn)

        // I went first.
        moves = listOf(0, 0, 1)
        myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = true, moves = moves)
        assertFalse(myTurn)

        // They won coin toss
        moves = listOf()
        myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = false, moves = moves)
        assertFalse(myTurn)

        // I won coin toss
        myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = true, moves = moves)
        assertTrue(myTurn)
    }

    @Test
    fun testDiagonalLoss() {
        val totalColumns = 4
        val totalRows = 4
        val wonToss = true
        var moves = arrayListOf(3, 0, 0, 1, 0, 1, 0, 1, 1, 3, 2, 2, 3, 2, 2, 3)

        var boardState = matchHelper.checkDiagonalWins(columns = totalColumns, rows = totalRows,
                wonToss = wonToss, moves = moves, winAmount = 4)

        assertSame(MatchResult.RESULT_LOSS, boardState.matchResult)
        assertSame(0, boardState.winPositions.get(0).column)
        assertSame(0, boardState.winPositions.get(0).row)
        assertSame(1, boardState.winPositions.get(1).column)
        assertSame(1, boardState.winPositions.get(1).row)
        assertSame(2, boardState.winPositions.get(2).column)
        assertSame(2, boardState.winPositions.get(2).row)
        assertSame(3, boardState.winPositions.get(3).column)
        assertSame(3, boardState.winPositions.get(3).row)
        assertSame(PositionValue.POSITION_OPPONENT, boardState.winPositions.get(0).value)

        moves = arrayListOf(1, 3, 0, 1, 2, 2, 0, 0, 3, 1, 3, 3, 2, 2, 1, 0)
        boardState = matchHelper.checkDiagonalWins(columns = totalColumns, rows = totalRows,
                wonToss = wonToss, moves = moves, winAmount = 4)
        assertSame(MatchResult.RESULT_LOSS, boardState.matchResult)
        assertSame(0, boardState.winPositions.get(0).column)
        assertSame(3, boardState.winPositions.get(0).row)
        assertSame(1, boardState.winPositions.get(1).column)
        assertSame(2, boardState.winPositions.get(1).row)
        assertSame(2, boardState.winPositions.get(2).column)
        assertSame(1, boardState.winPositions.get(2).row)
        assertSame(3, boardState.winPositions.get(3).column)
        assertSame(0, boardState.winPositions.get(3).row)
        assertSame(PositionValue.POSITION_OPPONENT, boardState.winPositions.get(0).value)
    }

    @Test
    fun testDiagonalWin() {
        val totalColumns = 4
        val totalRows = 4
        val wonToss = true
        val moves = arrayListOf(3, 0, 2, 3, 2, 2, 1, 0, 1, 3, 1, 0, 0, 3)

        val boardState = matchHelper.checkDiagonalWins(columns = totalColumns, rows = totalRows,
                wonToss = wonToss, moves = moves, winAmount = 4)

        assertSame(MatchResult.RESULT_WIN, boardState.matchResult)
        assertSame(0, boardState.winPositions.get(0).column)
        assertSame(3, boardState.winPositions.get(0).row)
        assertSame(1, boardState.winPositions.get(1).column)
        assertSame(2, boardState.winPositions.get(1).row)
        assertSame(2, boardState.winPositions.get(2).column)
        assertSame(1, boardState.winPositions.get(2).row)
        assertSame(3, boardState.winPositions.get(3).column)
        assertSame(0, boardState.winPositions.get(3).row)
        assertSame(PositionValue.POSITION_USER, boardState.winPositions.get(0).value)
    }

    @Test
    fun testStraightWin() {
        val totalColumns = 4
        val totalRows = 4

        // Red went first. Blue won. Horizontal win on row 3.
        var wonToss = false
        var moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        var boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)

        assertSame(MatchResult.RESULT_WIN, boardState.matchResult)
        assertSame(3, boardState.winPositions.get(0).row)
        assertSame(3, boardState.winPositions.get(1).row)
        assertSame(3, boardState.winPositions.get(2).row)
        assertSame(3, boardState.winPositions.get(3).row)
        assertSame(PositionValue.POSITION_USER, boardState.winPositions.get(0).value)

        wonToss = true
        moves = listOf(2, 2, 3, 2, 1)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves, winAmount = 3)
        assertSame(MatchResult.RESULT_WIN, boardState.matchResult)
    }

    @Test
    fun testStraightLoss() {
        val totalColumns = 4
        val totalRows = 4
        var boardState: BoardState

        // Blue went first. Red won. Vertical win on column 1.
        var wonToss = false
        var moves = listOf(1, 2, 1, 2, 1, 2, 1)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)

        assertSame(MatchResult.RESULT_LOSS, boardState.matchResult)
        assertSame(1, boardState.winPositions.get(0).column)
        assertSame(1, boardState.winPositions.get(1).column)
        assertSame(1, boardState.winPositions.get(2).column)
        assertSame(1, boardState.winPositions.get(3).column)
        assertSame(PositionValue.POSITION_OPPONENT, boardState.winPositions.get(0).value)

        // Blue went first. Red won. Horizontal win on row 1.
        wonToss = true
        moves = listOf(0, 0, 1, 2, 0, 0, 3, 1, 1, 3, 1, 2)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)

        assertSame(MatchResult.RESULT_LOSS, boardState.matchResult)
        assertSame(1, boardState.winPositions.get(0).row)
        assertSame(1, boardState.winPositions.get(1).row)
        assertSame(1, boardState.winPositions.get(2).row)
        assertSame(1, boardState.winPositions.get(3).row)
        assertSame(PositionValue.POSITION_OPPONENT, boardState.winPositions.get(0).value)

        // Blue went first. Red won. Vertical win on column  3.
        wonToss = true
        moves = listOf(1, 1, 1, 3, 1, 3, 0, 2, 0, 0, 2, 0, 2, 3, 2, 3)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)

        assertSame(MatchResult.RESULT_LOSS, boardState.matchResult)
        assertSame(3, boardState.winPositions.get(0).column)
        assertSame(3, boardState.winPositions.get(1).column)
        assertSame(3, boardState.winPositions.get(2).column)
        assertSame(3, boardState.winPositions.get(3).column)
        assertSame(PositionValue.POSITION_OPPONENT, boardState.winPositions.get(0).value)
    }

    @Test
    fun testMatchDraw() {
        val totalColumns = 4
        val totalRows = 4
        var boardState: BoardState

        // Blue went first. Red won. Vertical win on column 1.
        val wonToss = false
        var moves = listOf(3, 1, 0, 2, 0, 3, 0, 0, 1, 3, 2, 2, 1, 1, 2, 3)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)
        assertSame(MatchResult.RESULT_DRAW, boardState.matchResult)
        assertSame(0, boardState.winPositions.size)

        moves = arrayListOf(1, 1, 2, 0, 2, 1, 1, 3, 2, 2, 3, 0, 0, 0, 3, 3)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                wonToss = wonToss, moves = moves)
        assertSame(MatchResult.RESULT_DRAW, boardState.matchResult)
        assertSame(0, boardState.winPositions.size)
    }
}