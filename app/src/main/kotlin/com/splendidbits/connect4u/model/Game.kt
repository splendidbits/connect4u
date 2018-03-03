package com.splendidbits.connect4u.model

import io.requery.Entity
import io.requery.Generated
import io.requery.Key
import io.requery.Persistable

@Entity
data class Game constructor (
        @get:Key
        @get:Generated
        var id: Long = 0L,
        var cpuMatch: Boolean = true,
        var timeStarted: Long = 0L,
        var gridSizeX: Int = 4,
        var gridSizeY: Int = 4,
        var playedFirst: Boolean = false,
        var gameMoves: MutableList<Int> = mutableListOf()
) : Persistable