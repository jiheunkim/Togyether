package com.example.togyether

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.databinding.RowMemberBinding

class memberAdapter (var items:ArrayList<memberData>, var groupNum:Int)
    : RecyclerView.Adapter<memberAdapter.ViewHolder>() {

    val colorList = arrayListOf<String>("#0E2954","#1F6E8C","#2E8A99","#84A7A1","#CEEDC7","#FFF6BD","#FFD4B2","#FCC8D1","#FFABAB","#D14D72","#9A1663","#CD104D"
        ,"#E14D2A","#FD841F","#FFCC29","#81B214","#206A5D","#1F6E8C","#0A4D68","#00337C")


    interface OnItemClickListener{
        fun OnItemClick(data: memberData, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: RowMemberBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener{
                itemClickListener?.OnItemClick(items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RowMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount():Int{
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.binding.btn.setBackgroundColor(Color.parseColor(colorList[(position + 6 * groupNum) % 20]))


        holder.binding.name.text=items[position].name
    }


}
