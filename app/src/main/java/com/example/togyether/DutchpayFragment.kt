package com.example.togyether

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import com.example.togyether.databinding.FragmentDutchpayBinding
import com.google.android.material.tabs.TabLayoutMediator

class DutchpayFragment : Fragment() {
    lateinit var binding: FragmentDutchpayBinding
    lateinit var dActivity: MainActivity
    lateinit var adapter: DutchpayPageAdapter

    companion object Static{
        var groupSize = 0
        val groupMemberList = ArrayList<ArrayList<String>>()
        val groupNameList = ArrayList<String>()
    }

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
                tab.text = groupNameList[pos]
            }.attach()
        }

        // arguments가 null이면 블록 내부로 진입 X
        Log.d("size", arguments?.size().toString())
//        if(arguments == null){
//            Toast.makeText(requireContext(), "arguments null", Toast.LENGTH_SHORT).show()
//        }
        arguments?.let {
            // 새로 추가할 그룹 정보 수신
            val groupName = it.getString("group_name")!!
//            Toast.makeText(requireContext(), groupName, Toast.LENGTH_SHORT).show()
            val groupMembers = ArrayList<String>()
            for(i in 1 until it.size()){
                val key = "member${i}"
                it.getString(key)?.let { member -> groupMembers.add(member) }
            }
//            for(i in groupMembers){
//                Log.d("check_member$i", i)
//            }

            // 기존 그룹 + 신규 그룹 Adapter에 추가
            groupSize++
            groupNameList.add(groupName)
            groupMemberList.add(groupMembers)
            for(i in 0 until groupSize){
                adapter.addFragment(groupNameList[i], groupMemberList[i])
            }

            adapter.notifyDataSetChanged()
        }

        binding.addBtn.setOnClickListener(){
            // DutchpayFragment -> AddGroupFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.root_fragment, AddGroupFragment())
                .commit()
        }
        return view
    }

}
