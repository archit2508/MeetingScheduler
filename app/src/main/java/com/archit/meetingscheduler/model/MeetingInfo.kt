package com.archit.meetingscheduler.model
import android.annotation.SuppressLint
import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class MeetingInfo(
    var start_time: String,
    var end_time: String,
    val description: String,
    val participants: List<String>
) : Parcelable