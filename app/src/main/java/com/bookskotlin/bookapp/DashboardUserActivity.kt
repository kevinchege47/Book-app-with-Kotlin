package com.bookskotlin.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bookskotlin.bookapp.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardUserActivity : AppCompatActivity() {
//    view binding
    private lateinit var binding:ActivityDashboardUserBinding
    //    firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
//        handle logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this@DashboardUserActivity,MainActivity::class.java))
            finish()
        }
    }

    private fun checkUser() {
        //        get Current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
//            not logged in, go to main screen
            binding.subtitleTv.text = "Not Logged In"
        }else{
            val email = firebaseUser.email
            binding.subtitleTv.text = email
        }
    }
}