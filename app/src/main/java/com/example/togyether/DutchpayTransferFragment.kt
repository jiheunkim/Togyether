package com.example.togyether

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.togyether.databinding.FragmentAddSpendingBinding
import com.example.togyether.databinding.FragmentDutchpayTransferBinding

class DutchpayTransferFragment(var groupNum:Int) : Fragment(){
    lateinit var binding: FragmentDutchpayTransferBinding
    lateinit var tActivity: MainActivity
    lateinit var transferAdapter: transferAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        tActivity = context as MainActivity
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDutchpayTransferBinding.inflate(layoutInflater)
        val view = binding.root
        with(view) {
            binding.recyclerView3.layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView3.addItemDecoration(DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL))
            transferAdapter = transferAdapter(ArrayList(), groupNum)
            binding.recyclerView3.adapter = transferAdapter
            Log.i("init", "init")
        }



        binding.completionBtn.setOnClickListener {
            val fragment = DutchpayGroupFragment(groupNum)

            parentFragmentManager.beginTransaction()
                .replace(R.id.root_fragment, fragment)
                .commit()
        }
        init()
        return binding.root
    }

    // int to dp
    private fun changeDP(value: Int): Int {
        var displayMetrics = resources.displayMetrics
        var dp = Math.round(value * displayMetrics.density)
        return dp
    }

    private fun init(){
        transferAdapter.items.clear()
        for(i in DutchpayFragment.memberListList[groupNum]){
            for(j in DutchpayFragment.memberListList[groupNum]){
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
