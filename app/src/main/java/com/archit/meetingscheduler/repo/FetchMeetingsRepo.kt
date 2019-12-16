package com.archit.meetingscheduler.repo

import androidx.lifecycle.MutableLiveData
import com.archit.meetingscheduler.model.MeetingInfo
import com.archit.meetingscheduler.networkUtils.networkClient.RetrofitClient
import com.archit.meetingscheduler.serviceInterfaces.FetchMeetingsInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FetchMeetingsRepo {

    fun fetchMeetings(date:String?): MutableLiveData<List<MeetingInfo>>{
        val meetingInfoLiveData = MutableLiveData<List<MeetingInfo>>()
        val service = RetrofitClient.getCacheEnabledRetrofit().create(FetchMeetingsInterface::class.java)
        service.fetchMeetings(date).enqueue(object: Callback<List<MeetingInfo>>{
            override fun onFailure(call: Call<List<MeetingInfo>>, t: Throwable) {
                meetingInfoLiveData.postValue(null)
            }

            override fun onResponse(call: Call<List<MeetingInfo>>, response: Response<List<MeetingInfo>>) {
                meetingInfoLiveData.postValue(response.body())
            }
        })
        return meetingInfoLiveData
    }
}