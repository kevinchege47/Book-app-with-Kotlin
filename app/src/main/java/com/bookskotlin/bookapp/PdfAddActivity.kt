package com.bookskotlin.bookapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bookskotlin.bookapp.databinding.ActivityPdfAddBinding
import com.bookskotlin.bookapp.models.ModelCategory
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfAddActivity : AppCompatActivity() {
//    setup view binding
    private lateinit var binding:ActivityPdfAddBinding
//    firebase auth
    private lateinit var firebaseAuth:FirebaseAuth
//    progress dialog
    private lateinit var progressDialog:ProgressDialog
//    arrayList to hold pdf categories
    private lateinit var categoryArrayList:ArrayList<ModelCategory>
//    uri of picked pdf
    private var pdfUri: Uri? = null
//    TAG
    private val TAG = "PDF_ADD_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
//    init firebase auth
    firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()

//    setup progress dialog
    progressDialog = ProgressDialog(this);
    progressDialog.setTitle("Please wait")
    progressDialog.setCanceledOnTouchOutside(false)
//        handle click,go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
//        handle click, show category pick dialog
        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }
//        handle click,pick pdf intent
        binding.attachPdfBtn.setOnClickListener {
            pdfPickIntent()
        }
//        handle click,start uploading pdf/book
        binding.submitBtn.setOnClickListener {
            /*1.Validate data
            * 2.upload pdf to firebase storage
            * 3.Get url of uploaded pdf
            * 4.Upload pdf info to firebase db*/
            validateData()
        }
    }
    private var title=""
    private var description=""
    private var category=""

    private fun validateData() {
//        Step 1 validate data
        Log.d(TAG, "validateData: validating data")
//        get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()
//        validate data
        if (title.isEmpty()){
            Toast.makeText(this,"Title cannot be empty",Toast.LENGTH_SHORT).show()
        }else if(description.isEmpty()){
            Toast.makeText(this,"Description cannot be empty",Toast.LENGTH_SHORT).show()
        }
        else if(category.isEmpty()){
            Toast.makeText(this,"Category cannot be empty",Toast.LENGTH_SHORT).show()
        }
        else if (pdfUri==null){
            Toast.makeText(this,"Pick PDF",Toast.LENGTH_SHORT).show()
        }
        else{
//            data validated, bedin upload
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
//        upload pdf to firebase storage
        Log.d(TAG, "uploadPdfToStorage: Uploading to storage")
//        show progress dialog
        progressDialog.setMessage("Uploading PDF")
        progressDialog.show()
//        timestamp
        val timestamp = System.currentTimeMillis()
//        path of pdf in firebase storage
        val filePathAndName = "Books/$timestamp"
//        storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {taskSnapshot ->
                Log.d(TAG, "uploadPdfToStorage: PDF uploaded now getting Url")
//                STEP 3 Get url of uploaded pdf
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"

                uploadedPdfInfoToDb(uploadedPdfUrl,timestamp)

            }
            .addOnFailureListener{e->
                Log.d(TAG, "uploadPdfToStorage: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload due to ${e.message} ",Toast.LENGTH_SHORT).show()

            }

    }

    private fun uploadedPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {
//        Upload pdf info to database
        Log.d(TAG, "uploadedPdfInfoToDb: uploading to db")
        progressDialog.setMessage("Uploading pdf info")
//        uid of current user
        val uid = firebaseAuth.uid
        val hashmap:HashMap<String,Any> = HashMap()
        hashmap["uid"] = "$uid"
        hashmap["id"] = "$timestamp"
        hashmap["title"] = "$title"
        hashmap["description"] = "$description"
        hashmap["categoryId"] = "$selectedCategoryId"
        hashmap["url"] = "$uploadedPdfUrl"
        hashmap["timestamp"] = timestamp
        hashmap["viewsCount"] = 0
        hashmap["downloadsCount"] = 0
//        db reference DB>Books>BookId>(Book Info)
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashmap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadedPdfInfoToDb: uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this,"Uploaded Successfully",Toast.LENGTH_SHORT).show()
                pdfUri = null

            }
            .addOnFailureListener {e->
                Log.d(TAG, "uploadPdfToStorage: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload due to ${e.message} ",Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading Pdf Categories")
//        init arrayList
        categoryArrayList = ArrayList()
//        db Reference
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//                clear List before adding data
                categoryArrayList.clear()
                for (ds in snapshot.children){
//                    get data
                    val model = ds.getValue(ModelCategory::class.java)
//                    add to arrayList
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog(){
        Log.d(TAG, "categoryPickDialog: Showing pdf category pick dialog")
//        get string array of categories from arraylist
        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category
        }
//        alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Caategory")
            .setItems(categoriesArray){dialog,which ->
//                handle item click
//                get the clicked item
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id
//                set category to the text view
                binding.categoryTv.text = selectedCategoryTitle
                Log.d(TAG, "categoryPickDialog: Selected Category ID: $selectedCategoryId")
                Log.d(TAG, "categoryPickDialog: Selected Category ID: $selectedCategoryTitle")
            }
            .show()
    }
    private fun pdfPickIntent(){
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent")
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        PdfAddActivityResultLauncher.launch(intent)
    }
    val PdfAddActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{result ->
            if(result.resultCode == RESULT_OK){
                Log.d(TAG, "PDF picked")
                pdfUri = result.data!!.data
            }else{
                Log.d(TAG, "PDF Pick cancelled")
                Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()

            }


        }
    )
}