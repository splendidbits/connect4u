package com.splendidbits.connect4u.dao

import android.content.Context
import io.requery.Persistable
import io.requery.sql.KotlinEntityDataStore

class LocalStorageDao(val context: Context, val entityStore: KotlinEntityDataStore<Persistable>) {

//    fun saveMessage(message: TextMessage) {
//        entityStore.insert(message, Long::class)
//    }
}