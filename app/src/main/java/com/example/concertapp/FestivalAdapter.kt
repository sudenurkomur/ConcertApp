package com.example.concertapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FestivalAdapter(
    private val festivalList: List<DataClass>,
    private val onItemClick: ((DataClass) -> Unit)? = null // Tıklama işlevi
) : RecyclerView.Adapter<FestivalAdapter.FestivalViewHolder>() {

    inner class FestivalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val festivalTitle: TextView = itemView.findViewById(R.id.festivalTitle)
        val festivalDesc: TextView = itemView.findViewById(R.id.festivalDesc)
        val festivalLang: TextView = itemView.findViewById(R.id.festivalLang)
        val festivalDate: TextView = itemView.findViewById(R.id.festivalDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_festival, parent, false)
        return FestivalViewHolder(view)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivalList[position]
        holder.festivalTitle.text = festival.dataTitle ?: "No Title"
        holder.festivalDesc.text = festival.dataDesc ?: "No Description"
        holder.festivalLang.text = festival.dataLang ?: "No Language"
        holder.festivalDate.text = festival.dataTime ?: "No Date"

        // Tıklama olayını tanımlıyoruz
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(festival)
        }
    }

    override fun getItemCount(): Int = festivalList.size
}
