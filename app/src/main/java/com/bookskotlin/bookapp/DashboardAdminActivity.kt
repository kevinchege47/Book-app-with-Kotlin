package com.bookskotlin.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bookskotlin.bookapp.databinding.ActivityDashboardAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DashboardAdminActivity : AppCompatActivity() {
//    view binding
    private lateinit var binding: ActivityDashboardAdminBinding
//    firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
//        logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
//        get Current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
//            not logged in, go to main screen
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else{
            val email = firebaseUser.email
            binding.subtitleTv.text = email
        }
    }
}