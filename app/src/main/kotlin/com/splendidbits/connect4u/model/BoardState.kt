package com.splendidbits.connect4u.model

data class BoardState(val matchResult: MatchResult = MatchResult.RESULT_PENDING, val winPositions: List<Position>)