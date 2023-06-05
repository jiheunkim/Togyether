package com.example.togyether

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.databinding.RowTransferBinding

class transferAdapter (var items:ArrayList<transferData>)
    : RecyclerView.Adapter<transferAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(data: transferData, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: RowTransferBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener{
                itemClickListener?.OnItemClick(items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RowTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount():Int{
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.sender.text=items[position].sender
        holder.binding.receiver.text=items[position].receiver
        holder.binding.amount.text=items[position].amount.toString()
    }


}
