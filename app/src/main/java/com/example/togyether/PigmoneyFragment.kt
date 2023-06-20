package com.example.togyether

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.togyether.databinding.FragmentPigmoneyBinding

class PigmoneyFragment : Fragment() {

    lateinit var binding: FragmentPigmoneyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPigmoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subMenu()
    }

    private fun subMenu() {
        val subBtn = binding.pigmoneyMenu

        subBtn.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.first -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        val calenderFragment = CalenderFragment()
                        childFragmentManager.beginTransaction()
                            .replace(R.id.sub_container, calenderFragment).commit()
                    }
                    R.id.second -> {
                        val contentFragment = ContentFragment()
                        childFragmentManager.beginTransaction()
                            .replace(R.id.sub_container, contentFragment).commit()
                    }
                    R.id.third -> {
                        val budgetFragment = BudgetFragment()
                        childFragmentManager.beginTransaction()
                            .replace(R.id.sub_container, budgetFragment).commit()
                    }

                }
                true
            }
            selectedItemId = R.id.first
        }
    }
}