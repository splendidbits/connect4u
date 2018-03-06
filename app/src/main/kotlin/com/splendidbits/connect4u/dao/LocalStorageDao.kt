package com.splendidbits.connect4u.dao

import android.content.Context
import com.splendidbits.connect4u.model.Match
import io.requery.Persistable
import io.requery.sql.KotlinEntityDataStore

class LocalStorageDao(val context: Context, val entityStore: KotlinEntityDataStore<Persistable>) {

    fun saveMatch(match: Match) {
        entityStore.upsert(match)
    }

    fun getMatches(): List<Match> {
        return entityStore.select(Match::class)
                .get()
                .toList()
    }
}