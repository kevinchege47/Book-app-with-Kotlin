package com.bookskotlin.bookapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bookskotlin.bookapp.databinding.RowCategoryBinding

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.HolderCategory> {

    private val context: Context
    private val categoryArrayList: ArrayList<ModelCategory>
    private lateinit var binding: RowCategoryBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
//        inflate bind row_category.xml
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderCategory(binding.root)
    }

    override fun getItemCount(): Int {
        return  categoryArrayList.size
//        number of items in List

    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        /*
        * */
    }

    //    ViewHolder class to hold/init UI views for row category.xml
    inner class HolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        init ui views
        var categoryTv: TextView = binding.categoryTv
        var deleteBtn: ImageButton = binding.deleteBtn
    }


}