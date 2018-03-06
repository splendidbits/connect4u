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

        gameBoardLayout.setBoardDimensions(match.gridSizeY, match.gridSizeX, columnClickListener)
        playAgainButton.setOnClickListener(resetListener)
        resetListener.onClick(null)
    }

    private val resetListener = View.OnClickListener {
        val playerColor = if (match.playedFirst) getString(R.string.colour_blue) else getString(R.string.colour_blue)
        gameStatus.text = String.format(resources.getString(R.string.status_pending), playerColor)
        playAgainButton.visibility = View.GONE
        match.gameMoves = mutableListOf()
        gameBoardLayout.setBoardDimensions(match.gridSizeY, match.gridSizeX, columnClickListener)
    }

    private val columnClickListener = { column: Int ->
        match.gameMoves.add(column)
        getMoves(match.gameMoves)
    }

    /**
     * Makes a request to the 9DT webservice with an array of current moves.
     */
    private fun getMoves(currentMoves: List<Int>) {
        if (match.matchResult == MatchResult.RESULT_PENDING) {
            apiService.submitFetchMoves(currentMoves.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        val collectionType = object : TypeToken<List<Int>>() {}.type
                        val updatedMoves: List<Int> = Gson().fromJson(result, collectionType)
                        processNewMoves(updatedMoves)
                    }, { _ ->
                    })
        }
    }

    /**
     * Performed for any user or opponent initiated set of new moves.
     * 1) Checks for any wins or losses.
     * 2) Adds any new moves to the board.
     * 3) Persists the updated Match
     */
    private fun processNewMoves(moves: List<Int> = arrayListOf()) {
        val boardState = matchHelper.getBoardState(totalColumns = match.gridSizeY,
                totalRows = match.gridSizeX,
                playedFirst = match.playedFirst,
                moves = moves)

        addMovesToBoard(moves)

        // If we have won or loss, clear the board and highlight the winning streak
        val matchResult = boardState.matchResult
        if (matchResult == MatchResult.RESULT_WIN || matchResult == MatchResult.RESULT_LOSS) {
            playAgainButton.visibility = View.VISIBLE

            // Clear the board
            gameBoardLayout.setBoardDimensions(match.gridSizeY, match.gridSizeX, {})

            val positionValue: PositionValue
            if (matchResult == MatchResult.RESULT_WIN) {
                positionValue = PositionValue.POSITION_USER
                gameStatus.text = getString(R.string.status_won)
            } else {
                positionValue = PositionValue.POSITION_OPPONENT
                gameStatus.text = getString(R.string.status_lost)
            }

            // Add each winning position to highlight
            for (position in boardState.winPositions) {
                val chip = ChipView(aContext).setPositionValue(positionValue)
                gameBoardLayout.addChip(chip, position.column, position.row)
            }

        } else if (moves.size == match.gridSizeX * match.gridSizeY) {
            playAgainButton.visibility = View.VISIBLE
            gameStatus.text = getString(R.string.status_draw)
        }

        match.gameMoves.clear()
        match.gameMoves.addAll(moves)
    }

    /**
     * Add all the current game chips to the game board.
     */
    private fun addMovesToBoard(moves: List<Int> = mutableListOf()) {
        val currentMoves = mutableListOf<Int>()
        for (column in moves) {
            // Get the height of the move column
            val row = matchHelper.getColumnStackHeight(column, currentMoves, playedFirst = match.playedFirst)
            val isBlue = matchHelper.isLocalPlayerTurn(moves = currentMoves, playedFirst = match.playedFirst)

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