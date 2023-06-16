package com.example.togyether

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.Model.PriceModel

class ContentAdapter(private val priceList: MutableList<PriceModel>) : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_add, parent, false)
        return ContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val currentItem = priceList[position]
        val previousItem = if (position > 0) priceList[position - 1] else null

        holder.bind(currentItem)

        if (previousItem != null && previousItem.date == currentItem.date) {
            holder.dateTextView.visibility = View.GONE
        } else {
            holder.dateTextView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return priceList.size
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleContent)
        private val categoryTextView: TextView = itemView.findViewById(R.id.catagoryContent)
        private val categoryImageView: ImageView = itemView.findViewById(R.id.imageView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceContent)
        val dateTextView: TextView = itemView.findViewById(R.id.dateContent)

        fun bind(priceModel: PriceModel) {
            dateTextView.text = priceModel.date
            titleTextView.text = priceModel.title
            categoryTextView.text = priceModel.category
            priceTextView.text = priceModel.price

            val drawableResId = getDrawableResId(priceModel.category)
            categoryImageView.setImageResource(drawableResId)
        }
    }

    private fun getDrawableResId(category: String): Int {
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
            "급여" -> R.drawable.category_money
            "용돈" -> R.drawable.category_money
            "금융수입" -> R.drawable.category_money
            "기타수입" -> R.drawable.category_money
            else -> R.drawable.category_food
        }
    }
}
