package com.example.togyether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.togyether.Model.User
import com.example.togyether.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    lateinit var binding: ActivityJoinBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        binding.apply {
            // 가입하기 버튼 비활성화
            joinButton.isEnabled=false

            passwdAreaCheck.addTextChangedListener(object : TextWatcher {
                //입력이 끝났을 때
                // 비밀번호 일치하는지 확인
                override fun afterTextChanged(p0: Editable?) {
                    if(passwdArea.text.toString().equals(passwdAreaCheck.text.toString())){
                        pwConfirm.text = "비밀번호가 일치합니다."
                        pwConfirm.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.black))

                        // 가입하기 버튼 활성화
                        joinButton.isEnabled=true
                    }
                    else{
                        pwConfirm.text = "비밀번호가 일치하지 않습니다."
                    }
                }
                //입력하기 전
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                //텍스트 변화가 있을 시
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(passwdArea.text.toString().equals(passwdAreaCheck.text.toString())){
                        pwConfirm.text = "비밀번호가 일치합니다."
                        pwConfirm.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.black))

                        // 가입하기 버튼 활성화
                        joinButton.isEnabled=true
                    }
                    else{
                        pwConfirm.text = "비밀번호가 일치하지 않습니다."
                    }
                }
            })

            //회원가입 버튼 누를 경우
            joinButton.setOnClickListener {
                //입력한 id, passwd를 가져와 변수에 저장
                val id = idArea.text.toString()
                val passwd = passwdArea.text.toString()

                //인증 데이터베이스(로그인용)에 사용자 추가
                auth = Firebase.auth
                auth.createUserWithEmailAndPassword(id, passwd)
                    .addOnCompleteListener(this@JoinActivity) { task ->
                        if (task.isSuccessful) {
                            //추가 성공할 경우
                            val uid = FirebaseAuth.getInstance().uid ?: null
                            val username = username.text.toString()
                            val user = User(uid!!, username)

                            //데이터베이스에 회원가입한 사용자 정보 추가
                            val database = Firebase.database
                            val table = database.getReference("togyether")
                            val userEntry = table.child(uid)
                            userEntry.setValue(user).addOnCompleteListener { userEntryTask ->
                                if (userEntryTask.isSuccessful) {
                                    // 데이터베이스에 사용자 정보 추가 성공
                                    val year = "2023" // 예시로 고정된 연도
                                    val months = arrayOf("1m", "2m", "3m", "4m", "5m", "6m", "7m", "8m", "9m", "10m", "11m", "12m")
                                    val days28 = arrayOf("1d", "2d", "3d", "4d", "5d", "6d", "7d", "8d", "9d", "10d", "11d", "12d", "13d", "14d", "15d", "16d", "17d", "18d", "19d", "20d", "21d", "22d", "23d", "24d", "25d", "26d", "27d", "28d")
                                    val days30 = days28 + arrayOf("29d", "30d")
                                    val days31 = days30 + arrayOf("31d")

                                    userEntry.child("calendar").child("sum").setValue(0)

                                    // 월별 데이터 초기화
                                    for (month in months) {
                                        val monthEntry = userEntry.child("calendar").child(year).child(month)
                                        monthEntry.child("budget").setValue(0)
                                        monthEntry.child("expense").setValue(0)
                                        monthEntry.child("income").setValue(0)

                                        // 일별 데이터 초기화
                                        val days: Array<String> = when (month) {
                                            "2m" -> days28
                                            "4m", "6m", "9m", "11m" -> days30
                                            else -> days31
                                        }
                                        for (day in days) {
                                            monthEntry.child(day).setValue("")
                                            monthEntry.child(day).child("total").setValue(0)
                                            monthEntry.child(day).child("num").setValue(0)
                                        }
                                    }

                                    Toast.makeText(this@JoinActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()

                                    //로그인 화면으로 전환
                                    val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    // 데이터베이스에 사용자 정보 추가 실패
                                    Toast.makeText(this@JoinActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("REGISTER", "실패")
                            Toast.makeText(this@JoinActivity, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        }
    }
}