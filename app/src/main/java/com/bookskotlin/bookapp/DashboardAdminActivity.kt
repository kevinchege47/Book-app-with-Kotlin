package com.bookskotlin.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.bookskotlin.bookapp.databinding.ActivityDashboardAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardAdminActivity : AppCompatActivity() {
//    view binding
    private lateinit var binding: ActivityDashboardAdminBinding
//    firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
//    arrayList to hold categories
    private lateinit var categoryArrayList:ArrayList<ModelCategory>
//    adapter
    private lateinit var adapterCategory: AdapterCategory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()
        binding.searchEt.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                called when user types anything
                try {
                    adapterCategory.filter.filter(s)
                }
                catch (e:Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("Not yet implemented")
            }
        })
//        logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()

        }
//        handle click, start add category page
        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this@DashboardAdminActivity,CategoryAddActivity::class.java))

        }
    }

    private fun loadCategories() {
//        init array list
        categoryArrayList = ArrayList()
//        get all categories from Database
        val Ref = FirebaseDatabase.getInstance().getReference("Categories")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                clear list
                for (ds in snapshot.children){
//                    get data as model
                    val model = ds.getValue(ModelCategory::class.java)
//                    add to arrayList
                    if (model != null) {
                        categoryArrayList.add(model)
                    }
                }
//                setup adapter
                adapterCategory = AdapterCategory(this@DashboardAdminActivity, categoryArrayList )
//                set adapter to recycler view
                binding.categoriesRv.adapter = adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
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