package com.example.togyether

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.togyether.databinding.FragmentCurrencyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import kotlin.math.round
import kotlin.math.roundToLong


class CurrencyFragment : Fragment() {

    private var columnCount = 1
    //var data:ArrayList<MyNewsData> = ArrayList()
    lateinit var binding: FragmentCurrencyBinding
    var etNumber = 0
    var url = "https://obank.kbstar.com/quics?page=C101423#loading"
    var valuedata:ArrayList<Double> = ArrayList<Double>()
    var index = 0
    val scope = CoroutineScope(Dispatchers.IO)
    lateinit var cActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cActivity = context as MainActivity
    }

    fun getdata(){
        scope.launch {

            val doc = Jsoup.connect(url).get()
            //Log.i("check", doc.toString())
            var name = doc.select("#inqueryTable > table:nth-child(2) > tbody > tr> td.tLeft > a")
            var value = doc.select("#inqueryTable > table:nth-child(2) > tbody > tr > td:nth-child(3)")
            val adapter = ArrayAdapter(cActivity, R.layout.row_spinner, ArrayList<String>())

            for(i:Int in 0..name.size - 1){
                if(i==1){
                    adapter.add("일본(엔)")
                    valuedata.add(value[i].text().replace(",".toRegex(),"").toDouble() / 100.0)
                }
                else if(i==19){
                    adapter.add("인도네시아(루피아)")
                    valuedata.add(value[i].text().replace(",".toRegex(),"").toDouble() / 100.0)
                }
                else if(i==36){
                    adapter.add("베트남(동)")
                    valuedata.add(value[i].text().replace(",".toRegex(),"").toDouble() / 100.0)
                }
                else {
                    adapter.add(name[i].text())
                    valuedata.add(value[i].text().replace(",".toRegex(),"").toDouble())
                }
                Log.i("val", value[i].text())
            }

            Thread(Runnable{
                cActivity?.runOnUiThread {
                    binding.spinner1.adapter = adapter
                    binding.spinner1.setSelection(0)
                    binding.title.visibility = View.VISIBLE
                    binding.won.visibility = View.VISIBLE
                    binding.other.visibility = View.VISIBLE
                }
            }).start()


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCurrencyBinding.inflate(layoutInflater, container, false)
        binding.title.visibility = View.INVISIBLE
        binding.won.visibility = View.INVISIBLE
        binding.other.visibility = View.INVISIBLE

        //val view = inflater.inflate(R.layout.fragment_news_list, container, false)
        val view = binding.root

        with(view) {
            getdata()
        }

        binding.spinner1.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                index = position
                if(binding.edittext1.text != null && binding.edittext1.text.matches(Regex("""[-+]?\d*\.?\d+""")))
                    if(binding.edittext1.isFocused){
                        etNumber = 1
                        var value = binding.edittext1.text.toString().toDouble() * valuedata[index]
                        binding.edittext2.setText(String.format("%.4f",value))
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        binding.edittext1.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edittext1.text.matches(Regex(""))){
                    binding.edittext2.text.clear()
                }
                else if(binding.edittext1.text.matches(Regex("""[-+]?\d*\.?\d+"""))){
                    if(binding.edittext1.isFocused){
                        etNumber = 2
                        var value = binding.edittext1.text.toString().toDouble() / valuedata[index]
                        binding.edittext2.setText(String.format("%.4f",value))

                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.edittext2.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edittext2.text.matches(Regex(""))){
                    binding.edittext1.text.clear()
                }
                else if(binding.edittext2.text.matches(Regex("""[-+]?\d*\.?\d+"""))){
                    if(binding.edittext2.isFocused){
                        var value = binding.edittext2.text.toString().toDouble() * valuedata[index]
                        binding.edittext1.setText(String.format("%.4f",value))
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        //getdata()

        return view
    }


}
