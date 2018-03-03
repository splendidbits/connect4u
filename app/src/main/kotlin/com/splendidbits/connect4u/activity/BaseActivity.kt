package com.splendidbits.connect4u.activity

import android.support.v7.app.AppCompatActivity
import com.splendidbits.connect4u.main.Connect4UApplication

open class BaseActivity : AppCompatActivity() {
    init {
        Connect4UApplication.graph.inject(this)
    }
}
