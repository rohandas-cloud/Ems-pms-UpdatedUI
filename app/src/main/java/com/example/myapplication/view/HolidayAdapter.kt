package com.example.myapplication.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.Holiday

class HolidayAdapter(private var holidayList: List<Holiday>) :
    RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder>() {

    class HolidayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvDayNum: TextView = itemView.findViewById(R.id.tvDayNum)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDayName: TextView = itemView.findViewById(R.id.tvDayName)
        
        // Compatibility views
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_holiday, parent, false)
        return HolidayViewHolder(view)
    }

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        val holiday = holidayList[position]
        
        // Parse date (Assuming YYYY-MM-DD or similar)
        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val dateObj = sdf.parse(holiday.date)
            dateObj?.let {
                holder.tvMonth.text = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault()).format(it)
                holder.tvDayNum.text = java.text.SimpleDateFormat("dd", java.util.Locale.getDefault()).format(it)
                holder.tvDayName.text = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()).format(it)
            }
        } catch (e: Exception) {
            holder.tvMonth.text = ""
            holder.tvDayNum.text = ""
            holder.tvDayName.text = holiday.date
        }

        holder.tvName.text = holiday.name
        
        // Compatibility binding
        holder.tvDate.text = holiday.date
        holder.tvLocation.text = holiday.location
        holder.tvType.text = holiday.type
    }

    override fun getItemCount(): Int = holidayList.size

    fun updateList(newList: List<Holiday>) {
        holidayList = newList
        notifyDataSetChanged()
    }
}