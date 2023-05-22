package com.bookskotlin.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.bookskotlin.bookapp.adapter.AdapterPdfAdmin
import com.bookskotlin.bookapp.databinding.ActivityPdfListAdminActivityBinding
import com.bookskotlin.bookapp.models.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfListAdminActivity : AppCompatActivity() {
//    view binding
    private lateinit var binding: ActivityPdfListAdminActivityBinding
    private companion object{
        const val TAG = "PDF_LIST_ADMIN"
    }
//    category id,title
    private var categoryId = ""
    private var category = ""
//    arraylist to hold books
    private lateinit var pdfArrayList:ArrayList<ModelPdf>
//    adapter
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListAdminActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        get from intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!
//        set pdf category
        binding.subtitleTv.text = category
//        load books
        loadPdfList()
        //SEARCH
        binding.searchEt.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                filter data
                try {
                    adapterPdfAdmin.filter!!.filter(s)
                }
                catch (e:Exception){
                    Log.d(TAG, "onTextChanged: ${e.message}")

                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun loadPdfList() {
//        init arraylist
        pdfArrayList = ArrayList()
        //adapter
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                    clear list
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
//                        get data
                        val model = ds.getValue(ModelPdf::class.java)
//                        ad to list
                        if (model != null) {
                            pdfArrayList.add(model)
                            Log.d(TAG, "onDataChange:${model.title} ${model.categoryId} ")
                        }
                    }
//                    setup adapter
                    adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity,pdfArrayList)
                    binding.booksRv.adapter = adapterPdfAdmin
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}