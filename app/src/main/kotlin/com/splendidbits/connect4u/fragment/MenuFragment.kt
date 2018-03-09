package com.splendidbits.connect4u.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.splendidbits.connect4u.R
import com.splendidbits.connect4u.dao.LocalStorageDao
import com.splendidbits.connect4u.event.NewGameEvent
import com.splendidbits.connect4u.main.Connect4UApplication
import com.squareup.otto.Bus
import kotlinx.android.synthetic.main.fragment_menu.*
import javax.inject.Inject


class MenuFragment : Fragment() {
    @Inject
    lateinit var localStorageDao: LocalStorageDao

    @Inject
    lateinit var aContext: Context

    @Inject
    lateinit var bus: Bus

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Connect4UApplication.graph.inject(this)
        return LayoutInflater.from(context).inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newCpuMatchButton.setOnClickListener {
            bus.post(NewGameEvent(true))
        }
        newHumanMatchButton.setOnClickListener({
            bus.post(NewGameEvent(false))
        })
    }

    companion object {
        fun newInstance(): MenuFragment {
            val fragment = MenuFragment()
            return fragment
        }
    }
}