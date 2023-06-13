package com.example.togyether

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.togyether.databinding.FragmentDutchpayGroupBinding

class DutchpayGroupFragment(var nameList:ArrayList<String>) : Fragment() {
    lateinit var binding: FragmentDutchpayGroupBinding
    lateinit var memberAdapter: memberAdapter
    lateinit var spendingAdapter: spendingAdapter
    lateinit var transferAdapter: transferAdapter

    var spendingList = ArrayList<spendingData>();
    var transferList = ArrayList<transferData>();

    companion object Static{
        var spendCnt = 0
        var spendingTitleList = ArrayList<String>()
        var timeList = ArrayList<String>()
        var amountList = ArrayList<Int>()
        val spendingNameList = ArrayList<String>()
        val memberList = ArrayList<ArrayList<String>>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDutchpayGroupBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        with(view) {
            binding.recyclerView1.layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerView2.layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView3.layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)

            binding.recyclerView1.addItemDecoration(DividerItemDecoration(getContext(),LinearLayoutManager.HORIZONTAL))
            binding.recyclerView2.addItemDecoration(DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL))
            binding.recyclerView3.addItemDecoration(DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL))

            memberAdapter = memberAdapter(ArrayList())
            spendingAdapter = spendingAdapter(ArrayList())
            transferAdapter = transferAdapter(ArrayList())

            binding.recyclerView1.adapter = memberAdapter
            binding.recyclerView2.adapter = spendingAdapter
            binding.recyclerView3.adapter = transferAdapter
            Log.i("init", "init")
        }

        arguments?.let {
            // 새로 추가할 그룹 정보 수신
            val spendingTitle = it.getString("spendingTitle")!!
            val spendingTime = it.getString("spendingTime")!!
            val spendingAmount = it.getInt("spendingAmount")!!
            val spendingName = it.getString("spendingName")!!

            val spendingMembers = ArrayList<String>()
            for(i in 0 until nameList.size-1){
                val key = "member${i}"
                it.getString(key)?.let { member -> spendingMembers.add(member) }
            }

            // 기존 그룹 + 신규 그룹 Adapter에 추가
            spendCnt++
            spendingTitleList.add(spendingTitle)
            timeList.add(spendingTime)
            amountList.add(spendingAmount)
            spendingNameList.add(spendingName)
            memberList.add(spendingMembers)

            for(i in 0 until DutchpayFragment.groupSize){
                spendingAdapter.items.add(spendingData(spendingTitleList[i], timeList[i],
                    spendingNameList[i], amountList[i], memberList[i]))
            }

            spendingAdapter.notifyDataSetChanged()
        }

        binding.addSpendingBtn.setOnClickListener(){
            // DutchpayFragment -> AddGroupFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.root_fragment, AddSpendingFragment(nameList))
                .commit()
        }


        init()
        return view
    }

    private fun init(){
        for(i in nameList){
            memberAdapter.items.add(memberData(i,0,0))
        }
        memberAdapter.notifyDataSetChanged()
    }
}

