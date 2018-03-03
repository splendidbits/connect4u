package com.splendidbits.connect4u.activity

import android.content.Context
import android.os.Bundle
import com.splendidbits.connect4u.R
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}