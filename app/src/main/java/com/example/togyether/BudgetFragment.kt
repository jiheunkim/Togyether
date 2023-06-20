package com.example.togyether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.togyether.databinding.FragmentBudgetBinding
import android.app.AlertDialog
import android.graphics.Color
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.utils.ColorTemplate
import java.lang.Math.abs

class BudgetFragment : Fragment() {

    lateinit var binding: FragmentBudgetBinding

    private var budget: Int = 0 // 예산
    private var expenses: Int = 0 // 지출
    private var income: Int = 0 // 수입
    lateinit var selectedDate: LocalDate
    private lateinit var expenseChart: BarChart
    private lateinit var budgetChart: BarChart

    lateinit var myUid: String
    lateinit var setMonth: String
    lateinit var getMonth: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 그래프를 위한 BarChart 인스턴스 생성
        expenseChart = binding.expenseChart
        budgetChart = binding.budgetChart

        usernameFromFirebase()

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

        // 설정 버튼 클릭 이벤트 처리
        binding.settings.setOnClickListener {
            // 설정 화면으로 이동하는 코드 추가
            showBudgetSettingDialog()
            setMonthView()
        }
        updateExpenseRatio()
        updateBudgetRatio()
    }

    private fun setMonthView() {
        binding.nowMonth.text = monthYearFromDate(selectedDate)

        // 날짜 생성해서 리스트 담기
        val dayList = dayInMonthArray(selectedDate)

        calendarFromFirebase() // 월별 정보 가져오기

    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MM월 yyyy")
        return date.format(formatter)
    }

    private fun dayInMonthArray(date: LocalDate): ArrayList<LocalDate?> {
        // 날짜 생성
        val dayList = ArrayList<LocalDate?>()
        val yearMonth = YearMonth.from(date)

        // 해당 월 마지막 날짜 가져오기(28, 30, 31일)
        val lastDay = yearMonth.lengthOfMonth()

        // 해당 월 첫 번째 날짜 가져오기(예: 5월 1일)
        val firstDay = selectedDate.withDayOfMonth(1)

        // 첫 번째 날 요일 가져오기(월:1, 일:7)
        val dayOfWeek = firstDay.dayOfWeek.value

        for(i in 1..41) {
            if(i <= dayOfWeek || i > (lastDay + dayOfWeek)) {
                dayList.add(null)
            }else {
                dayList.add(LocalDate.of(selectedDate.year, selectedDate.monthValue, i - dayOfWeek))
            }
        }

        return dayList
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

                                // Update expense and budget variables
                                expenses = spent.toInt()
                                this@BudgetFragment.budget = budget.toInt()
                                this@BudgetFragment.income = income.toInt()
                                updateExpenseRatio()
                                updateBudgetRatio()
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

    private fun showBudgetSettingDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("예산 설정하기")

        // 다이얼로그에 입력 필드 추가
        val budgetEditText = EditText(requireContext())
        budgetEditText.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(budgetEditText)

        // 확인 버튼 클릭 시 예산 설정 처리
        builder.setPositiveButton("확인") { dialog, _ ->
            val budgetString = budgetEditText.text.toString()
            val budget = budgetString.toIntOrNull()
            if (budget != null) {
                setBudget(budget)
            }
            dialog.dismiss()
        }

        // 취소 버튼 클릭 시 다이얼로그 닫기
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        // 다이얼로그 표시
        builder.show()
    }

    // 예산 설정 처리
    private fun setBudget(budget: Int) {
        binding.planMoney.text = budget.toString()
        this.budget = budget
        updateExpenseRatio()

        val myUid = FirebaseAuth.getInstance().currentUser?.uid!!
        val db = Firebase.database.getReference("togyether")

        // 예산 값을 Firebase의 budget 노드에 업데이트
        db.child(myUid).child("calendar").child("2023").child(getMonth).child("budget").setValue(budget)
            .addOnSuccessListener {
                // 업데이트 성공 시 처리할 로직 추가
            }
            .addOnFailureListener {
                // 업데이트 실패 시 처리할 로직 추가
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    // 지출 비율 업데이트
    private fun updateExpenseRatio() {
        //calendarFromFirebase()
        binding.incomeMoney.text = income.toString()
        this.income = income

        // 그래프 데이터 생성
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, budget.toFloat()))
        entries.add(BarEntry(1f, abs(expenses).toFloat())) 

        // 그래프 데이터셋 설정
        val dataSet = BarDataSet(entries, "Expense")
        dataSet.colors = if (budget >= (income - expenses)) {
            // 예산이 수입-지출보다 크거나 같을 경우 파란색으로 설정
            listOf(Color.BLUE, Color.RED)
        } else {
            // 예산이 수입-지출보다 작을 경우 빨간색으로 설정
            listOf(Color.RED, Color.BLUE)
        }

        // 그래프 데이터셋 리스트 생성
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(dataSet)

        // 그래프 데이터 설정
        val data = BarData(dataSets)

        // 그래프 스타일 및 텍스트 설정
        val description = Description()
        description.text = ""
        expenseChart.description = description
        expenseChart.xAxis.labelCount = 2
        expenseChart.xAxis.setDrawGridLines(false)
        expenseChart.axisLeft.setDrawGridLines(false)
        expenseChart.axisRight.setDrawGridLines(false)
        expenseChart.axisRight.setDrawLabels(false)
        expenseChart.legend.isEnabled = false
        expenseChart.setDrawValueAboveBar(false)
        expenseChart.setPinchZoom(false)
        expenseChart.setDrawBarShadow(false)
        expenseChart.data = data
        expenseChart.axisLeft.axisMinimum = 0f // y축 최소값을 0으로 설정
        expenseChart.axisLeft.axisMaximum = 500000f //  y축 최대값을 500000으로 설정
        expenseChart.invalidate()

    }

    // 예산 비율 업데이트
    private fun updateBudgetRatio() {
        //calendarFromFirebase()
        binding.planMoney.text = budget.toString()

        // 그래프 데이터 생성
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, budget.toFloat()))
        entries.add(BarEntry(1f, expenses.toFloat()))

        // 그래프 데이터셋 설정
        val dataSet = BarDataSet(entries, "Budget")
        dataSet.colors = if (budget >= expenses) {
            // 예산이 지출보다 크거나 같을 경우 파란색으로 설정
            listOf(Color.BLUE, Color.RED)
        } else {
            // 예산이 지출보다 작을 경우 빨간색으로 설정
            listOf(Color.RED, Color.BLUE)
        }

        // 그래프 데이터셋 리스트 생성
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(dataSet)

        // 그래프 데이터 설정
        val data = BarData(dataSets)

        // 그래프 스타일 및 텍스트 설정
        val description = Description()
        description.text = ""
        budgetChart.description = description
        budgetChart.xAxis.labelCount = 2
        budgetChart.xAxis.setDrawGridLines(false)
        budgetChart.axisLeft.setDrawGridLines(false)
        budgetChart.axisRight.setDrawGridLines(false)
        budgetChart.axisRight.setDrawLabels(false)
        budgetChart.legend.isEnabled = false
        budgetChart.setDrawValueAboveBar(false)
        budgetChart.setPinchZoom(false)
        budgetChart.setDrawBarShadow(false)
        budgetChart.data = data
        budgetChart.axisLeft.axisMinimum = 0f //  y축 최소값을 0으로 설정
        budgetChart.axisLeft.axisMaximum = 500000f //y축 최대값을 500000으로 설정
        budgetChart.invalidate()

    }
}
