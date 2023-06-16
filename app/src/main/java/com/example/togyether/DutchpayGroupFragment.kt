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
import com.github.mikephil.charting.utils.Utils.init

class DutchpayGroupFragment(var groupNum:Int) : Fragment() {
    lateinit var binding: FragmentDutchpayGroupBinding
    lateinit var memberAdapter: memberAdapter
    lateinit var spendingAdapter: spendingAdapter
    lateinit var transferAdapter: transferAdapter

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
            spendingAdapter = spendingAdapter(ArrayList(), groupNum, resources.displayMetrics)
            transferAdapter = transferAdapter(ArrayList(), groupNum)

            binding.recyclerView1.adapter = memberAdapter
            binding.recyclerView2.adapter = spendingAdapter
            binding.recyclerView3.adapter = transferAdapter
            Log.i("init", "init")
        }

        arguments?.let {
            // 새로 추가할 그룹 정보 수신
            val spendingTitle = it.getString("spendingTitle")!!
            val spendingTime = it.getString("spendingTime")!!
            val spendingAmount = it.getLong("spendingAmount")!!
            val spendingNameNum = it.getInt("spendingName")!!
            val spendingName = memberListList[groupNum][spendingNameNum].name
            val spendingCnt = it.getInt("spendingCnt")
            val spendingMembers = ArrayList<memberData>()
            for(i in 1 .. memberListList[groupNum].size){
                val key = "member${i}"
                it.getInt(key)?.let { memberNum ->
                    if(i==memberNum){
                        spendingMembers.add(memberListList[groupNum][memberNum-1])
                        if(memberNum - 1 != spendingNameNum){
                            memberListList[groupNum][memberNum - 1].transfer[spendingNameNum] += spendingAmount/spendingCnt
                            Log.i("tranfer1", memberListList[groupNum][memberNum - 1].name + "->"
                                    + memberListList[groupNum][spendingNameNum].name
                                    + " " + (spendingAmount/spendingCnt).toString())
                        }
                    }
                }
            }

            for(i in memberListList[groupNum]){
                for(j in 0..memberListList[groupNum].size - 1){
                    Log.i("trnsfr", i.name + "->" + memberListList[groupNum][j].name + " " + i.transfer[j].toString())
                }

            }

            transferAdapter.items.clear()
            for(i in memberListList[groupNum]){
                for(j in memberListList[groupNum]){
                    if(i.transfer[j.num]>j.transfer[i.num]){
                        transferAdapter.items.add(transferData(i.num, j.num, i.transfer[j.num]-j.transfer[i.num]))
                        Log.i("transfer2", i.name + " " + j.name +" "+i.transfer[j.num].toString() + " " + j.transfer[i.num].toString())
                        Log.i("transfer2", i.name + "->" + j.name +" "+ (i.transfer[j.num]-j.transfer[i.num]).toString())
                    }
                }
            }
            transferAdapter.notifyDataSetChanged()

            // 기존 그룹 + 신규 그룹 Adapter에 추가
            spendingListList[groupNum].add(spendingData(spendingTitle, spendingTime, spendingAmount, spendingNameNum, spendingMembers))
            spendingAdapter.items = spendingListList[groupNum]
            spendingAdapter.notifyDataSetChanged()

        }

        binding.addSpendingBtn.setOnClickListener(){
            // DutchpayFragment -> AddGroupFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.root_fragment, AddSpendingFragment(groupNum))
                .commit()
        }

        binding.settlementBtn.setOnClickListener(){
            // DutchpayFragment -> DutchpayTransferFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.root_fragment, DutchpayTransferFragment(groupNum))
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

        transferAdapter.items.clear()
        for(i in memberListList[groupNum]){
            for(j in memberListList[groupNum]){
                if(i.transfer[j.num]>j.transfer[i.num]){
                    transferAdapter.items.add(transferData(i.num, j.num, i.transfer[j.num]-j.transfer[i.num]))
                    Log.i("transfer2", i.name + " " + j.name +" "+i.transfer[j.num].toString() + " " + j.transfer[i.num].toString())
                    Log.i("transfer2", i.name + "->" + j.name +" "+ (i.transfer[j.num]-j.transfer[i.num]).toString())
                }
            }
        }
        transferAdapter.notifyDataSetChanged()
    }
}

