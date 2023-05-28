package com.example.togyether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.togyether.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        binding.apply {
            loginBtn.setOnClickListener {
                val id = loginId.text.toString()
                val passwd = loginPasswd.text.toString()

                auth = Firebase.auth

                if (id.isEmpty() && passwd.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if (id.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if (passwd.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(id, passwd)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information

                                //데이터베이스에 user정보를 넣어줘야함
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                // If sign in fails, display a message to the user.
                                //updateUI(null)
                                Toast.makeText(this@LoginActivity, "login 실패", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }

            joinBtn.setOnClickListener {
                val intent = Intent(this@LoginActivity, JoinActivity::class.java)
                startActivity(intent)
            }
        }
    }
}