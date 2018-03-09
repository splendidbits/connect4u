package com.splendidbits.connect4u.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.splendidbits.connect4u.R
import com.splendidbits.connect4u.dao.ApiService
import com.splendidbits.connect4u.dao.LocalStorageDao
import com.splendidbits.connect4u.helper.MatchHelper
import com.splendidbits.connect4u.main.Connect4UApplication
import com.splendidbits.connect4u.model.Match
import com.splendidbits.connect4u.model.MatchResult
import com.splendidbits.connect4u.model.PositionValue
import com.splendidbits.connect4u.view.ChipView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_game.*
import javax.inject.Inject


class GameFragment : Fragment() {

    private var match: Match = Match()
    private lateinit var apiService: ApiService

    @Inject
    lateinit var matchHelper: MatchHelper

    @Inject
    lateinit var localStorageDao: LocalStorageDao

    @Inject
    lateinit var aContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Connect4UApplication.graph.inject(this)
        match = arguments?.get(BUNDLE_MATCH_KEY) as Match
        apiService = ApiService.create(getString(R.string.api_base_url))

        return LayoutInflater.from(context).inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameBoardLayout.setBoardDimensions(match.boardSize, match.boardSize, columnClickListener)

        playAgainButton.setOnClickListener(resetListener)
        resetListener.onClick(null)
        processNewMoves(match.gameMoves)
    }

    private val resetListener = View.OnClickListener {
        val playerColor = if (match.wonToss) getString(R.string.colour_blue) else getString(R.string.colour_blue)
        gameStatus.text = String.format(resources.getString(R.string.status_pending), playerColor)
        playAgainButton.visibility = View.GONE
        match.gameMoves = mutableListOf()
        gameBoardLayout.setBoardDimensions(match.boardSize, match.boardSize, columnClickListener)
    }

    private val columnClickListener = { column: Int ->
        if (matchHelper.getColumnStackHeight(column, match.gameMoves) < match.boardSize) {
            match.gameMoves.add(column)
            getMoves(match)
        }
    }

    /**
     * Makes a request to the 9DT webservice with an array of current moves.
     */
    private fun getMoves(match: Match) {
        if (match.boardSize < 5 && match.boardSize < 5) {
            apiService.submitFetchMoves(match.gameMoves.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        val collectionType = object : TypeToken<List<Int>>() {}.type
                        val updatedMoves: List<Int> = Gson().fromJson(result, collectionType)
                        processNewMoves(updatedMoves)
                    }, { _ ->
                    })
        } else {
            val updatedMoves = mutableListOf<Int>()
            updatedMoves.addAll(match.gameMoves)
            updatedMoves.add(matchHelper.getComputerMove(match.boardSize, match.boardSize,
                    match.gameMoves, match.wonToss, match.winLength))
            processNewMoves(updatedMoves)
        }
    }

    /**
     * Performed for any user or opponent initiated set of new moves.
     * 1) Checks for any wins or losses.
     * 2) Adds any new moves to the board.
     * 3) Persists the updated Match
     */
    private fun processNewMoves(moves: List<Int> = arrayListOf()) {
        val boardState = matchHelper.getBoardState(
                totalColumns = match.boardSize,
                totalRows = match.boardSize,
                wonToss = match.wonToss,
                moves = moves,
                winLength = match.winLength)

        addMovesToBoard(moves)

        // If we have won or loss, clear the board and highlight the winning streak
        if (boardState.matchResult == MatchResult.RESULT_DRAW) {
            playAgainButton.visibility = View.VISIBLE
            gameStatus.text = getString(R.string.status_draw)

        } else if (boardState.matchResult == MatchResult.RESULT_WIN) {
            gameBoardLayout.setBoardDimensions(match.boardSize, match.boardSize, {})
            playAgainButton.visibility = View.VISIBLE
            gameStatus.text = getString(R.string.status_won)

            // Add each winning position to highlight
            for (position in boardState.winPositions) {
                val chip = ChipView(aContext).setPositionValue(PositionValue.POSITION_USER)
                gameBoardLayout.addChip(chip, position.column, position.row)
            }

        } else if (boardState.matchResult == MatchResult.RESULT_LOSS) {
            gameBoardLayout.setBoardDimensions(match.boardSize, match.boardSize, {})
            playAgainButton.visibility = View.VISIBLE
            gameStatus.text = getString(R.string.status_lost)

            // Add each winning position to highlight
            for (position in boardState.winPositions) {
                val chip = ChipView(aContext).setPositionValue(PositionValue.POSITION_OPPONENT)
                gameBoardLayout.addChip(chip, position.column, position.row)
            }
        }

        match.gameMoves.clear()
        match.gameMoves.addAll(moves)

        localStorageDao.saveMatch(match)
    }

    /**
     * Add all the current game chips to the game board.
     */
    private fun addMovesToBoard(moves: List<Int> = mutableListOf()) {
        val currentMoves = mutableListOf<Int>()
        for (column in moves) {
            // Get the height of the move column
            val row = matchHelper.getColumnStackHeight(column, currentMoves)
            val isBlue = matchHelper.isLocalPlayerTurn(moves = currentMoves, wonToss = match.wonToss
            )

            val positionValue = if (isBlue) PositionValue.POSITION_USER else PositionValue.POSITION_OPPONENT
            val chip = ChipView(aContext).setPositionValue(positionValue)

            // Insert the chip
            gameBoardLayout.addChip(chip, column, row)
            currentMoves.add(column)
        }
    }

    companion object {
        private const val BUNDLE_MATCH_KEY: String = "bundle_match"

        fun newInstance(match: Match): GameFragment {
            val args = Bundle()
            args.putParcelable(BUNDLE_MATCH_KEY, match)
            val fragment = GameFragment()
            fragment.arguments = args
            return fragment
        }
    }
}