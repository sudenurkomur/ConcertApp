package com.example.concertapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.concertapp.models.Festival

class FestivalAdapter(
    private var festivalList: List<Festival>,
    private val onItemClick: ((Festival) -> Unit)? = null
) : RecyclerView.Adapter<FestivalAdapter.FestivalViewHolder>() {

    inner class FestivalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val festivalName: TextView = itemView.findViewById(R.id.festivalTitle)
        val festivalDescription: TextView = itemView.findViewById(R.id.festivalDescription)
        val festivalDates: TextView = itemView.findViewById(R.id.festivalDates)
    }

    fun updateList(newList: List<Festival>) {
        festivalList = newList
        notifyDataSetChanged() // RecyclerView'i günceller
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_festival, parent, false)
        return FestivalViewHolder(view)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivalList[position]
        holder.festivalName.text = festival.name
        holder.festivalDescription.text = festival.desc
        holder.festivalDates.text = "${festival.start_date} - ${festival.end_date}"

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(festival)
        }
    }

    override fun getItemCount(): Int = festivalList.size
}