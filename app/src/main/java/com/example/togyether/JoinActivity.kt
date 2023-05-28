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
            passwdAreaCheck.addTextChangedListener(object : TextWatcher {
                //입력이 끝났을 때
                //4. 비밀번호 일치하는지 확인
                override fun afterTextChanged(p0: Editable?) {
                    if(passwdArea.text.toString().equals(passwdAreaCheck.text.toString())){
                        pwConfirm.text = "비밀번호가 일치합니다."
                        pwConfirm.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.black))
                        // 가입하기 버튼 활성화
                        joinButton.isEnabled=true
                    }
                    else{
                        pwConfirm.text = "비밀번호가 일치하지 않습니다."
                        // 가입하기 버튼 비활성화
                        joinButton.isEnabled=false
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
                        // 가입하기 버튼 비활성화
                        joinButton.isEnabled=false
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
                            val table = Firebase.database.getReference("users")
                            val tuple = table.child(uid)
                            tuple.setValue(user).addOnCompleteListener{
                                Toast.makeText(this@JoinActivity, "객체넣기 성공", Toast.LENGTH_LONG).show()
                            }
                                .addOnFailureListener {
                                    Toast.makeText(this@JoinActivity, "객체넣기 실패", Toast.LENGTH_LONG).show()
                                }

                            //로그인 화면으로 전환
                            val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("REGISTER", "실패")
                        }
                    }
            }
        }
    }
}