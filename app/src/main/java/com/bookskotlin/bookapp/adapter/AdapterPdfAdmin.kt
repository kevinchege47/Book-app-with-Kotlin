package com.bookskotlin.bookapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bookskotlin.bookapp.FilterPdfAdmin
import com.bookskotlin.bookapp.MyApplication
import com.bookskotlin.bookapp.databinding.RowPdfAdminBinding
import com.bookskotlin.bookapp.models.ModelPdf

class AdapterPdfAdmin:RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>,Filterable {
//    context
    private var context:Context
//    arrayList to hold pdfs
    public var pdfArrayList: ArrayList<ModelPdf>
    private val filterList:ArrayList<ModelPdf>
    //    view Binding
    private lateinit var  binding:RowPdfAdminBinding
//    filter object
    private var filter: FilterPdfAdmin? = null
//    constructor
constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
    this.context = context
    this.pdfArrayList = pdfArrayList
    this.filterList = pdfArrayList
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
//        inflate layout
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderPdfAdmin(binding.root)
    }

    override fun getItemCount(): Int {
//        items count
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
//        Get data, set data, hanle click
//        get data
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp
//        convert timestamp to dd/MM/yyyy format
        val formattedDate = MyApplication.formatTimeStamp(timestamp)
//        set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate

//        load further details like category, pdf from url, pdf size
//        category id
        MyApplication.loadCategory(categoryId,holder.categoryTv)
//        we dont need page number here, pass null for page number //load pdf thumbnail
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl,title,holder.pdfView,holder.progressBar,null)
//        load pdf size
        MyApplication.loadPdfSize(pdfUrl, title,holder.sizeTv)
    }

    override fun getFilter(): Filter {
        if (filter ==null){
            filter = FilterPdfAdmin(filterList,this)
        }
        return filter as FilterPdfAdmin
    }
    //    view holder class for row_pdf_admin.xml
    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView){
        //        UI views os row_pdf_admin.xml
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val categoryTv = binding.categoryTv
        val moreBtn = binding.moreBtn

    }
}