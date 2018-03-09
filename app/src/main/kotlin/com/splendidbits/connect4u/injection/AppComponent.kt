package com.splendidbits.connect4u.injection

import com.splendidbits.connect4u.activity.BaseActivity
import com.splendidbits.connect4u.activity.MainActivity
import com.splendidbits.connect4u.fragment.GameFragment
import com.splendidbits.connect4u.fragment.MenuFragment
import com.splendidbits.connect4u.main.Connect4UApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationModule::class), (DatabaseModule::class), (GameModule::class)])
interface AppComponent {

    fun inject(application: Connect4UApplication)

    fun inject(baseActivity: BaseActivity)
    fun inject(mainActivity: MainActivity)

    fun inject(fragment: GameFragment)
    fun inject(fragment: MenuFragment)
}