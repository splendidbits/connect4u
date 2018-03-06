package com.splendidbits.connect4u.injection

import android.content.Context
import com.splendidbits.connect4u.BuildConfig
import com.splendidbits.connect4u.dao.LocalStorageDao
import com.splendidbits.connect4u.model.Models
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.sql.KotlinEntityDataStore
import javax.inject.Singleton


@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): KotlinEntityDataStore<Persistable> {
        val source = DatabaseSource(context, Models.DEFAULT, 1)
        if (BuildConfig.DEBUG) {
//            source.setTableCreationMode(TableCreationMode.DROP_CREATE)
        }

        return KotlinEntityDataStore(source.configuration)
    }

    @Provides
    @Singleton
    fun provideStorageDaoDatabase(context: Context, entityStore: KotlinEntityDataStore<Persistable>): LocalStorageDao {
        return LocalStorageDao(context, entityStore)
    }
}
