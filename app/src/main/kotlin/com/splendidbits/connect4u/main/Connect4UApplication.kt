package com.splendidbits.connect4u.main

import android.support.multidex.MultiDexApplication
import com.splendidbits.connect4u.injection.AppComponent
import com.splendidbits.connect4u.injection.ApplicationModule
import com.splendidbits.connect4u.injection.DaggerAppComponent
import com.splendidbits.connect4u.injection.DatabaseModule


class Connect4UApplication : MultiDexApplication() {

    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit var graph: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        graph = DaggerAppComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .databaseModule(DatabaseModule())
                .build()

        graph.inject(this)
    }
}

