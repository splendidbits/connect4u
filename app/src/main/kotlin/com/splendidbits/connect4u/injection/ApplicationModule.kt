package com.splendidbits.connect4u.injection

import android.content.Context
import com.splendidbits.connect4u.main.Connect4UApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ApplicationModule(private var application: Connect4UApplication) {

    @Provides
    @Singleton
    fun provideApplication(): Connect4UApplication {
        return application
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application.applicationContext
    }
}
