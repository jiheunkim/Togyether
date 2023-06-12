package com.example.togyether

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.Model.PriceModel

class PriceAdapter(private val priceList: MutableList<PriceModel>) : RecyclerView.Adapter<PriceAdapter.PriceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_add, parent, false)
        return PriceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PriceViewHolder, position: Int) {
        val priceModel = priceList[position]
        holder.bind(priceModel)
    }

    override fun getItemCount(): Int {
        return priceList.size
    }

    inner class PriceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleContent)
        private val categoryTextView: TextView = itemView.findViewById(R.id.catagoryContent)
        private val categoryImageView: ImageView = itemView.findViewById(R.id.imageView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceContent)

        fun bind(priceModel: PriceModel) {
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
            else -> R.drawable.category_food
        }
    }
}