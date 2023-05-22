package com.bookskotlin.bookapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bookskotlin.bookapp.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable {

    private val context: Context
    public var categoryArrayList: ArrayList<ModelCategory>
    private var filterList: ArrayList<ModelCategory>
    private var filter: FilterCategory? = null
    private lateinit var binding: RowCategoryBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
//        inflate bind row_category.xml
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategory(binding.root)
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
//        number of items in List

    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        /* Get Data
        Set Data
        Handle Clicks
        * */
        val model = categoryArrayList.get(position)
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp
//        set data
        holder.categoryTv.text = category
//        handle click,delete category
        holder.deleteBtn.setOnClickListener {
//            confirm before delete
            val builder = AlertDialog.Builder(context)
            builder
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this $category ?")
                .setPositiveButton("Confirm") { a, d ->
                    Toast.makeText(context, "Deleting", Toast.LENGTH_SHORT).show()
                    deleteCategory(model, holder);
                }
                .setNegativeButton("Cancel") { a, d ->
                    a.dismiss()

                }
                .show()
        }
//        handle click,start pdf list admin activity, also pass pdf id,title
        holder.itemView.setOnClickListener {
            val intent = Intent(context,PdfListAdminActivity::class.java)
            intent.putExtra("categoryId",id)
            intent.putExtra("category",category)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
//        get id of category
        val id = model.id
        val Ref = FirebaseDatabase.getInstance().getReference("Categories")
        Ref.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //    ViewHolder class to hold/init UI views for row category.xml
    inner class HolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        init ui views
        var categoryTv: TextView = binding.categoryTv
        var deleteBtn: ImageButton = binding.deleteBtn
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterCategory(filterList,this)
        }
        return filter as FilterCategory
    }


}