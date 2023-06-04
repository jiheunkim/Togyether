package com.example.togyether

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        private val priceTextView: TextView = itemView.findViewById(R.id.priceContent)

        fun bind(priceModel: PriceModel) {
            titleTextView.text = priceModel.title
            categoryTextView.text = priceModel.category
            priceTextView.text = priceModel.price
        }
    }
}