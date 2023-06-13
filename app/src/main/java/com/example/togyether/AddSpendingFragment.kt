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
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.togyether.databinding.FragmentAddGroupBinding
import com.example.togyether.databinding.FragmentAddSpendingBinding
import com.example.togyether.databinding.FragmentDutchpayGroupBinding

class AddSpendingFragment(var nameList:ArrayList<String>) : Fragment() {
    lateinit var binding: FragmentAddSpendingBinding
    lateinit var sActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sActivity = context as MainActivity
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddSpendingBinding.inflate(layoutInflater)
        var groupMemberNum = 0
        var count = 0

        for(i:Int in 0..nameList.size - 1){

            val checkBox = CheckBox(requireContext())
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                changeDP(47)
            )
            layoutParams.setMargins(0, changeDP(5), 0 ,0)
            checkBox.setBackgroundResource(R.drawable.edittext)
            checkBox.setEms(10)
            checkBox.text = nameList[i]
            checkBox.setPadding(changeDP(15), 0, 0, 0)
            checkBox.layoutParams = layoutParams
            checkBox.id = i
            binding.spendingMember.addView(checkBox)
        }

        val adapter = ArrayAdapter(sActivity, R.layout.row_spinner, nameList)
        Thread(Runnable{
            sActivity?.runOnUiThread {
                binding.spendingName.adapter = adapter
                binding.spendingName.setSelection(0)
            }
        }).start()

        
        binding.completionBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("spendingTitle", binding.spendingTitle.text.toString())
            bundle.putString("spendingTime", binding.spendingTime.text.toString())
            bundle.putInt("spendingAmount", binding.spendingAmount.text.toString().toInt())
            bundle.putString("spendingName", binding.spendingName.selectedItem.toString())
            for(i:Int in 0..nameList.size - 1){
                if(view?.findViewById<CheckBox>(i)!!.isChecked()){
                    bundle.putString("member$i", view?.findViewById<CheckBox>(i)!!.text.toString())
                }
            }
            val fragment = DutchpayGroupFragment(nameList)
            fragment.arguments = bundle
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.root_fragment, fragment)
                .commit()
        }
        return binding.root
    }

    // int to dp
    private fun changeDP(value: Int): Int {
        var displayMetrics = resources.displayMetrics
        var dp = Math.round(value * displayMetrics.density)
        return dp
    }
}
