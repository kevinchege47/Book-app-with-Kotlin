package com.bookskotlin.bookapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.bookskotlin.bookapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    //    view binding
    private lateinit var binding: ActivityLoginBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //        init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false);
//        handle click, not have account go to register screen
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
//        handle click,begin login
        binding.loginBtn.setOnClickListener {
            /* Steps
            1. Input Data
            2. Validate Data
            3. Login - Firebase Auth
            4. Check User Type
            * */
            validateData()
        }
    }

    private var email = ""
    private var password = ""
    private fun validateData() {
//        input data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email Format", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
        } else {
            loginUser()
        }

    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In")
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
//                login success
                checkUser()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Login Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User")
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
//                    get user type
                    val userType = snapshot.child("userType").value
                    if(userType == "user"){
                        startActivity(Intent(this@LoginActivity,DashboardUserActivity::class.java))
                        finish()
                    }else if(userType == "admin"){
                        startActivity(Intent(this@LoginActivity,DashboardAdminActivity::class.java))
                        finish()
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}