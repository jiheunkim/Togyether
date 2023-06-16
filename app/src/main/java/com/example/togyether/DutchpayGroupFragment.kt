package com.example.togyether

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.togyether.DutchpayFragment.Static.memberListList
import com.example.togyether.DutchpayFragment.Static.spendingListList
import com.example.togyether.databinding.FragmentDutchpayGroupBinding

class DutchpayGroupFragment(var groupNum:Int) : Fragment() {
    lateinit var binding: FragmentDutchpayGroupBinding
    lateinit var memberAdapter: memberAdapter
    lateinit var spendingAdapter: spendingAdapter
    lateinit var transferAdapter: transferAdapter

    var transferList = ArrayList<transferData>();

    companion object Static{
        var spendCnt = 0
        //var spendingList = ArrayList<spendingData>();


        var spendingTitleList = ArrayList<String>()
        var timeList = ArrayList<String>()
        var amountList = ArrayList<Int>()
        val spendingNameList = ArrayList<String>()
        val spendingMemberList = ArrayList<ArrayList<memberData>>()
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

            val spendingMembers = ArrayList<memberData>()
            for(i in 1 .. memberListList[groupNum].size){
                val key = "member${i}"
                it.getInt(key)?.let {
                        memberNum -> if(i==memberNum){ spendingMembers.add(memberListList[groupNum][memberNum-1])}
                }

            }

            // 기존 그룹 + 신규 그룹 Adapter에 추가
            spendCnt++
            spendingListList[groupNum].add(spendingData(spendingTitle, spendingTime, spendingAmount, spendingName, spendingMembers))
            spendingAdapter.items = spendingListList[groupNum]
            spendingAdapter.notifyDataSetChanged()

        }

        binding.addSpendingBtn.setOnClickListener(){
            // DutchpayFragment -> AddGroupFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.root_fragment, AddSpendingFragment(groupNum))
                .commit()
        }


        init()
        return view
    }

    private fun init(){
        for(i in memberListList[groupNum]){
            memberAdapter.items.add(i)
        }
        memberAdapter.notifyDataSetChanged()

        spendingAdapter.items = spendingListList[groupNum]
        spendingAdapter.notifyDataSetChanged()
    }
}

