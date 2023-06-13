package com.example.togyether

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.togyether.CategoryData
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.togyether.databinding.FragmentCategoryBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class CategoryFragment : Fragment() {
    lateinit var binding: FragmentCategoryBinding //
    private val transactions = mutableListOf<CategoryData>()//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCategoryBinding.inflate(layoutInflater) //

        val categories = arrayOf("음식", "쇼핑", "전자기기", "책", "교통비")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        binding.spinnerCategory.adapter = adapter

        binding.buttonAdd.setOnClickListener {
            addTransaction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    private fun addTransaction() {
        val category = binding.spinnerCategory.selectedItem.toString()
        val amountText = binding.editTextAmount.text.toString()
        val amount = amountText.toIntOrNull()

        if (category.isNotEmpty() && amount != null) {
            val categoryData = CategoryData(category, amount)
            transactions.add(categoryData)

            updateChart()
            updateTransactionList()
            clearInputFields()
        } else {
            Toast.makeText(requireContext(), "카테고리와 금액을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateChart() {
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        val categoryAmounts = mutableMapOf<String, Double>()
        for (transaction in transactions) {
            if (categoryAmounts.containsKey(transaction.category)) {
                categoryAmounts[transaction.category] = categoryAmounts[transaction.category]!! + transaction.amount
            } else {
                categoryAmounts[transaction.category] = transaction.amount.toDouble()
            }
        }

        val distinctCategories = categoryAmounts.keys.toList()
        for (i in distinctCategories.indices) {
            val category = distinctCategories[i]
            val amount = categoryAmounts[category] ?: 0.0
            entries.add(PieEntry(amount.toFloat(), category))

            // 각 카테고리에 원하는 색상을 설정
            val color = when (category) {
                "음식" -> Color.rgb(255, 0, 0) // 빨간색
                "쇼핑" -> Color.rgb(0, 255, 0) // 초록색
                "전자기기" -> Color.rgb(0, 0, 255) // 파란색
                "책" -> Color.rgb(255, 255, 0) // 노란색
                "교통비" -> Color.rgb(255, 0, 255) // 보라색
                else -> Color.rgb(128, 128, 128) // 기본 색상 (회색)
            }
            colors.add(color)
        }

        val dataSet = PieDataSet(entries, "카테고리")
        dataSet.colors = colors
        val pieData = PieData(dataSet)

        binding.pieChart.data = pieData
        binding.pieChart.invalidate()

    }

    private fun updateTransactionList() {
        val categoryAmounts = mutableMapOf<String, Double>()

        for (transaction in transactions) {
            if (categoryAmounts.containsKey(transaction.category)) {
                categoryAmounts[transaction.category] = categoryAmounts[transaction.category]!! + transaction.amount
            } else {
                categoryAmounts[transaction.category] = transaction.amount.toDouble()
            }
        }

        val transactionsToShow = categoryAmounts.entries.map { CategoryData(it.key, it.value.toInt()) }.takeLast(5)
        val categoryTextViews = arrayOf(
            binding.textCategory1,
            binding.textCategory2,
            binding.textCategory3,
            binding.textCategory4,
            binding.textCategory5
        )
        val amountTextViews = arrayOf(
            binding.textAmount1,
            binding.textAmount2,
            binding.textAmount3,
            binding.textAmount4,
            binding.textAmount5
        )

        for (i in transactionsToShow.indices) {
            categoryTextViews[i].text = transactionsToShow[i].category
            amountTextViews[i].text = transactionsToShow[i].amount.toString()
        }
    }


    private fun clearInputFields() {
        binding.editTextAmount.text.clear()
    }

}