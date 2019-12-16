package com.archit.meetingscheduler.activity

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.archit.meetingscheduler.R
import com.archit.meetingscheduler.model.MeetingInfo
import com.archit.meetingscheduler.viewModel.MeetingSchedulerViewModel
import kotlinx.android.synthetic.main.activity_schedule_meeting_form.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_schedule_meeting_form.meetingDate
import kotlinx.android.synthetic.main.custom_action_bar2.*


class ScheduleMeetingFormActivity : AppCompatActivity() {

    private var meetingSchedulerViewModel: MeetingSchedulerViewModel? = null

    lateinit var myCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meeting_form)
        meetingSchedulerViewModel = ViewModelProviders.of(this).get(MeetingSchedulerViewModel::class.java)
        setCustomBar()

        if(intent.extras?.get("DATE").toString().isNotEmpty()){
            meetingDate.setText(intent.extras?.get("DATE").toString())
        }

        prevParentLayout.setOnClickListener {
            onBackPressed()
        }

        setClickListenerForMeetingDate()
        setClickListenerForStartTime()
        setClickListenerForEndTime()
        setClickListenerForSubmit()
    }

    /**
     * handles click on submit button
     * this button will first validate the inputs and then check for slot availability
     */
    private fun setClickListenerForSubmit() {
        scheduleMeetingButton.setOnClickListener {
            if(validateInputs())
                checkSlotAvailability()
        }
    }

    /**
     * validates the input given user for meeting date, start time and end time
     */
    private fun validateInputs(): Boolean{
        val formatter = SimpleDateFormat("K:mm a")
        if(meetingDate.text.isEmpty() || startTime.text.isEmpty() || endTime.text.isEmpty()){
            showAlert("Error","Please fill all fields")
            return false
        }
        if(formatter.parse(endTime.text.toString())<=formatter.parse(startTime.text.toString())) {
            showAlert("Error", "Meeting ending time cannot be behind Start Time")
            return false
        }
        return true
    }

    /**
     * checks slot availability
     * it firstly fetches schedule for date entered by user and then checks slot in that schedule
     */
    private fun checkSlotAvailability(){
        showProgressBar()
        fetchScheduleAndValidateSlot(meetingDate.text.toString())
    }

    /**
     * handles API hit required to fetch meeting schedule for given date
     */
    private fun fetchScheduleAndValidateSlot(date: String?) {
        date.let {
            meetingSchedulerViewModel?.fetchMeetings(it)?.observe(this, Observer {
                hideProgressBar()
                if(it != null){
                    if(it!=null){
                        if(it.isEmpty())
                            showAlert("Slot Availability","Yay!! Slot is available.")
                        else{
                            validateSlot(it as ArrayList<MeetingInfo>)
                        }
                    }
                }
                else{
                    hideProgressBar()
                    Toast.makeText(this, "Couldn't fetch data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    /**
     * validates slot input by user in data received in schedule from api
     */
    private fun validateSlot(meetingList: java.util.ArrayList<MeetingInfo>) {
        val formatter1 = SimpleDateFormat("H:mm")
        val formatter2 = SimpleDateFormat("K:mm a")
        var startTimeAvailable = true
        var endTimeAvailable = true
        meetingList.forEach {
            if(formatter2.parse(startTime.text.toString())>=formatter1.parse(it.start_time) &&
                formatter2.parse(startTime.text.toString())<formatter1.parse(it.end_time))
                startTimeAvailable = false
            if(formatter2.parse(endTime.text.toString())>formatter1.parse(it.start_time) &&
                formatter2.parse(endTime.text.toString())<=formatter1.parse(it.end_time))
                endTimeAvailable = false
        }
        if(startTimeAvailable && endTimeAvailable)
            showAlert("Slot Availability","Yay!! Slot is available.")
        else
            showAlert("Slot Availability","Sorry, Slot is not available.")
    }

    private fun showAlert(title: String, msg: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(android.R.string.ok,null)
            .show()
    }

    private fun hideProgressBar() {
        progressbar.visibility = View.GONE
    }

    private fun showProgressBar() {
        progressbar.visibility = View.VISIBLE
    }

    private fun setClickListenerForEndTime() {
        endTime.setOnClickListener {
            setEndTime()
        }
    }

    /**
     * initializes and shows time picker dialog
     */
    private fun setEndTime() {
        myCalendar = Calendar.getInstance()
        val timeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            updateEndTime()
        }
        showTimePickerDialog(timeListener)
    }

    /**
     * updates end time field after time selected in timer
     */
    private fun updateEndTime() {
        val format = "hh:mm aa"
        val sdf = SimpleDateFormat(format, Locale.US)
        endTime.setText(sdf.format(myCalendar.time))
    }

    private fun setClickListenerForStartTime() {
        startTime.setOnClickListener {
            setStartTime()
        }
    }

    private fun setStartTime() {
        myCalendar = Calendar.getInstance()
        val timeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            updateStartTime()
        }
        showTimePickerDialog(timeListener)
    }

    private fun updateStartTime() {
        val format = "hh:mm aa"
        val sdf = SimpleDateFormat(format, Locale.US)
        startTime.setText(sdf.format(myCalendar.time))
    }

    private fun showTimePickerDialog(timeListener: TimePickerDialog.OnTimeSetListener) {
        val timePickerDialog = TimePickerDialog(
            this,
            timeListener,
            myCalendar.get(Calendar.HOUR),
            myCalendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun setClickListenerForMeetingDate() {
        meetingDate.setOnClickListener{
            setDate()
        }
    }

    private fun setDate() {
        myCalendar = Calendar.getInstance()
        val dateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(year, month, dayOfMonth)
            updateMeetingDate()
        }
        val datePickerDialog = DatePickerDialog(
            this,
            dateListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun updateMeetingDate() {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.US)
        meetingDate.setText(sdf.format(myCalendar.time))
    }

    private fun setCustomBar() {
        supportActionBar.let {
            val inflator = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customViewForActionbar = inflator.inflate(R.layout.custom_action_bar2,null)
            it?.setDisplayShowCustomEnabled(true)
            it?.setCustomView(customViewForActionbar, ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            it?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

            val toolbar = customViewForActionbar.parent as Toolbar
            toolbar.setContentInsetsAbsolute(0,0)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
