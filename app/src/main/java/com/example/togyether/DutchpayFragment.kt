package com.example.togyether

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.togyether.databinding.FragmentDutchpayBinding
import com.google.android.material.tabs.TabLayoutMediator

class DutchpayFragment : Fragment() {
    lateinit var binding: FragmentDutchpayBinding
    lateinit var dActivity: MainActivity
    val fragmentList = ArrayList<Fragment>()
    lateinit var adapter: DutchpayPageAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDutchpayBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        with(view) {
            adapter = DutchpayPageAdapter(dActivity)
            binding.viewPager.adapter = adapter
            TabLayoutMediator(binding.tabLayout, binding.viewPager){
                    tab , pos ->
                tab.text = "GROUP${pos + 1}"
            }.attach()
        }

        // arguments가 null이면 블록 내부로 진입 X
        arguments?.let {
            val groupName = it.getString("group_name")!!
            adapter.addFragment(groupName)
            val groupMembers = ArrayList<String>()
            for(i in 1 .. it.size()){
                val key = "member${i}"
                it.getString(key)?.let { member -> groupMembers.add(member) }
                Log.d("member", groupMembers[i-1])
            }
            adapter.notifyDataSetChanged()
        }

        binding.addBtn.setOnClickListener(){
            // DutchpayFragment -> AddGroupFragment
            parentFragmentManager.beginTransaction()
                .hide(this)
                .add(R.id.root_fragment, AddGroupFragment())
                .addToBackStack(null)
                .commit()


            adapter.addFragment("1111")
            adapter.notifyDataSetChanged()
        }
        return view
    }

}
