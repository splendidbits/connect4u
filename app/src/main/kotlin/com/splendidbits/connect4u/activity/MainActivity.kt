package com.splendidbits.connect4u.activity

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.splendidbits.connect4u.R
import com.splendidbits.connect4u.dao.LocalStorageDao
import com.splendidbits.connect4u.event.NewGameEvent
import com.splendidbits.connect4u.fragment.GameFragment
import com.splendidbits.connect4u.fragment.MenuFragment
import com.splendidbits.connect4u.main.Connect4UApplication
import com.splendidbits.connect4u.model.Match
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity() {
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var localStorageDao: LocalStorageDao

    @Inject
    lateinit var bus: Bus

    private var matches = mutableListOf<Match>()
    private var pagerAdapter: MainPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Connect4UApplication.graph.inject(this)

        bus.register(this)
//        matches = localStorageDao.getMatches().toMutableList()
        pagerAdapter = MainPagerAdapter(supportFragmentManager)
        fragmentPager.adapter = pagerAdapter
    }

    @Subscribe
    fun newGame(newGameEvent: NewGameEvent) {
        if (newGameEvent.isCpuGame) {
            val newMatch = Match()
            newMatch.boardSize = 4
            matches.add(newMatch)
            pagerAdapter?.notifyDataSetChanged()
            fragmentPager.currentItem = matches.size+1
        }
    }

    /**
     * Pager adapter for switching between games and the menu
     */
    private inner class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            if (position == 0) {
                val menuFragment = MenuFragment.newInstance()
                return menuFragment
            }
            return GameFragment.newInstance(matches.get(index = position-1))
        }

        override fun getCount() = matches.size + 1
    }
}