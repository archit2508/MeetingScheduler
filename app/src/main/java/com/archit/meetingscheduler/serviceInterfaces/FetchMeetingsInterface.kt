package com.archit.meetingscheduler.serviceInterfaces

import com.archit.meetingscheduler.model.MeetingInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchMeetingsInterface{

    @GET("api/schedule")
    fun fetchMeetings(@Query("date") date:String?): Call<List<MeetingInfo>>
}