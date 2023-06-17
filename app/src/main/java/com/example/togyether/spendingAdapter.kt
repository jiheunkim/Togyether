package com.example.togyether
 
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.DutchpayFragment.Static.memberListList
import com.example.togyether.databinding.RowSpendingBinding

class spendingAdapter (var items:ArrayList<spendingData>, var groupNum:Int, var displayMetrics:DisplayMetrics)
    : RecyclerView.Adapter<spendingAdapter.ViewHolder>() {

    val colorList = arrayListOf<String>("#0E2954","#1F6E8C","#2E8A99","#84A7A1","#CEEDC7","#FFF6BD","#FFD4B2","#FCC8D1","#FFABAB","#D14D72","#9A1663","#CD104D"
        ,"#E14D2A","#FD841F","#FFCC29","#81B214","#206A5D","#1F6E8C","#0A4D68","#00337C")

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
        holder.binding.spender.setBackgroundColor(Color.parseColor(colorList[(items[position].num + 6 * groupNum) % 20]))
        holder.binding.name.text=memberListList[groupNum][items[position].num].name
        holder.binding.amount.text=items[position].amount.toString()
        for(i in items[position].group){
            val btn = AppCompatButton(holder.binding.spendingMemberIcon.context)
            btn.setBackgroundColor(Color.parseColor(colorList[(i.num + 6 * groupNum) % 20]))
            val param = LinearLayout.LayoutParams(changeDP(18),changeDP(18))
            param.setMargins(changeDP(5), 0, 0 ,0)
            param.gravity=Gravity.CENTER_VERTICAL
            btn.layoutParams=param
            holder.binding.spendingMemberIcon.addView(btn)
        }

        //holder.binding.spendingMember.text=items[position].group[0].name.toString() + " 등 " + items[position].group.size + "명"
    }

    private fun changeDP(value: Int): Int {
        var dp = Math.round(value * displayMetrics.density)
        return dp
    }

}
