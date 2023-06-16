package com.example.togyether

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class CategoryAdapter2(private val context: Context, private val categories: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.row_spinner_category, parent, false)

        val categoryImage = view.findViewById<ImageView>(R.id.sub_menu_spinner_item_image)
        val categoryText = view.findViewById<TextView>(R.id.sub_menu_spinner_item)

        val category = categories[position]
        val drawableResId = getDrawableResId(category) // 벡터 이미지 리소스 아이디 가져오기
        val drawable = ContextCompat.getDrawable(context, drawableResId)
        categoryImage.setImageDrawable(drawable)
        categoryText.text = category

        return view
    }

    private fun getDrawableResId(category: String): Int {
        return when (category) {
            "급여" -> R.drawable.category_money
            "용돈" -> R.drawable.category_money
            "금융수입" -> R.drawable.category_money
            "기타수입" -> R.drawable.category_money
            else -> R.drawable.category_money
        }
    }
}