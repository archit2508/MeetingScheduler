package com.archit.meetingscheduler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.archit.meetingscheduler.R
import com.archit.meetingscheduler.model.MeetingInfo
import kotlinx.android.synthetic.main.meeting_info_item.view.*

class MeetingInfoAdapter(private val meetingslist: ArrayList<MeetingInfo>) :
    RecyclerView.Adapter<MeetingInfoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingInfoHolder =
        MeetingInfoHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.meeting_info_item, parent, false)
        )

    override fun getItemCount(): Int = meetingslist.size

    override fun onBindViewHolder(holder: MeetingInfoHolder, position: Int) {
        val meetingInfo = meetingslist.get(position)
        if(meetingInfo!=null){
            holder.itemView.meetingSlot.text = meetingInfo.start_time + " - " + meetingInfo.end_time
            holder.itemView.meetingDescription.text = meetingInfo.description
        }
    }
}

class MeetingInfoHolder(itemView: View) : RecyclerView.ViewHolder(itemView)