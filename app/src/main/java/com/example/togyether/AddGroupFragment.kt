// AddGroupFragment.kt
package com.example.togyether

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.togyether.databinding.FragmentAddGroupBinding

class AddGroupFragment : Fragment() {
    lateinit var binding: FragmentAddGroupBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddGroupBinding.inflate(layoutInflater)
        var groupMemberNum = 0
        var count = 0

        // add the number of group member
        binding.addMemberBtn.setOnClickListener {
//            Toast.makeText(requireContext(), "test", Toast.LENGTH_SHORT).show()
            binding.groupMemberName.removeAllViews()

            groupMemberNum = binding.groupMemberNum.text.toString().toInt()
            count = 0

            for(i:Int in 0..groupMemberNum - 1){
                val editText = EditText(requireContext())
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    changeDP(47)
                )
                layoutParams.setMargins(0, changeDP(5), 0 ,0)
                editText.setBackgroundResource(R.drawable.edittext)
                editText.setEms(10)
                editText.hint = "그룹원" + (++count).toString()
                editText.setPadding(changeDP(15), 0, 0, 0)
                editText.layoutParams = layoutParams
                editText.id = i+1
                binding.groupMemberName.addView(editText)
            }
        }

        // 그룹명, 그룹원 정보 번들에 담아서 DutchpayFragment에 송신
        binding.completionBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("group_name", binding.groupName.text.toString())
            for(i in 1 .. groupMemberNum){
//                Log.d("member_test", view?.findViewById<EditText>(i)!!.text.toString())
                bundle.putString("member$i", view?.findViewById<EditText>(i)!!.text.toString())
            }
            val fragment = DutchpayFragment()
            fragment.arguments = bundle

            // AddGroupFragment -> DutchpayFragment with bundle
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
