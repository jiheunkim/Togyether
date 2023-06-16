package com.example.togyether

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.databinding.RowSpendingBinding

class spendingAdapter (var items:ArrayList<spendingData>)
    : RecyclerView.Adapter<spendingAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(data: spendingData, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: RowSpendingBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener{
                itemClickListener?.OnItemClick(items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RowSpendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount():Int{
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text=items[position].title
        holder.binding.time.text=items[position].time
        holder.binding.name.text=items[position].name
        holder.binding.amount.text=items[position].amount.toString()
        holder.binding.spendingMember.text=items[position].group[0].name.toString() + " 등 " + items[position].group.size + "명"
    }


}
