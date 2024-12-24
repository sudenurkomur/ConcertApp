package com.example.concertapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FestivalAdapter(private val festivalList: List<DataClass>) : RecyclerView.Adapter<FestivalAdapter.FestivalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_festival, parent, false)
        return FestivalViewHolder(view)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivalList[position]
        holder.bind(festival)
    }

    override fun getItemCount(): Int {
        return festivalList.size
    }

    class FestivalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.festivalTitle)
        private val desc: TextView = itemView.findViewById(R.id.festivalDesc)
        private val lang: TextView = itemView.findViewById(R.id.festivalLang)
        private val date: TextView = itemView.findViewById(R.id.festivalDate)

        fun bind(festival: DataClass) {
            // Güncellenmiş DataClass alanlarıyla bağlama
            title.text = festival.dataTitle
            desc.text = festival.dataDesc
            lang.text = festival.dataLang
            date.text = festival.dataTime
        }
    }
}
