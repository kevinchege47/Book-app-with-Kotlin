package com.bookskotlin.bookapp

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bookskotlin.bookapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    //    view binding
    private lateinit var binding: ActivityRegisterBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    //progress dialog
    private lateinit var progressDialog:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false);
//        handle back button click,
        binding.backBtn.setOnClickListener{
            onBackPressed()
//            go to previous screen
        }
//        handle click,begin register
        binding.registerBtn.setOnClickListener {
            /*Steps
            * 1.Input Data
            * 2.ValidateData
            * 3.Create Account
            * 4. Save User Info*/
            validateData();

         }
    }

    private fun validateData() {
        TODO("Not yet implemented")
    }
}