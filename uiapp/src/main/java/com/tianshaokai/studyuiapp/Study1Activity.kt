package com.tianshaokai.studyuiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tianshaokai.studyuiapp.adpater.Study1Adapter
import com.tianshaokai.studyuiapp.bean.StudyEntity
import kotlinx.android.synthetic.main.activity_study1.*

class Study1Activity : AppCompatActivity() {


    private var studyAdapter: Study1Adapter? = null

    companion object {
        fun lanchMode(context: Context) {
            val intent = Intent(context, Study1Activity::class.java);
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study1)
        studyAdapter = Study1Adapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = studyAdapter



        val studyEntityList = arrayListOf<StudyEntity>()

        val studyEntity1 = StudyEntity("Line", "This line is a special")

        studyEntityList.add(studyEntity1)


        studyAdapter?.add(studyEntityList)

    }

}