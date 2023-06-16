package com.example.togyether

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.Model.PriceModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Math.abs
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class CalendarAdapter(private val dayList: ArrayList<LocalDate?>):
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {

    // 가계부 내역을 저장할 리스트
    private val priceList: MutableList<PriceModel> = mutableListOf()
    // PriceAdapter 인스턴스
    private val priceAdapter: PriceAdapter = PriceAdapter(priceList)
    // RecyclerView 인스턴스
    private lateinit var recyclerView: RecyclerView

    lateinit var listSize: String
    lateinit var myUid: String

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.dayText)
        val moneyInfo: TextView = itemView.findViewById(R.id.moneyInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_cell, parent, false)

        return ItemViewHolder(view)
    }

    /**
     * 리사이클러뷰의 데이터를 업데이트하고 화면 갱신
     *
     * @param year 연도
     * @param month 월
     * @param day 일
     */
    private fun updateRecyclerViewData(year: String, month: String, day: String, onComplete: (Int) -> Unit) {
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!

        val db = Firebase.database.getReference("togyether")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (users in dataSnapshot.children) {
                    val uid = users.child("uid").value.toString()

                    if (myUid == uid) {
                        val userDB = users.child("calendar")
                            .child(year).child(month).child(day)
                        val num = userDB.child("num").value.toString()

                        // 리사이클러뷰에 등록된 가계부 내역을 불러옴
                        priceList.clear()
                        for (i in 0 until num.toInt()) {
                            val priceEntry = userDB.child(i.toString())
                            val priceCategory = priceEntry.child("category").value.toString()
                            val priceDate = priceEntry.child("date").value.toString()
                            val priceValue = priceEntry.child("price").value.toString()
                            val priceTitle = priceEntry.child("title").value.toString()
                            val priceModel = PriceModel(priceDate, priceTitle, priceCategory, priceValue)
                            priceList.add(priceModel)
                        }

                        // onComplete 콜백 함수 호출하여 데이터 크기 전달
                        onComplete(priceList.size)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error handling
            }
        })
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!
        val db = Firebase.database.getReference("togyether")
        listSize = "0"

        var day = dayList[holder.adapterPosition] // 날짜
        var selectedDate: LocalDate = LocalDate.now() // 현재 날짜
        var iYear = day?.year
        var iMonth = day?.monthValue
        var iDay = day?.dayOfMonth

        if (day != null) {
            holder.dayText.text = day.dayOfMonth.toString()
            // 오늘 날짜 색상 지정
            if (day.isEqual(selectedDate)) {
                holder.dayText.setTextColor(Color.parseColor("#48D366"))
            }

            // ValueEventListener 생성
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // 데이터베이스의 값이 변경되었을 때 실행되는 로직을 여기에 작성
                    db.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (users in dataSnapshot.children) {
                                val uid = users.child("uid").value.toString()

                                if (myUid == uid) {
                                    val total = users.child("calendar").child(iYear.toString())
                                        .child(iMonth.toString() + "m").child(iDay.toString() + "d")
                                        .child("total").value.toString()

                                    val numberFormat = NumberFormat.getInstance(Locale.getDefault())
                                    if (total.toInt() != 0) {
                                        holder.moneyInfo.text = numberFormat.format(total.toInt())
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Error handling
                        }
                    })
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 에러 처리
                }
            }

            // ValueEventListener를 addValueEventListener로 등록하여 데이터의 변화를 감지합니다.
            db.addValueEventListener(valueEventListener)

            holder.itemView.setOnClickListener {
                var yearMonth = "$iYear 년   $iMonth 월   $iDay 일"

                // BottomSheetDialog 생성
                val bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
                val view = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.dialog_calendar, null)

                // RecyclerView 설정
                val recyclerView = view.findViewById<RecyclerView>(R.id.detailCalendar)
                recyclerView.layoutManager = LinearLayoutManager(view.context)
                recyclerView.adapter = priceAdapter

                // 리사이클러뷰에 등록된 가계부 내역을 불러오기 위해 데이터베이스 정보 업데이트
                // 리사이클러뷰의 어댑터에 데이터를 다시 설정하고 notifyDatasetChanged를 호출하여 업데이트된 데이터 표시
                val year = iYear.toString()
                val month = iMonth.toString() + "m"
                val day = iDay.toString() + "d"
                updateRecyclerViewData(year, month, day) { size ->
                    // 데이터 변경을 알림
                    priceAdapter.notifyDataSetChanged()

                    // totalDialog에 아이템 개수 설정
                    val totalDialog = view.findViewById<TextView>(R.id.totalDialog)
                    totalDialog.text = size.toString()
                }

                // ValueEventListener 생성
                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // 데이터베이스의 값이 변경되었을 때 실행되는 로직을 여기에 작성
                        // totalSum에 금액 총합 정보 넣기
                        val totalSum = view.findViewById<TextView>(R.id.total_sum)
                        db.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (users in dataSnapshot.children) {
                                    val uid = users.child("uid").value.toString()

                                    if (myUid == uid) {
                                        val total = users.child("calendar").child(iYear.toString())
                                            .child(iMonth.toString() + "m").child(iDay.toString() + "d")
                                            .child("total").value.toString()

                                        val numberFormat = NumberFormat.getInstance(Locale.getDefault())
                                        totalSum.text = numberFormat.format(total.toInt())
                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                // Error handling
                            }
                        })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // 에러 처리
                    }
                }

                // ValueEventListener를 addValueEventListener로 등록하여 데이터의 변화를 감지합니다.
                db.addValueEventListener(valueEventListener)

                // BottomSheetDialog 표시
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()

                // BottomSheetDialog의 콘텐츠 레이아웃에서 날짜 정보 표시
                val dateTextView = view.findViewById<TextView>(R.id.dayDialog)
                dateTextView.text = yearMonth

                // btn_add 클릭 이벤트
                val btnAdd = view.findViewById<ImageView>(R.id.btn_add)
                btnAdd.setOnClickListener {
                    // 가계부 내역 추가 다이얼로그 생성
                    val contentAddDialog = BottomSheetDialog(holder.itemView.context)
                    val secondView = LayoutInflater.from(holder.itemView.context)
                        .inflate(R.layout.dialog_content, null)

                    // 가계부 내역 추가 다이얼로그 내부 로직 구현
                    // ,,원 자동 추가
                    val priceEditText = secondView.findViewById<EditText>(R.id.price)
                    priceEditText.setText("")

                    // 가계부 내역 추가 다이얼로그 스피너 초기화
                    val spinner = secondView.findViewById<AppCompatSpinner>(R.id.category_spinner)
                    val categories = listOf("식당", "카페", "생필품", "문화", "주거", "금융", "쇼핑", "교통", "여행")
                    val categories2 = listOf("급여", "용돈", "금융수입", "기타수입")
                    val categoryAdapter = CategoryAdapter(holder.itemView.context, categories)
                    val categoryAdapter2 = CategoryAdapter2(holder.itemView.context, categories2)
                    spinner.adapter = categoryAdapter

                    // 지출, 수입 버튼
                    val expensesBtn = secondView.findViewById<Button>(R.id.expenses_btn)
                    val incomeBtn = secondView.findViewById<Button>(R.id.income_btn)

                    // 디폴트로 선택된 버튼을 expensesBtn으로 설정
                    expensesBtn.setBackgroundResource(R.drawable.btn_circle_green)

                    var isExpense = true // expensesBtn이 선택되었는지 여부를 나타내는 변수

                    expensesBtn.setOnClickListener {
                        // expensesBtn 클릭 시
                        isExpense = true
                        expensesBtn.setBackgroundResource(R.drawable.btn_circle_green)
                        incomeBtn.setBackgroundResource(R.drawable.btn_circle_gray)
                        priceEditText.setText("")
                        spinner.adapter = categoryAdapter
                    }

                    incomeBtn.setOnClickListener {
                        // incomeBtn 클릭 시
                        isExpense = false
                        incomeBtn.setBackgroundResource(R.drawable.btn_circle_green)
                        expensesBtn.setBackgroundResource(R.drawable.btn_circle_gray)
                        priceEditText.setText("")
                        spinner.adapter = categoryAdapter2
                    }

                    priceEditText.addTextChangedListener(object : TextWatcher {
                        private val numberFormat =
                            NumberFormat.getNumberInstance(Locale.getDefault())

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            val rawText = s.toString()
                            val price = rawText.replace(Regex("[^0-9]"), "") // 숫자 이외의 모든 문자 제거

                            if (isExpense) {
                                if (price.isNotEmpty()) {
                                    val formattedPrice = numberFormat.format(price.toLong())
                                    priceEditText.removeTextChangedListener(this)
                                    priceEditText.setText("-$formattedPrice 원")
                                    priceEditText.setSelection(priceEditText.text.length - 1)
                                    priceEditText.addTextChangedListener(this)
                                }
                            } else {
                                if (price.isNotEmpty()) {
                                    val formattedPrice = numberFormat.format(price.toLong())
                                    priceEditText.removeTextChangedListener(this)
                                    priceEditText.setText("$formattedPrice 원")
                                    priceEditText.setSelection(priceEditText.text.length - 1)
                                    priceEditText.addTextChangedListener(this)
                                }
                            }
                        }
                    })

                    // 가계부 내역 추가 다이얼로그 날짜 정보 넣기
                    val dateTextView2 = secondView.findViewById<TextView>(R.id.date_text)
                    dateTextView2.text = yearMonth

                    // 가계부 내역 추가 다이얼로그 고정지출 정보 받기
                    val checkBox = secondView.findViewById<CheckBox>(R.id.cb_necessary)
                    checkBox.setOnClickListener {
                        //추가 구현
                    }

                    // 가계부 내역 추가 다이얼로그 표시
                    contentAddDialog.setContentView(secondView)
                    contentAddDialog.show()

                    // 가계부 내역 추가 다이얼로그 닫기 버튼 이벤트
                    val btnclose = secondView.findViewById<ImageView>(R.id.imageView)
                    btnclose.setOnClickListener {
                        // 가계부 내역 추가 다이얼로그 닫기
                        contentAddDialog.dismiss()
                    }

                    // btn_content 클릭 이벤트
                    val btnCotent = secondView.findViewById<Button>(R.id.btn_content)
                    val contentEditText = secondView.findViewById<EditText>(R.id.content_text)

                    btnCotent.setOnClickListener {
                        // 가계부 RecyclerView에 데이터 추가
                        val priceTitle = contentEditText.text.toString() // 내용 정보
                        val priceCategory = spinner.selectedItem.toString() // 선택한 카테고리 스피너 정보 가져오기
                        val priceValue = priceEditText.text.toString()
                        val priceInt = if (priceValue.startsWith("-")) { // 가격 숫자 정보만 넣기
                            val priceDigits = priceValue.replace(Regex("[^0-9]"), "")
                            -priceDigits.toInt()
                        } else {
                            priceValue.replace(Regex("[^0-9]"), "").toInt()
                        }
                        val priceDate = dateTextView.text.toString() // 날짜 정보
                        val priceModel =
                            PriceModel(priceDate, priceTitle, priceCategory, priceValue)
                        var subNum = 0

                        db.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (users in dataSnapshot.children) {
                                    val uid = users.child("uid").value.toString()

                                    if (myUid == uid) {
                                        // 데이터베이스에 가계부 내역 추가
                                        val userDB = users.child("calendar")
                                            .child(iYear.toString()).child(iMonth.toString() + "m")
                                            .child(iDay.toString() + "d")
                                        val num = userDB.child("num").value.toString()
                                        userDB.child(num)

                                        subNum = num.toInt()
                                    }
                                }

                                // 데이터베이스에 가계부 내역 추가
                                // sum 값 업데이트
                                val sumRef = db.child(myUid).child("calendar").child("sum")
                                db.child(myUid).child("calendar")
                                    .child("sum")
                                    .addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val oldSum =
                                                snapshot.getValue(Int::class.java)
                                                    ?: 0
                                            val sum = oldSum + priceInt
                                            sumRef.setValue(sum)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })

                                if (priceInt < 0) {
                                    // 월별 expense 업데이트
                                    db.child(myUid).child("calendar")
                                        .child(iYear.toString()).child(iMonth.toString() + "m")
                                        .child("expense")
                                        .addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val oldTotal =
                                                    snapshot.getValue(Int::class.java) ?: 0

                                                // 새로운 expense 값 계산
                                                val newExpense =
                                                    oldTotal + kotlin.math.abs(priceInt)

                                                // expense 값 업데이트
                                                db.child(myUid).child("calendar")
                                                    .child(iYear.toString())
                                                    .child(iMonth.toString() + "m")
                                                    .child("expense")
                                                    .setValue(newExpense)
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }
                                        })
                                } else {
                                    // 월별 income 업데이트
                                    db.child(myUid).child("calendar")
                                        .child(iYear.toString()).child(iMonth.toString() + "m")
                                        .child("income")
                                        .addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val oldTotal =
                                                    snapshot.getValue(Int::class.java) ?: 0

                                                // 새로운 income 값 계산
                                                val newIncome = oldTotal + priceInt

                                                // income 값 업데이트
                                                db.child(myUid).child("calendar")
                                                    .child(iYear.toString())
                                                    .child(iMonth.toString() + "m")
                                                    .child("income")
                                                    .setValue(newIncome)
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                // 처리 중 오류가 발생한 경우의 로직 추가
                                            }
                                        })
                                }

                                // total 업데이트
                                db.child(myUid).child("calendar")
                                    .child(iYear.toString()).child(iMonth.toString() + "m")
                                    .child(iDay.toString() + "d").child("total")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val oldTotal = snapshot.getValue(Int::class.java) ?: 0

                                            // 새로운 total 값 계산
                                            val newTotal = oldTotal + priceInt

                                            // total 값 업데이트
                                            db.child(myUid).child("calendar")
                                                .child(iYear.toString())
                                                .child(iMonth.toString() + "m")
                                                .child(iDay.toString() + "d")
                                                .child("total")
                                                .setValue(newTotal)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // 처리 중 오류가 발생한 경우의 로직 추가
                                        }
                                    })

                                val priceEntry = db.child(myUid).child("calendar")
                                    .child(iYear.toString()).child(iMonth.toString() + "m")
                                    .child(iDay.toString() + "d").child(subNum.toString())
                                priceEntry.child("category").setValue(priceCategory)
                                priceEntry.child("date").setValue(priceDate)
                                priceEntry.child("price").setValue(priceValue)
                                priceEntry.child("title").setValue(priceTitle)

                                // num 값 1 증가
                                db.child(myUid).child("calendar")
                                    .child(iYear.toString()).child(iMonth.toString() + "m")
                                    .child(iDay.toString() + "d").child("num").setValue(subNum + 1)

                                priceList.add(priceModel)
                                priceAdapter.notifyItemInserted(priceList.size - 1)

                                // 가계부 내역 추가 다이얼로그 닫기
                                contentAddDialog.dismiss()

                                // RecyclerView 설정
                                val recyclerView =
                                    view.findViewById<RecyclerView>(R.id.detailCalendar)
                                recyclerView.layoutManager = LinearLayoutManager(view.context)
                                recyclerView.adapter = priceAdapter

                                updateRecyclerViewData(year, month, day) { size ->
                                    // 데이터 변경을 알림
                                    priceAdapter.notifyDataSetChanged()

                                    // totalDialog에 아이템 개수 설정
                                    val totalDialog = view.findViewById<TextView>(R.id.totalDialog)
                                    totalDialog.text = size.toString()
                                }

                                // BottomSheetDialog 표시
                                bottomSheetDialog.setContentView(view)
                                bottomSheetDialog.show()
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Error handling
                            }
                        })
                    }
                }
            }
        } else {
            holder.dayText.text = ""
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }
}