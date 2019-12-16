package com.archit.meetingscheduler.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.archit.meetingscheduler.R
import com.archit.meetingscheduler.adapter.MeetingInfoAdapter
import com.archit.meetingscheduler.model.MeetingInfo
import com.archit.meetingscheduler.networkUtils.NetworkUtils
import com.archit.meetingscheduler.viewModel.MeetingSchedulerViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_action_bar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var meetingSchedulerViewModel: MeetingSchedulerViewModel? = null
    private var meetingList: ArrayList<MeetingInfo> = arrayListOf()
    private var meetingInfoAdapter: MeetingInfoAdapter? = null
    private var currDate: String? = null

    companion object{
        lateinit var mInstance: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mInstance = this
        meetingSchedulerViewModel = ViewModelProviders.of(this).get(MeetingSchedulerViewModel::class.java)

        setCustomBar()

        currDate = getCurrentDate()
        meetingDate.text = currDate

        rv.layoutManager = LinearLayoutManager(this)
        meetingInfoAdapter = MeetingInfoAdapter(meetingList)
        rv.adapter = meetingInfoAdapter

        fetchScheduleForCurrentDate(currDate)

        setClickListenerForPrev()
        setClickListenerForNext()
        setOnclickListenerForScheduleMeeting()
    }

    /**
     * handles click on schedule meeting button which navigates to schedule meeting form
     */
    private fun setOnclickListenerForScheduleMeeting() {
        scheduleMeetingButton.setOnClickListener {
            val intent = Intent(this, ScheduleMeetingFormActivity::class.java)
            intent.putExtra("DATE",currDate)
            startActivity(intent)
        }
    }

    /**
     * handles click on next> button, displays schedule for next date
     */
    private fun setClickListenerForNext() {
        nextParentLayout.setOnClickListener {
            incrementCurrentDate()
            fetchSchedule(currDate)
            meetingDate.text = currDate
        }
    }

    /**
     * increments current date
     */
    private fun incrementCurrentDate() {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.US)
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(currDate)
        cal.add(Calendar.DATE, 1) //Adds a day
        currDate = sdf.format(cal.time)
    }

    /**
     * handles click on <prev button in toolbar which will display schedule for previous date
     */
    private fun setClickListenerForPrev() {
        prevParentLayout.setOnClickListener {
            decrementCurrentDate()
            fetchSchedule(currDate)
            meetingDate.text = currDate
        }
    }

    /**
     * decrements current date to previous date
     */
    private fun decrementCurrentDate() {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.US)
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(currDate)
        cal.add(Calendar.DATE, -1) //Adds a day
        currDate = sdf.format(cal.time)
    }

    /**
     * sets toolbar to custom created
     */
    private fun setCustomBar() {
        supportActionBar.let {
            val inflator = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customViewForActionbar = inflator.inflate(R.layout.custom_action_bar,null)
            it?.setDisplayShowCustomEnabled(true)
            it?.setCustomView(customViewForActionbar, ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            it?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

            val toolbar = customViewForActionbar.parent as Toolbar
            toolbar.setContentInsetsAbsolute(0,0)
        }
    }

    /**
     * gets current Date
     */
    private fun getCurrentDate(): String {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.US)
        return sdf.format(Calendar.getInstance().time)
    }

    /**
     * fetch schedule for current Date by hitting API
     */
    private fun fetchScheduleForCurrentDate(currDate: String?) {
        fetchSchedule(currDate)
    }

    /**
     * API hit which takes date as argument to fetch schedule for the same
     * Refreshes the view when response received
     */
    private fun fetchSchedule(date: String?) {
        showProgressBar()
        date.let {
            meetingSchedulerViewModel?.fetchMeetings(it)?.observe(this, Observer {
                if(it != null){
                    hideProgressBar()
                    if(it.isEmpty()){
                        Toast.makeText(this, "No Meetings Scheduled", Toast.LENGTH_SHORT).show()
                    }
                    sortListAccToTime(it)
                    convertTimeFormatInResponse(it)
                    meetingList.clear()
                    meetingList.addAll(it)
                    meetingInfoAdapter?.notifyDataSetChanged()
                }
                else{
                    hideProgressBar()
                    Toast.makeText(this, "Couldn't fetch data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    /**
     * this method sorts the response list of meetings according to start time
     */
    private fun sortListAccToTime(list: List<MeetingInfo>?) {
        val formatter = SimpleDateFormat("H:mm")
        Collections.sort(list, kotlin.Comparator { slot1, slot2 ->
            formatter.parse(slot1.start_time).compareTo(formatter.parse(slot2.start_time)) })
    }

    /**
     * converts 24 hour format present in response to 12 hour format
     */
    private fun convertTimeFormatInResponse(it: List<MeetingInfo>?) {
        val dateFormatter = SimpleDateFormat("H:mm")
        val dateFormatterWithAmPm = SimpleDateFormat("K:mm a")
        it?.forEach {
            var startTime = it.start_time
            it.start_time = dateFormatterWithAmPm.format(dateFormatter.parse(startTime))
            var endTime = it.end_time
            it.end_time = dateFormatterWithAmPm.format(dateFormatter.parse(endTime))
        }
    }

    private fun hideProgressBar() {
        progress_bar.visibility = View.GONE
        rv.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
        rv.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        NetworkUtils.enableConnectivityManager(this)
    }

    override fun onPause() {
        super.onPause()
        if(NetworkUtils.connectivityManager!=null)
            NetworkUtils.disableConnectivityManager()
    }
}
