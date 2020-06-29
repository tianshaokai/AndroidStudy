package com.tianshaokai.studyuiapp.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tianshaokai.studyuiapp.R
import com.tianshaokai.studyuiapp.bean.StudyEntity

class Study1Adapter : RecyclerView.Adapter<Study1Adapter.ViewHolder>() {

    private var studyList:ArrayList<StudyEntity>? = null


    init {
        studyList = arrayListOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_study1, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return studyList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studyEntity = studyList?.get(position)
        holder.title.text =  studyEntity?.title
        holder.content.text =  studyEntity?.content
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val content: TextView = itemView.findViewById(R.id.content)
    }

    fun add(list: ArrayList<StudyEntity>) {
        studyList?.addAll(list)
        notifyDataSetChanged()
    }
}