package com.example.togyether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.togyether.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var pigmoneyFragment: Fragment? = null
    var categoryFragment: Fragment? = null
    var dutchpayFrameFragment: Fragment? = null
    var currencyFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        startFragment()
    }

    private fun startFragment() {
        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        var mainBtn = findViewById<BottomNavigationView>(R.id.main_btn)

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        mainBtn.run()
        {
            setOnNavigationItemSelectedListener {
                // 다른 프래그먼트 화면으로 이동하는 기능
                when (it.itemId) {
                    R.id.first -> {
                        if(pigmoneyFragment == null){
                            pigmoneyFragment = PigmoneyFragment()
                            supportFragmentManager.beginTransaction().add(R.id.container, pigmoneyFragment!!).commit()
                        }
                        if(pigmoneyFragment != null) supportFragmentManager.beginTransaction().show(pigmoneyFragment!!).commit()
                        if(categoryFragment != null) supportFragmentManager.beginTransaction().hide(categoryFragment!!).commit()
                        if(dutchpayFrameFragment != null) supportFragmentManager.beginTransaction().hide(dutchpayFrameFragment!!).commit()
                        if(currencyFragment != null) supportFragmentManager.beginTransaction().hide(currencyFragment!!).commit()
                    }
                    R.id.second -> {
                        if(categoryFragment == null){
                            categoryFragment = CategoryFragment()
                            supportFragmentManager.beginTransaction().add(R.id.container, categoryFragment!!).commit()
                        }
                        if(pigmoneyFragment != null) supportFragmentManager.beginTransaction().hide(pigmoneyFragment!!).commit()
                        if(categoryFragment != null) supportFragmentManager.beginTransaction().show(categoryFragment!!).commit()
                        if(dutchpayFrameFragment != null) supportFragmentManager.beginTransaction().hide(dutchpayFrameFragment!!).commit()
                        if(currencyFragment != null) supportFragmentManager.beginTransaction().hide(currencyFragment!!).commit()
                    }
                    R.id.third -> {
                        if(dutchpayFrameFragment == null){
                            dutchpayFrameFragment = DutchpayFrameFragment()
                            supportFragmentManager.beginTransaction().add(R.id.container, dutchpayFrameFragment!!).commit()
                        }
                        if(pigmoneyFragment != null) supportFragmentManager.beginTransaction().hide(pigmoneyFragment!!).commit()
                        if(categoryFragment != null) supportFragmentManager.beginTransaction().hide(categoryFragment!!).commit()
                        if(dutchpayFrameFragment != null) supportFragmentManager.beginTransaction().show(dutchpayFrameFragment!!).commit()
                        if(currencyFragment != null) supportFragmentManager.beginTransaction().hide(currencyFragment!!).commit()
                    }
                    R.id.forth -> {
                        if(currencyFragment == null){
                            currencyFragment = CurrencyFragment()
                            supportFragmentManager.beginTransaction().add(R.id.container, currencyFragment!!).commit()
                        }
                        if(pigmoneyFragment != null) supportFragmentManager.beginTransaction().hide(pigmoneyFragment!!).commit()
                        if(categoryFragment != null) supportFragmentManager.beginTransaction().hide(categoryFragment!!).commit()
                        if(dutchpayFrameFragment != null) supportFragmentManager.beginTransaction().hide(dutchpayFrameFragment!!).commit()
                        if(currencyFragment != null) supportFragmentManager.beginTransaction().show(currencyFragment!!).commit()
                    }

                }
                true
            }
            selectedItemId = R.id.first
        }
    }
}
