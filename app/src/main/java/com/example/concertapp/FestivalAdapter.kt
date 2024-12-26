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

    // ViewHolder sınıfı
    inner class FestivalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val festivalTitle: TextView = itemView.findViewById(R.id.festivalTitle)
        val festivalStage: TextView = itemView.findViewById(R.id.festivalStage) // Sahne adı
        val festivalSinger: TextView = itemView.findViewById(R.id.festivalSinger) // Şarkıcı adı
        val festivalTime: TextView = itemView.findViewById(R.id.festivalTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_festival, parent, false)
        return FestivalViewHolder(view)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivalList[position]

        // Verileri bağlama
        holder.festivalTitle.text = festival.dataTitle ?: "No Title"
        holder.festivalStage.text = festival.dataStage ?: "No Stage"
        holder.festivalSinger.text = festival.dataSinger ?: "No Singer"
        holder.festivalTime.text = festival.dataTime ?: "No Time"

        // Tıklama olayını ayarla
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(festival)
        }
    }

    override fun getItemCount(): Int = festivalList.size
}