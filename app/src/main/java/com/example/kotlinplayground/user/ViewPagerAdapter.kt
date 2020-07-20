package com.example.kotlinplayground.user

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kotlinplayground.view.PagerFragment

class ViewPagerAdapter(manager: FragmentManager, private var totalTabs: Int) : FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return PagerFragment()
    }

    override fun getCount(): Int {
        return totalTabs
    }
}