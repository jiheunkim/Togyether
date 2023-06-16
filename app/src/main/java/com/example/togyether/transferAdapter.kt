package com.example.togyether

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.DutchpayFragment.Static.memberListList
import com.example.togyether.databinding.RowTransferBinding

class transferAdapter (var items:ArrayList<transferData>, var groupNum:Int)
    : RecyclerView.Adapter<transferAdapter.ViewHolder>() {

    val colorList = arrayListOf<String>("#0E2954","#1F6E8C","#2E8A99","#84A7A1","#CEEDC7","#FFF6BD","#FFD4B2","#FCC8D1","#FFABAB","#D14D72","#9A1663","#CD104D"
        ,"#E14D2A","#FD841F","#FFCC29","#81B214","#206A5D","#1F6E8C","#0A4D68","#00337C")

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
        holder.binding.senderBtn.setBackgroundColor(Color.parseColor(colorList[(items[position].senderNum + 8 * groupNum) % 20]))
        holder.binding.sender.text=memberListList[groupNum][items[position].senderNum].name
        holder.binding.receiverBtn.setBackgroundColor(Color.parseColor(colorList[(items[position].receiverNum + 8 * groupNum) % 20]))
        holder.binding.receiver.text=memberListList[groupNum][items[position].receiverNum].name
        holder.binding.amount.text=items[position].amount.toString()
    }


}
