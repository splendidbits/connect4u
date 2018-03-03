package com.splendidbits.connect4u.injection

import com.splendidbits.connect4u.helper.GameHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class GameModule {

    @Provides
    @Singleton
    fun provideGameHelper(): GameHelper {
        return GameHelper()
    }
}
