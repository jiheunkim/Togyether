package com.example.togyether

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.databinding.FragmentCalenderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CalenderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var selectedDate: LocalDate
    lateinit var binding: FragmentCalenderBinding

    lateinit var myUid: String
    lateinit var setMonth: String
    lateinit var getMonth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        usernameFromFirebase() // 사용자 이름 정보 가져오기
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun usernameFromFirebase() {
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!

        val db = Firebase.database.getReference("togyether")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (users in dataSnapshot.children) {
                    val name = users.child("username").value.toString()
                    val uid = users.child("uid").value.toString()
                    Log.i("user", name.plus(uid))

                    if (myUid == uid) {
                        binding.myName.text = name
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error handling
            }
        })
    }

    private fun calendarFromFirebase() {
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!

        val db = Firebase.database.getReference("togyether")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (users in dataSnapshot.children) {
                    val uid = users.child("uid").value.toString()

                    if (myUid == uid) {
                        when (setMonth) {
                            "JANUARY" -> getMonth = "1m"
                            "FEBRUARY" -> getMonth = "2m"
                            "MARCH" -> getMonth = "3m"
                            "APRIL" -> getMonth = "4m"
                            "MAY" -> getMonth = "5m"
                            "JUNE" -> getMonth = "6m"
                            "JULY" -> getMonth = "7m"
                            "AUGUST" -> getMonth = "8m"
                            "SEPTEMBER" -> getMonth = "9m"
                            "OCTOBER" -> getMonth = "10m"
                            "NOVEMBER" -> getMonth = "11m"
                            "DECEMBER" -> getMonth = "12m"
                        }
                        val spent = users.child("calendar").child("2023").child(getMonth).child("expense").value.toString()
                        val income = users.child("calendar").child("2023").child(getMonth).child("income").value.toString()
                        val budget = users.child("calendar").child("2023").child(getMonth).child("budget").value.toString()

                        binding.spentMoney.text = spent
                        binding.incomeMoney.text = income
                        binding.planMoney.text = budget
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error handling
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedDate = LocalDate.now() // 현재 날짜
        setMonth = selectedDate.month.toString()
//        calendarFromFirebase() // 달력 월금액 정보 가져오기
        setMonthView() // 월 이동

        binding.lastMonth.setOnClickListener {
            // 이전 달 버튼 이벤트
            selectedDate = selectedDate.minusMonths(1)
            setMonth = selectedDate.month.toString()
//            calendarFromFirebase() // 달력 월금액 정보 가져오기
            setMonthView()
        }

        binding.nextMonth.setOnClickListener {
            // 다음 달 버튼 이벤트
            selectedDate = selectedDate.plusMonths(1)
            setMonth = selectedDate.month.toString()
//            calendarFromFirebase() // 달력 월금액 정보 가져오기
            setMonthView()
        }
    }

    private fun setMonthView() {
        binding.nowMonth.text = monthYearFromDate(selectedDate)

        // 날짜 생성해서 리스트 담기
        val dayList = dayInMonthArray(selectedDate)
        val adapter = CalendarAdapter(dayList)
        var manager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 7)

        calendarFromFirebase() // 월별 정보 가져오기

        binding.calendar.layoutManager = manager
        binding.calendar.adapter = adapter
    }

    private fun monthYearFromDate(date: LocalDate): String {
        var formatter = DateTimeFormatter.ofPattern("MM월 yyyy")

        return date.format(formatter)
    }

    private fun dayInMonthArray(date: LocalDate): ArrayList<LocalDate?> {
        // 날짜 생성
        var dayList = ArrayList<LocalDate?>()
        var yearMonth = YearMonth.from(date)

        // 해당 월 마지막 날짜 가져오기(28, 30, 31일)
        var lastDay = yearMonth.lengthOfMonth()

        // 해당 월 첫 번째 날짜 가져오기(예: 5월 1일)
        var firstDay = selectedDate.withDayOfMonth(1)

        // 첫 번째 날 요일 가져오기(월:1, 일:7)
        var dayOfWeek = firstDay.dayOfWeek.value

        for(i in 1..41) {
            if(i <= dayOfWeek || i > (lastDay + dayOfWeek)) {
                dayList.add(null)
            }else {
                dayList.add(LocalDate.of(selectedDate.year, selectedDate.monthValue, i - dayOfWeek))
            }
        }

        return dayList
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CalenderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalenderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}