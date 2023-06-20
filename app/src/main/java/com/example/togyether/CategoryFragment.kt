package com.example.togyether

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.DecimalFormat
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.togyether.databinding.FragmentCategoryBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.ArrayList

class CategoryFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private var categoryList: MutableList<CategoryItem> = mutableListOf()
    private lateinit var category1Adapter: Category1Adapter
    private lateinit var pieChart: PieChart
    private lateinit var pieChartColors: List<Int> // 색상 배열 선언
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    lateinit var myUid: String
    lateinit var selectedDate: LocalDate
    lateinit var setMonth: String
    lateinit var getMonth: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
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

        // 카테고리 목록 초기화
        categoryList = mutableListOf()
        category1Adapter = Category1Adapter(categoryList)

        binding.recyclerView.adapter = category1Adapter

        // RecyclerView 설정
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = category1Adapter

            // Firebase에서 데이터 가져오기
            //fetchDataFromFirebase()

            category1Adapter.notifyDataSetChanged()
        }

        // PieChart 설정
        pieChart = view.findViewById(R.id.pie_chart)
        pieChart.apply {
            setUsePercentValues(true)
            setEntryLabelTextSize(12f)
            setEntryLabelColor(Color.BLACK)
            description.isEnabled = false
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
        }
        // 색상 배열 초기화
        pieChartColors = resources.getIntArray(R.array.chartColors).toList()

        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance().reference
    }

    private fun setMonthView() {
        binding.nowMonth.text = monthYearFromDate(selectedDate)

        // 날짜 생성해서 리스트 담기
        val dayList = dayInMonthArray(selectedDate)

        fetchDataFromFirebase() // 월별 정보 가져오기
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

    private fun fetchDataFromFirebase() {
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!
        //selectedDate = LocalDate.now() // 현재 날짜
        setMonth = selectedDate.monthValue.toString()

        val db = Firebase.database.getReference("togyether")
            .child(myUid)
            .child("calendar")
            .child("2023")
            .child(setMonth + "m")

        // 기존 데이터 초기화
        categoryList.clear()

        // 카테고리별 가격 합산
        val categoryMap = mutableMapOf(
            "식당" to 0,
            "카페" to 0,
            "생필품" to 0,
            "문화" to 0,
            "주거" to 0,
            "금융" to 0,
            "쇼핑" to 0,
            "교통" to 0,
            "여행" to 0,
            "급여" to 0,
            "용돈" to 0,
            "금융수입" to 0,
            "기타수입" to 0
        )

        // Firebase에서 데이터 가져오기
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in 1 until 31) {
                    val dayDataSnapshot = snapshot.child(i.toString() + "d")
                    if (dayDataSnapshot.exists()) {
                        for (itemSnapshot in dayDataSnapshot.children) {
                            val itemCategory =
                                itemSnapshot.child("category").getValue(String::class.java)
                            val itemPrice = itemSnapshot.child("price").getValue(String::class.java)
                            val priceInt = if (itemPrice?.startsWith("-") == true) { // 가격 숫자 정보만 넣기
                                val priceDigits = itemPrice?.replace(Regex("[^0-9]"), "")
                                -priceDigits?.toInt()!!
                            } else {
                                null
                            }

                            if (priceInt != null) {
                                val category = itemCategory.toString()
                                categoryMap[category] = categoryMap[category]!! + priceInt
                            }
                        }
                    }
                }

                // 합산된 가격으로 CategoryItem 생성
                for ((category, price) in categoryMap) {
                    println("$category: $price")
                    val formattedPrice = DecimalFormat("#,###").format(price)
                    val item = CategoryItem(category, formattedPrice)
                    categoryList.add(item)
                }

                // RecyclerView 갱신
                category1Adapter.notifyDataSetChanged()

                // 차트에 데이터 추가
                addChartEntries()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addChartEntries() {
        val entries = mutableListOf<PieEntry>()
        val totalAmount = categoryList.sumOf { it.amount.replace(",", "").toDoubleOrNull() ?: 0.0 }

        // 기존 차트 데이터 초기화
        pieChart.data = null

        for ((index, categoryItem) in categoryList.withIndex()) {
            val amount = categoryItem.amount.replace(",", "").toFloatOrNull()
            val percentage = if (totalAmount != 0.0&& amount != null) (amount / totalAmount).toFloat() else 0.0f
            val entry = PieEntry(percentage, categoryItem.category)
            entries.add(entry)
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.apply {
            setDrawIcons(false)
            sliceSpace = 2f
            selectionShift = 10f
            colors = pieChartColors // 배열로 변환하여 할당
        }

        val data = PieData(dataSet)
        data.apply {
            setValueTextSize(12f)
            setValueTextColor(Color.BLACK)
        }

        pieChart.data = data
        pieChart.invalidate()
    }

    // RecyclerView Adapter
    inner class Category1Adapter(private val categoryList: List<CategoryItem>) :
        RecyclerView.Adapter<Category1Adapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val categoryImageView: ImageView = itemView.findViewById(R.id.category_list)
            val categoryNameTextView: TextView = itemView.findViewById(R.id.category_name)
            val categoryAmountTextView: TextView = itemView.findViewById(R.id.category_amount)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_category, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val categoryItem = categoryList[position]

            // 카테고리 정보 설정
            holder.categoryImageView.setImageResource(
                getDrawableResId(
                    holder.itemView.context,
                    categoryItem.category
                )
            )
            holder.categoryNameTextView.text = categoryItem.category
            holder.categoryAmountTextView.text = categoryItem.amount ?: ""
            // 지출 항목만 표시
            if (categoryItem.category == "급여" || categoryItem.category == "용돈" ||
                categoryItem.category == "금융수입" || categoryItem.category == "기타수입") {
                holder.itemView.visibility = View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            } else {
                holder.itemView.visibility = View.VISIBLE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        override fun getItemCount(): Int {
            return categoryList.size
        }

        //data class CategoryItem(val category: String, val amount: String)

        private fun getDrawableResId(context: Context, category: String): Int {
            return when (category) {
                "식당" -> R.drawable.category_food
                "카페" -> R.drawable.category_cafe
                "생필품" -> R.drawable.category_daily
                "문화" -> R.drawable.category_activities
                "주거" -> R.drawable.category_home
                "금융" -> R.drawable.category_money
                "쇼핑" -> R.drawable.category_shopping
                "교통" -> R.drawable.category_traffic
                "여행" -> R.drawable.category_travel
                else -> R.drawable.category_food
            }
        }
    }

    // 카테고리 아이템 데이터 클래스
    data class CategoryItem(val category: String, val amount: String)
}
