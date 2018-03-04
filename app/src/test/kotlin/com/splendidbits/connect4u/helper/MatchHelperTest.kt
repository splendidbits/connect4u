package com.splendidbits.connect4u.helper

import com.splendidbits.connect4u.model.BoardState
import com.splendidbits.connect4u.model.MatchResult
import com.splendidbits.connect4u.model.PositionValue
import junit.framework.Assert.*
import org.junit.Test

class MatchHelperTest {

    private var matchHelper: MatchHelper = MatchHelper()

    @Test
    fun testFindPositionColour() {
        val totalColumns = 4
        val totalRows = 4

        var positionValue: PositionValue
        var moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        var playedFirst = false

        positionValue = matchHelper.getPositionValue(findColumn = 2, findRow = 2, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_OPPONENT, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 1, findRow = 0, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_USER, positionValue)

        moves = listOf(1, 2, 1, 2, 1, 2, 1)
        positionValue = matchHelper.getPositionValue(findColumn = 0, findRow = 0, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_BLANK, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 1, findRow = 3, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_OPPONENT, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 2, findRow = 0, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_USER, positionValue)

        playedFirst = true
        moves = listOf(0, 0, 1, 2, 0, 0, 3, 1, 1, 3, 1, 2)
        positionValue = matchHelper.getPositionValue(findColumn = 3, findRow = 1, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_OPPONENT, positionValue)

        positionValue = matchHelper.getPositionValue(findColumn = 3, findRow = 2, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionValue.POSITION_BLANK, positionValue)
    }

    @Test
    fun testIsLocalPlayerTurn() {
        val totalColumns = 4
        val totalRows = 4
        var playedFirst = false

        // My turn (one move remaining)
        var moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3)
        var myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)
        assertTrue(myTurn)

        // Opponent turn (two moves remaining)
        moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2)
        myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)
        assertFalse(myTurn)

        // No moves remaining
        moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)
        assertFalse(myTurn)

        // Opponent won with 9 moves remaining
        moves = listOf(1, 2, 1, 2, 1, 2, 1)
        myTurn = matchHelper.isLocalPlayerTurn(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)
        assertFalse(myTurn)
    }

    @Test
        fun testMatchWin() {
        val totalColumns = 4
        val totalRows = 4
        val matchResult: MatchResult

        // Red went first. Blue won. Horizontal win on row 3.
        val playedFirst = false
        val moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        val boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)

        assertSame(MatchResult.RESULT_WIN, boardState.matchResult)
        assertSame(3, boardState.winPositions.get(0).rowPosition)
        assertSame(3, boardState.winPositions.get(1).rowPosition)
        assertSame(3, boardState.winPositions.get(2).rowPosition)
        assertSame(3, boardState.winPositions.get(3).rowPosition)
        assertSame(PositionValue.POSITION_USER, boardState.winPositions.get(0).value)
    }

    @Test
    fun testMatchLoss() {
        val totalColumns = 4
        val totalRows = 4
        var boardState: BoardState

        // Blue went first. Red won. Vertical win on column 1.
        var playedFirst = false
        var moves = listOf(1, 2, 1, 2, 1, 2, 1)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)

        assertSame(MatchResult.RESULT_LOSS, boardState.matchResult)
        assertSame(1, boardState.winPositions.get(0).columnPosition)
        assertSame(1, boardState.winPositions.get(1).columnPosition)
        assertSame(1, boardState.winPositions.get(2).columnPosition)
        assertSame(1, boardState.winPositions.get(3).columnPosition)
        assertSame(PositionValue.POSITION_OPPONENT, boardState.winPositions.get(0).value)

        // Blue went first. Red won. Horizontal win on row 1.
        playedFirst = true
        moves = listOf(0, 0, 1, 2, 0, 0, 3, 1, 1, 3, 1, 2)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)

        assertSame(MatchResult.RESULT_LOSS, boardState.matchResult)
        assertSame(1, boardState.winPositions.get(0).rowPosition)
        assertSame(1, boardState.winPositions.get(1).rowPosition)
        assertSame(1, boardState.winPositions.get(2).rowPosition)
        assertSame(1, boardState.winPositions.get(3).rowPosition)
        assertSame(PositionValue.POSITION_OPPONENT, boardState.winPositions.get(0).value)

        // Blue went first. Red won. Vertical win on column  3.
        playedFirst = true
        moves = listOf(1, 1, 1, 3, 1, 3, 0, 2, 0, 0, 2, 0, 2, 3, 2, 3)
        boardState = matchHelper.getBoardState(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)

        assertSame(MatchResult.RESULT_LOSS, boardState.matchResult)
        assertSame(3, boardState.winPositions.get(0).columnPosition)
        assertSame(3, boardState.winPositions.get(1).columnPosition)
        assertSame(3, boardState.winPositions.get(2).columnPosition)
        assertSame(3, boardState.winPositions.get(3).columnPosition)
        assertSame(PositionValue.POSITION_OPPONENT, boardState.winPositions.get(0).value)
    }

    @Test
    fun testMatchDraw() {

    }
}