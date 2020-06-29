package com.tianshaokai.studyuiapp.bean

import android.os.Parcel
import android.os.Parcelable

data class StudyEntity(var title: String?, var content: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StudyEntity> {
        override fun createFromParcel(parcel: Parcel): StudyEntity {
            return StudyEntity(parcel)
        }

        override fun newArray(size: Int): Array<StudyEntity?> {
            return arrayOfNulls(size)
        }
    }


}