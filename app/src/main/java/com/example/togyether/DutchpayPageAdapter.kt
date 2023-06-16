package com.example.togyether

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class DutchpayPageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val fragmentList: MutableList<DutchpayGroupFrameFragment> = ArrayList()

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment{
        return fragmentList[position]
    }

    fun addFragment(groupNum:Int){
        fragmentList.add(DutchpayGroupFrameFragment(groupNum))
    }


}
