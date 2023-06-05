package com.example.togyether

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.togyether.databinding.FragmentDutchpayBinding
import com.example.togyether.databinding.FragmentDutchpayFrameBinding
import com.google.android.material.tabs.TabLayoutMediator

class DutchpayFrameFragment : Fragment() {
    lateinit var binding: FragmentDutchpayFrameBinding
    lateinit var dActivity: MainActivity

    var dutchpayFragment = DutchpayFragment()
    var addGroupFragment = AddGroupFragment()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        dActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDutchpayFrameBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        with(view) {
            childFragmentManager.beginTransaction().apply {
                add(R.id.root_fragment, dutchpayFragment, "dutch")
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack(null)
                commit()
            }
        }
        return view
    }

}
