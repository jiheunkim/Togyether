package com.example.togyether

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.togyether.Model.PriceModel
import com.google.android.material.bottomsheet.BottomSheetDialog
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

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.dayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_cell, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var day = dayList[holder.adapterPosition] // 날짜
        var selectedDate: LocalDate = LocalDate.now() // 현재 날짜

        if (day != null) {
            holder.dayText.text = day.dayOfMonth.toString()

            // 오늘 날짜 색상 지정
            if (day.isEqual(selectedDate)) {
                holder.dayText.setTextColor(Color.parseColor("#48D366"))
            }
        } else {
            holder.dayText.text = ""
        }

        // 날짜 클릭 이벤트
        holder.itemView.setOnClickListener {
            var iYear = day?.year
            var iMonth = day?.monthValue
            var iDay = day?.dayOfMonth

            var yearMonth = "$iYear 년   $iMonth 월   $iDay 일"

            // BottomSheetDialog 생성
            val bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
            val view = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.dialog_calendar, null)

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

                priceEditText.addTextChangedListener(object : TextWatcher {
                    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        val rawText = s.toString()
                        val price = rawText.replace(Regex("[^0-9]"), "") // 숫자 이외의 모든 문자 제거

                        if (price.isNotEmpty()) {
                            val formattedPrice = numberFormat.format(price.toLong())
                            priceEditText.removeTextChangedListener(this)
                            priceEditText.setText("$formattedPrice 원")
                            priceEditText.setSelection(priceEditText.text.length - 1)
                            priceEditText.addTextChangedListener(this)
                        }
                    }
                })

                // 지출, 수입 버튼
                val expensesBtn = secondView.findViewById<Button>(R.id.expenses_btn)
                val incomeBtn = secondView.findViewById<Button>(R.id.income_btn)

                // 디폴트로 선택된 버튼을 expensesBtn으로 설정
                expensesBtn.setBackgroundResource(R.drawable.btn_circle_green)

                expensesBtn.setOnClickListener {
                    // expensesBtn 클릭 시
                    expensesBtn.setBackgroundResource(R.drawable.btn_circle_green)
                    incomeBtn.setBackgroundResource(R.drawable.btn_circle_gray)
                }

                incomeBtn.setOnClickListener {
                    // incomeBtn 클릭 시
                    incomeBtn.setBackgroundResource(R.drawable.btn_circle_green)
                    expensesBtn.setBackgroundResource(R.drawable.btn_circle_gray)
                }

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

                // btn_content 클릭 이벤트
                val btnCotent = secondView.findViewById<Button>(R.id.btn_content)
                val contentEditText = secondView.findViewById<EditText>(R.id.content_text)

                btnCotent.setOnClickListener {
                    // 가계부 RecyclerView에 데이터 추가
                    val priceTitle = contentEditText.text.toString() // EditText에서 가져온 정보로 대체
                    val priceCategory = "카테고리" // EditText에서 가져온 정보로 대체
                    val priceValue = priceEditText.text.toString()

                    val priceModel = PriceModel(priceTitle, priceCategory, priceValue)
                    priceList.add(priceModel)
                    priceAdapter.notifyItemInserted(priceList.size - 1)

                    // 가계부 내역 추가 다이얼로그 닫기
                    contentAddDialog.dismiss()

                    // RecyclerView 설정
                    recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
                    recyclerView.adapter = priceAdapter
                }
            }

            // BottomSheetDialog 표시
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            // RecyclerView 설정
            recyclerView = view.findViewById(R.id.detailCalendar)
            recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            recyclerView.adapter = priceAdapter
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }
}