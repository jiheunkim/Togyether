package com.example.togyether

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.databinding.RowMemberBinding

class memberAdapter (var items:ArrayList<memberData>)
    : RecyclerView.Adapter<memberAdapter.ViewHolder>() {

    val colorList = arrayListOf<String>("#E71D36","#5A9367","#EFDC05","#9055A2","#4F86C6","#f199bc",
        "#C16200","#FDD692","#00b9f1","#548687","#8F2D56","#F16B6F","#00dffc","#cbe86b","#ee2560","#1a1a1a","#4f953b",
        "#f4f7f7","#353866","#1F6E8C")


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


        holder.binding.btn.setBackgroundColor(Color.parseColor(colorList[position % items.size]))


        holder.binding.name.text=items[position].name
    }


}
