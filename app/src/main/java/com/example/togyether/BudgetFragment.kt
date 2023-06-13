package com.example.togyether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.togyether.databinding.FragmentBudgetBinding
import android.app.AlertDialog
import android.text.InputType
import android.widget.EditText

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private var currentMonth: Int = 5 // 현재 월
    private var budget: Int = 0 // 예산
    private var expenses: Int = 0 // 지출

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        val view = binding.root

        updateMonthView()
        updateExpenseRatio()

        // 월 이동 버튼 클릭 이벤트 처리
        binding.lastMonth.setOnClickListener {
            currentMonth--
            updateMonthView()
        }

        binding.nextMonth.setOnClickListener {
            currentMonth++
            updateMonthView()
        }

        // 설정 버튼 클릭 이벤트 처리
        binding.settings.setOnClickListener {
            // 설정 화면으로 이동하는 코드 추가
            showBudgetSettingDialog()
        }

        updateMonthView()

        return view
    }

    private fun showBudgetSettingDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("예산 설정하기")

        // 다이얼로그에 입력 필드 추가
        val budgetEditText = EditText(requireContext())
        budgetEditText.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(budgetEditText)

        // 확인 버튼 클릭 시 예산 설정 처리
        builder.setPositiveButton("확인") { dialog, _ ->
            val budgetString = budgetEditText.text.toString()
            val budget = budgetString.toIntOrNull()
            if (budget != null) {
                setBudget(budget)
            }
            dialog.dismiss()
        }

        // 취소 버튼 클릭 시 다이얼로그 닫기
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        // 다이얼로그 표시
        builder.show()
    }

    // 예산 설정 처리
    private fun setBudget(budget: Int) {
        binding.planMoney.text = budget.toString()
        updateExpenseRatio()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateMonthView() {
        binding.nowMonth.text = "${currentMonth}월 2023"

        // 예산과 지출 데이터를 가져와서 업데이트
        budget = getBudgetForMonth(currentMonth)
        expenses = getExpensesForMonth(currentMonth)

        //binding.budgetAmount.text = budget.toString()
        //binding.expenseAmount.text = expenses.toString()

        val ratio = if (budget != 0) expenses.toFloat() / budget.toFloat() else 0.0f
        //binding.ratioText.text = String.format("%.1f", ratio * 100) + "%"

        //updateRatioBar(ratio)
    }

    // 지출 비율 업데이트
    private fun updateExpenseRatio() {
        val progressBar = binding.progressBar
        val expenseRatio = if (budget != 0) {
            (expenses.toFloat() / budget.toFloat() * 100).toInt()
        } else {
            0
        }
        progressBar.progress = expenseRatio
    }

    private fun getBudgetForMonth(month: Int): Int {
        // 해당 월에 대한 예산 데이터를 가져오는 코드 추가
        // 예: return budgetData[month]
        return 0
    }

    private fun getExpensesForMonth(month: Int): Int {
        // 해당 월에 대한 지출 데이터를 가져오는 코드 추가
        // 예: return expenseData[month]
        return 0
    }
}
