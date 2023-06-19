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

class CategoryFragment : Fragment() {

    private lateinit var valueEventListener: ValueEventListener
    private lateinit var database: DatabaseReference
    private lateinit var categoryList: MutableList<CategoryItem>
    private lateinit var category1Adapter: Category1Adapter
    private lateinit var pieChart: PieChart
    private lateinit var pieChartColors: List<Int> // 색상 배열 선언
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 카테고리 목록 초기화
        categoryList = mutableListOf()
        category1Adapter = Category1Adapter(categoryList)

        binding.recyclerView.adapter = category1Adapter

        // RecyclerView 설정
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = category1Adapter
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

        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance().reference

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d("FirebaseData", dataSnapshot.toString()) // 데이터 확인용 로그

                // 기존 데이터 초기화
                categoryList.clear()

                // 카테고리별 가격 합산
                val categoryMap = mutableMapOf<String, Int>()
                for (snapshot in dataSnapshot.children) {
                    val category = snapshot.child("category").value.toString()
                    val priceValue = snapshot.child("price").value
                    if (priceValue != null) {
                        val price = priceValue.toString().toInt()
                        val existingPrice = categoryMap.getOrDefault(category, 0)
                        categoryMap[category] = existingPrice + price
                    }
                }

                // 합산된 가격으로 CategoryItem 생성
                for ((category, price) in categoryMap) {
                    val formattedPrice = DecimalFormat("#,###").format(price)
                    val item = CategoryItem(category, formattedPrice)
                    categoryList.add(item)
                }

                // RecyclerView 갱신
                category1Adapter.notifyDataSetChanged()

                // 차트에 데이터 추가
                addChartEntries()


                // Firebase 데이터베이스에서 데이터 읽기
                database.addListenerForSingleValueEvent(valueEventListener)
            }

            private fun addChartEntries() {
                val entries = mutableListOf<PieEntry>()
                val totalAmount = categoryList.sumOf { it.amount.replace(",", "").toDoubleOrNull() ?: 0.0 }

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

            // 랜덤 색상 반환 함수
            private fun getRandomColor(index: Int): Int {
                return pieChartColors[index % pieChartColors.size]
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error handling
            }
        }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 색상 배열 초기화
        pieChartColors = resources.getIntArray(R.array.chartColors).toList()

    }
}
