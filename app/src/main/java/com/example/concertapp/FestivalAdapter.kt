package com.example.concertapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.concertapp.models.DataClass

class FestivalAdapter(
    private val festivalList: List<DataClass>,
    private val onItemClick: ((DataClass) -> Unit)? = null // Tıklama işlevi
) : RecyclerView.Adapter<FestivalAdapter.FestivalViewHolder>() {

    // ViewHolder sınıfı
    inner class FestivalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val festivalTitle: TextView = itemView.findViewById(R.id.festivalTitle)
        val festivalStage: TextView = itemView.findViewById(R.id.festivalStage)
        val festivalSinger: TextView = itemView.findViewById(R.id.festivalSinger)
        val festivalDate: TextView = itemView.findViewById(R.id.festivalTime)
        val festivalLocation: TextView = itemView.findViewById(R.id.festivalLocation)
        val festivalImage: ImageView = itemView.findViewById(R.id.festivalImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_festival, parent, false)
        return FestivalViewHolder(view)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivalList[position]

        // Verileri bağlama
        holder.festivalTitle.text = festival.title
        holder.festivalStage.text = festival.stage
        holder.festivalSinger.text = festival.singer
        holder.festivalDate.text = festival.date
        holder.festivalLocation.text = "Lat: ${festival.location.latitude}, Lng: ${festival.location.longitude}"

        // Glide kullanarak görseli yükleme
        Glide.with(holder.itemView.context)
            .load(festival.image) // Supabase'den gelen görsel URL
            .placeholder(R.drawable.sample_image) // Görsel yüklenirken geçici resim
            .into(holder.festivalImage)

        // Tıklama olayını tanımlama
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(festival)
        }
    }

    override fun getItemCount(): Int = festivalList.size
}
