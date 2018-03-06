package com.splendidbits.connect4u.activity

import android.content.Context
import android.os.Bundle
import com.splendidbits.connect4u.R
import com.splendidbits.connect4u.fragment.GameFragment
import com.splendidbits.connect4u.model.Match
import javax.inject.Inject


class MainActivity : BaseActivity() {
    @Inject
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add the game fragment to the activity fragment holder
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.gameFragmentHolder, GameFragment.newInstance(Match()))
                .commit()
    }
}