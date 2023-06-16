package com.example.togyether

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.Model.PriceModel
import com.example.togyether.databinding.FragmentContentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ContentFragment : Fragment() {
    lateinit var selectedDate: LocalDate
    lateinit var setMonth: String
    lateinit var getMonth: String

    lateinit var binding: FragmentContentBinding
    lateinit var myUid: String

    // 가계부 내역을 저장할 리스트
    private val priceList: MutableList<PriceModel> = mutableListOf()
    // PriceAdapter 인스턴스
    private val contentAdapter: ContentAdapter = ContentAdapter(priceList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usernameFromFirebase() // 사용자 이름 정보 가져오기
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContentBinding.inflate(inflater, container, false)
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

        // ValueEventListener 생성
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 데이터베이스의 값이 변경되었을 때 실행되는 로직을 여기에 작성
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
                                val sum = users.child("calendar").child("sum").value.toString()
                                val numberFormat = NumberFormat.getInstance(Locale.getDefault())

                                binding.spentMoney.text = numberFormat.format(spent.toInt())
                                binding.incomeMoney.text = numberFormat.format(income.toInt())
                                binding.planMoney.text = numberFormat.format(budget.toInt())
                                binding.sumEvery.text = numberFormat.format(sum.toInt())
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Error handling
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
            }
        }

        // ValueEventListener를 addValueEventListener로 등록하여 데이터의 변화를 감지합니다.
        db.addValueEventListener(valueEventListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedDate = LocalDate.now() // 현재 날짜
        setMonth = selectedDate.month.toString()
        setMonthView() // 월 이동

        binding.lastMonth.setOnClickListener {
            // 이전 달 버튼 이벤트
            selectedDate = selectedDate.minusMonths(1)
            setMonth = selectedDate.month.toString()
            setMonthView()
        }

        binding.nextMonth.setOnClickListener {
            // 다음 달 버튼 이벤트
            selectedDate = selectedDate.plusMonths(1)
            setMonth = selectedDate.month.toString()
            setMonthView()
        }
    }

    private fun setMonthView() {
        binding.nowMonth.text = monthYearFromDate(selectedDate)

        val year = "2023"
        val month = selectedDate.monthValue.toString()
        updateRecyclerViewData(year, month+"m") {
            // 데이터 변경을 알림
            contentAdapter.notifyDataSetChanged()
        }
        val filteredList = filterAndSortDataByMonth(month)

        priceList.clear()
        priceList.addAll(filteredList)
        priceList.sortByDescending { priceModel ->
            priceModel.date
        }
        contentAdapter.notifyDataSetChanged()

        val adapter = contentAdapter

        calendarFromFirebase() // 월별 정보 가져오기

        binding.recyclerView.adapter = adapter
    }


    private fun updateRecyclerViewData(year: String, month: String, onComplete: (Int) -> Unit) {
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!

        val db = Firebase.database.getReference("togyether")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (users in dataSnapshot.children) {
                    val uid = users.child("uid").value.toString()

                    if (myUid == uid) {
                        val userDB = users.child("calendar")
                            .child(year).child(month)
                        val monthInfo = month[0].toString().toInt()
                        val daysInMonth = YearMonth.of(year.toInt(), monthInfo).lengthOfMonth()

                        // RecyclerView에 등록된 가계부 내역을 불러옴
                        priceList.clear()
                        for (i in daysInMonth downTo 1) {
                            val day = userDB.child(i.toString() + "d")
                            val num = day.child("num").value.toString()

                            for (j in (num.toInt() - 1) downTo 0) {
                                val priceEntry = day.child(j.toString())
                                val priceCategory = priceEntry.child("category").value.toString()
                                val priceDate = priceEntry.child("date").value.toString()
                                val priceValue = priceEntry.child("price").value.toString()
                                val priceTitle = priceEntry.child("title").value.toString()
                                val priceModel = PriceModel(priceDate, priceTitle, priceCategory, priceValue)
                                priceList.add(priceModel)
                            }
                        }

                        // onComplete 콜백 함수 호출하여 데이터 크기 전달
                        onComplete(priceList.size)

                        // RecyclerView에 가계부 내역 업데이트
                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = ContentAdapter(priceList)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error handling
            }
        })
    }


    private fun filterAndSortDataByMonth(month: String): MutableList<PriceModel> {
        val filteredList: MutableList<PriceModel> = mutableListOf()

        for (priceModel in priceList) {
            val modelMonth = priceModel.date.split(" ")[1].replace("월", "")
            if (modelMonth == month) {
                filteredList.add(priceModel)
            }
        }

        filteredList.sortByDescending { priceModel ->
            priceModel.date
        }

        return filteredList
    }

    private fun monthYearFromDate(date: LocalDate): String {
        var formatter = DateTimeFormatter.ofPattern("MM월 yyyy")
        return date.format(formatter)
    }
}