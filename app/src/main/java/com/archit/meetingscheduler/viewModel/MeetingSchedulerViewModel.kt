package com.archit.meetingscheduler.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.archit.meetingscheduler.model.MeetingInfo
import com.archit.meetingscheduler.repo.FetchMeetingsRepo

class MeetingSchedulerViewModel: ViewModel() {

    private var responseLiveData = MutableLiveData<List<MeetingInfo>>()
    private val fetchMeetingsRepo = FetchMeetingsRepo()

    fun fetchMeetings(date:String?): MutableLiveData<List<MeetingInfo>>{
        responseLiveData = fetchMeetingsRepo.fetchMeetings(date)
        return responseLiveData
    }

}