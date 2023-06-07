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

class AddSpendingFragment : Fragment() {
    lateinit var binding: FragmentDutchpayGroupBinding
    lateinit var memberAdapter: memberAdapter
    lateinit var spendingAdapter: spendingAdapter
    lateinit var transferAdapter: transferAdapter
    var memberList = ArrayList<memberData>();
    var spendingList = ArrayList<spendingData>();
    var transferList = ArrayList<transferData>();

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

        binding.addSpendingBtn.setOnClickListener(){

        }

        return view
    }
}
