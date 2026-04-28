package com.example.myapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.PayrollSummaryItem
import java.util.*

class PayrollSummaryAdapter(
    private val items: List<PayrollSummaryItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<PayrollSummaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMonthYear: TextView = view.findViewById(R.id.tvMonthYear)
        val btnDownload: ImageView = view.findViewById(R.id.btnDownload)
        val cardView: View = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payroll_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        val monthName = getMonthName(item.month ?: 0)
        holder.tvMonthYear.text = "$monthName ${item.year ?: ""}"
        
        holder.btnDownload.setOnClickListener {
            onItemClick(item.empSalaryId)
        }
        
        holder.cardView.setOnClickListener {
            onItemClick(item.empSalaryId)
        }
    }

    override fun getItemCount() = items.size

    private fun getMonthName(month: Int): String {
        if (month < 1 || month > 12) return ""
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month - 1)
        val name = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) ?: ""
        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
