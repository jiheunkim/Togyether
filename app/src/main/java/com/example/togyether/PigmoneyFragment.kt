package com.example.togyether

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.togyether.databinding.FragmentPigmoneyBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PigmoneyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PigmoneyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentPigmoneyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PigmoneyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PigmoneyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}