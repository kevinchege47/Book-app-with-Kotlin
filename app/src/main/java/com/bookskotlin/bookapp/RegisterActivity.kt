package com.bookskotlin.bookapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.bookskotlin.bookapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    //    view binding
    private lateinit var binding: ActivityRegisterBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog
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
        binding.backBtn.setOnClickListener {
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

    private var name = ""
    private var email = ""
    private var password = ""
    private fun validateData() {
//        Input Data
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.confirmPasswordEt.text.toString().trim()
//        validate data
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            invalid email
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
//            empty password
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
        } else if (cPassword.isEmpty()) {
//            empty password
            Toast.makeText(this, "Confirm Password", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            Toast.makeText(this, "Passwords dont match", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
//        create user account
        progressDialog.setMessage("Creating Account")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
//                account created
                updateUserInfo()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Create Account ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun updateUserInfo() {
//        save user info
        progressDialog.setMessage("Saving User Info")
        val timeStamp = System.currentTimeMillis()
//        get current user ID, since user is registered we can get it now
        val uid = firebaseAuth.uid
//        setup data
        val hashMap:HashMap<String,Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timeStamp"] = timeStamp
//        set data to DB
        val ref:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        uid?.let {
            ref.child(it).setValue(hashMap)
                .addOnSuccessListener {
//                    user info saved successful
                    progressDialog.dismiss()
                    Toast.makeText(this, "User Info saved Successfull", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity,DashboardUserActivity::class.java))
                    finish()

                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to Save User Data due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }
}