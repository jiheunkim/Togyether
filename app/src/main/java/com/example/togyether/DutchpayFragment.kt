package com.example.togyether

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import com.example.togyether.databinding.FragmentDutchpayBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class DutchpayFragment : Fragment() {
    lateinit var binding: FragmentDutchpayBinding
    lateinit var dActivity: MainActivity
    lateinit var adapter: DutchpayPageAdapter

    companion object Static{
        var groupSize = 0
        val groupNameList = ArrayList<String>()
        val memberListList = ArrayList<ArrayList<memberData>>()
        val spendingListList = ArrayList<ArrayList<spendingData>>()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDutchpayBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        with(view) {
            adapter = DutchpayPageAdapter(dActivity)
            binding.viewPager.adapter = adapter
            TabLayoutMediator(binding.tabLayout, binding.viewPager){
                    tab , pos ->
                tab.text = groupNameList[pos]
            }.attach()
        }

        // arguments가 null이면 블록 내부로 진입 X
        Log.d("size", arguments?.size().toString())
//        if(arguments == null){
//            Toast.makeText(requireContext(), "arguments null", Toast.LENGTH_SHORT).show()
//        }

        // 파이어베이스에 있는 코드 읽어오기 (시간 차가 발생하여 제대로 기능 x)
//        if(arguments == null) {
//            val myUid = FirebaseAuth.getInstance().currentUser?.uid!!
//            val db = Firebase.database.getReference("togyether/$myUid/dutchpay")
//            db.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    for (group in dataSnapshot.children) {
//                        groupSize++
//                        groupNameList.add(group.key!!)
//                        Toast.makeText(requireContext(), groupNameList.size.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    println("Data read cancelled or failed: ${error.message}")
//                }
//            })
//            Log.d("TempTest", groupSize.toString())
//
//            for(groupName in groupNameList){
//                val cnt = 0
//                val ref = db.child(groupName).child("member")
//                ref.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        for (memberName in dataSnapshot.children) {
//                            Log.d("memberName", memberName.key!!)
//                            val data = ref.child(memberName.key!!)
//                            for(temp in memberName.children){
//                                println("Temp1Test:${temp.key}")
//                            }
//                            println("Temptest\n")
////                        val member = memberData(memberName.key!!,
////                            data.key.toString().toInt(),
//
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        println("Data read cancelled or failed: ${error.message}")
//                    }
//                })
//            }
//        }

        arguments?.let {
            // 새로 추가할 그룹 정보 수신
            val groupName = it.getString("group_name")!!
//            Toast.makeText(requireContext(), groupName, Toast.LENGTH_SHORT).show()
            val groupMembersNames = ArrayList<String>()
            for(i in 1 until it.size()){
                val key = "member${i}"
                it.getString(key)?.let { member -> groupMembersNames.add(member) }
            }
//            for(i in groupMembers){
//                Log.d("check_member$i", i)
//            }

            // 새로 추가한 그룹 정보 데이터베이스에 추가
            val myUid = FirebaseAuth.getInstance().currentUser?.uid!!
            val db = Firebase.database.getReference("togyether/$myUid/dutchpay")
            db.child(groupName).setValue("")
            for(member in groupMembersNames){
                val ref = db.child(groupName).child("member").child(member)
                ref.child("지출").setValue(0)
                ref.child("송금").setValue("")
            }

            // 기존 그룹 + 신규 그룹 Adapter에 추가
            groupSize++
            groupNameList.add(groupName)
            val groupMemberList = ArrayList<memberData>()

            for(i in 0 until groupMembersNames.size){
                val transferList=ArrayList<Long>()
                for(i in 0 until groupMembersNames.size){
                    transferList.add(0)
                }
                groupMemberList.add(memberData(groupMembersNames[i], i,0, transferList))
            }
            memberListList.add(groupMemberList)
            spendingListList.add(ArrayList())

            for(i in 0 until groupSize){
                adapter.addFragment(i)
            }

            adapter.notifyDataSetChanged()
        }

        binding.addBtn.setOnClickListener(){
            // DutchpayFragment -> AddGroupFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.root_fragment, AddGroupFragment())
                .commit()
        }
        return view
    }

}
