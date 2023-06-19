package com.example.togyether

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.Model.PriceModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

    // 항목 삭제 메서드
    fun deleteItem(position: Int) {
        // 데이터 삭제
        println("priceList size before deletion: ${priceList.size}")
        val removedItem = priceList[position]

        // 삭제 데이터 정보
        val title = removedItem.title
        val category = removedItem.category
        val date = removedItem.date

        // 데이터베이스에서도 삭제
        val db = Firebase.database.getReference("togyether")
        val userUid = FirebaseAuth.getInstance().currentUser?.uid

        // 날짜 정보
        var modelMonth = ""
        var modelDay = ""

        val regex = """(\d+)\s+년\s+(\d+)\s+월\s+(\d+)\s+일""".toRegex()
        val matchResult = regex.find(date)
        if (matchResult != null) {
            val year = matchResult.groups[1]?.value
            val month = matchResult.groups[2]?.value
            val day = matchResult.groups[3]?.value
            if (year != null && month != null && day != null) {
                println("Year: $year, Month: $month, Day: $day")
                modelMonth = month
                modelDay = day
            }
        }

        if (userUid != null) {
            db.child(userUid)
                .child("calendar")
                .child("2023")
                .child(modelMonth + "m")
                .child(modelDay + "d")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (itemSnapshot in dataSnapshot.children) {
                            val itemTitle = itemSnapshot.child("title").getValue(String::class.java)
                            val itemCategory =
                                itemSnapshot.child("category").getValue(String::class.java)
                            val itemPrice = itemSnapshot.child("price").getValue(String::class.java)
                            val priceInt = if (itemPrice?.startsWith("-") == true) { // 가격 숫자 정보만 넣기
                                val priceDigits = itemPrice?.replace(Regex("[^0-9]"), "")
                                -priceDigits?.toInt()!!
                            } else {
                                itemPrice?.replace(Regex("[^0-9]"), "")?.toInt()
                            }

                            if (itemTitle == title && itemCategory == category) {
                                val orderRef = itemSnapshot.ref
                                val orderRefString = orderRef.toString() // 참조를 문자열로 변환
                                var numberString = ""

                                val lastSlashIndex = orderRefString.lastIndexOf("/") // 마지막 '/'의 인덱스 가져오기
                                if (lastSlashIndex != -1 && lastSlashIndex < orderRefString.length - 1) {
                                    numberString = orderRefString.substring(lastSlashIndex + 1) // 마지막 '/' 다음의 문자열 가져오기
                                    //val number = numberString.toIntOrNull() // 문자열을 정수로 변환 (null일 수 있음)
                                }

                                val totalRef = itemSnapshot.ref.parent?.child("total")
                                val numRef = itemSnapshot.ref.parent?.child("num")
                                var intNumRef = 0
                                val incomeRef = itemSnapshot.ref.parent?.parent?.child("income")
                                val expenseRef = itemSnapshot.ref.parent?.parent?.child("expense")
                                val sumRef = itemSnapshot.ref.parent?.parent?.parent?.parent?.child("sum")

                                // 데이터 삭제
                                orderRef.removeValue()
                                Log.i("update", "삭제 성공:$position")
//                                if (position >= 0 && position < priceList.size) {
//                                    priceList.removeAt(position)
//                                    println(">>>$title")
//                                    println("priceList size after deletion: ${priceList.size}")
//
//                                    // 뷰 업데이트
//                                    notifyDataSetChanged()
//                                }

                                numRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        intNumRef = dataSnapshot.getValue(Int::class.java)!!
                                        println(">>>num:$intNumRef")
                                        if (intNumRef != null) {
                                            numRef.setValue(intNumRef - 1)

                                            for (itemOrder in 0 until intNumRef) {
                                                println("hihihi")
                                                if (itemOrder >= numberString.toInt()) {
                                                    val nextRef = itemOrder + 1
                                                    println(">>>$nextRef")
                                                    db.child(userUid)
                                                        .child("calendar")
                                                        .child("2023")
                                                        .child(modelMonth + "m")
                                                        .child(modelDay + "d")
                                                        .child(nextRef.toString())
                                                        .addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                                // 1. 기존 경로의 데이터 읽기
                                                                val data = dataSnapshot.value

                                                                // 2. 새로운 경로에 데이터 복사
                                                                db.child(userUid)
                                                                    .child("calendar")
                                                                    .child("2023")
                                                                    .child(modelMonth + "m")
                                                                    .child(modelDay + "d")
                                                                    .child(itemOrder.toString())
                                                                    .setValue(data)
                                                                    .addOnSuccessListener {
                                                                        // 복사 성공 시 기존 경로의 데이터 삭제
                                                                        dataSnapshot.ref.removeValue()
                                                                        Log.i("update", "옮기기 성공")
                                                                    }
                                                                    .addOnFailureListener {
                                                                        Log.i("update", "경로 변경 실패")
                                                                    }
                                                            }

                                                            override fun onCancelled(databaseError: DatabaseError) {
                                                                Log.i("update", "데이터 읽기 실패")
                                                            }
                                                        })
                                                }
                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.i("update", "num 변경 실패")
                                    }
                                })

                                totalRef?.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val totalValue = dataSnapshot.getValue(Int::class.java)
                                        if (totalValue != null) {
                                            totalRef.setValue(totalValue - priceInt!!)
                                            Log.i("update", "total 변경 성공")
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.i("update", "total 변경 실패")
                                    }
                                })

                                if (priceInt != null) {
                                    if (priceInt > 0) {
                                        incomeRef?.addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                val incomeValue = dataSnapshot.getValue(Int::class.java)
                                                if (incomeValue != null) {
                                                    incomeRef.setValue(incomeValue - priceInt!!)
                                                    Log.i("update", "income 변경 성공")
                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                Log.i("update", "income 변경 실패")
                                            }
                                        })
                                    } else {
                                        expenseRef?.addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                val expenseValue = dataSnapshot.getValue(Int::class.java)
                                                if (expenseValue != null) {
                                                    expenseRef.setValue(expenseValue + priceInt!!)
                                                    Log.i("update", "expense 변경 성공")
                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                Log.i("update", "expense 변경 실패")
                                            }
                                        })
                                    }
                                }

                                sumRef?.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val sumValue = dataSnapshot.getValue(Int::class.java)
                                        if (sumValue != null) {
                                            sumRef.setValue(sumValue - priceInt!!)
                                            Log.i("update", "sum 변경 성공")
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.i("update", "sum 변경 실패")
                                    }
                                })

                                // 화면 갱신
                                notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.i("update", "전체 실패")
                    }
                })
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