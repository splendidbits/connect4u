package com.splendidbits.connect4u.helper

import com.splendidbits.connect4u.model.PositionColor
import junit.framework.Assert.*
import org.junit.Test

class GameHelperTest {

    private var gameHelper: GameHelper = GameHelper()

    @Test
    fun testHasGameFinished() {
        var moves = listOf(0, 2, 1, 4, 4, 3, 0, 3, 1)
        var hasFinished = gameHelper.hasGameFinished(5, 9, moves)
        assertFalse(hasFinished)

        moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        hasFinished = gameHelper.hasGameFinished(4, 4, moves)
        assertTrue(hasFinished)
    }

    @Test
    fun testFindColumnHeight() {
        var moves = listOf(0, 2, 1, 4, 4, 3, 0, 3, 1, 3, 0, 1, 4)
        var columnHeight = gameHelper.findColumnHeight(4, moves)
        assertEquals(3, columnHeight)

        moves = listOf(0, 2, 1, 4, 4, 3, 0, 3, 1, 3, 0, 1, 3)
        columnHeight = gameHelper.findColumnHeight(0, moves)
        assertNotSame(4, columnHeight)
    }

    @Test
    fun testFindPositionColour() {
        var positionColor: PositionColor
        val moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        val playedFirst = false
        val totalColumns = 4
        val totalRows = 4

        positionColor = gameHelper.findPositionColor(column = 2, row = 2, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionColor.POSITION_RED, positionColor)

        positionColor = gameHelper.findPositionColor(column = 1, row = 0, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionColor.POSITION_BLUE, positionColor)

        positionColor = gameHelper.findPositionColor(column = 3, row = 2, playedFirst = playedFirst,
                moves = moves, totalColumns = totalColumns, totalRows = totalRows)
        assertSame(PositionColor.POSITION_RED, positionColor)
    }

    @Test
    fun testWasMatchWin() {
        val moves = listOf(0, 1, 0, 1, 1, 0, 2, 0, 2, 1, 3, 3, 2, 2, 3, 3)
        val playedFirst = false
        val totalColumns = 4
        val totalRows = 4

        val matchWon = gameHelper.wasMatchWin(totalColumns = totalColumns, totalRows = totalRows,
                playedFirst = playedFirst, moves = moves)
        assertTrue(matchWon)
    }

    @Test
    fun testWasMatchLoss() {

    }

    @Test
    fun testWasMatchDraw() {

    }
}