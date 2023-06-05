package com.example.togyether

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
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
            if(binding.groupMemberNum.text != null && binding.groupMemberNum.text.matches(Regex("""\d*"""))
                && !binding.groupMemberNum.text.matches(Regex(""))){
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
                    binding.groupMemberName.addView(editText)
                }
            }


        }

        // send data
        binding.completionBtn.setOnClickListener {
            // need to add codes which send data
            val bundle = Bundle()
            bundle.putString("group_name", binding.groupName.text.toString())
            for(i in 1 until groupMemberNum){
                bundle.putString("member$i", view?.findViewWithTag("멤버 + i"))
            }
//            val fragment = DutchpayFragment()
//            fragment.arguments = bundle

//            // tag가 dutch인 fragment의 arguments에 정보 담아 볼라 했는데 튕김
//            val parentFragment = parentFragmentManager.findFragmentByTag("dutch")!!
//            parentFragment.arguments = bundle


            parentFragmentManager.beginTransaction()
                .remove(this)
                .show(parentFragmentManager.findFragmentByTag("dutch")!!)
                .commit()
            parentFragmentManager.popBackStack()
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
