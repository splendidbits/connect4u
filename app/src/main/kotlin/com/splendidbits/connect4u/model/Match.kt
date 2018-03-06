package com.splendidbits.connect4u.model

import android.os.Parcelable
import io.requery.Entity
import io.requery.Generated
import io.requery.Key
import io.requery.Persistable
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Match constructor (
        @get:Key
        @get:Generated
        var id: Long = 0L,
        var cpuMatch: Boolean = true,
        var timeStarted: Long = System.currentTimeMillis(),
        var gridSizeX: Int = 4,
        var gridSizeY: Int = 4,
        var playedFirst: Boolean = true,
        var gameMoves: MutableList<Int> = mutableListOf(),
        var matchResult: MatchResult = MatchResult.RESULT_PENDING
) : Persistable, Parcelable