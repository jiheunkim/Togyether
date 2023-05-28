package com.example.togyether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.togyether.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
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
                when (it.itemId) {
                    R.id.first -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        val chatFragment = PigmoneyFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, chatFragment).commit()
                    }
                    R.id.second -> {
                        val myplaceFragment = CategoryFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, myplaceFragment).commit()
                    }
                    R.id.third -> {
                        val friendFragment = DutchpayFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, friendFragment).commit()
                    }
                    R.id.forth -> {
                        val profileFragment = CurrencyFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, profileFragment).commit()
                    }

                }
                true
            }
            selectedItemId = R.id.first
        }
    }
}