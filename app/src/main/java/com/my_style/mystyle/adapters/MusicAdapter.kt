package com.my_style.mystyle.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.my_style.mystyle.R
import com.my_style.mystyle.click.MusicClick
import com.my_style.mystyle.models.MusicModel
import java.util.Locale

class MusicAdapter(
    private val items: LiveData<List<MusicModel>>,
    private val click: MusicClick,
    private val context: Context
) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>(), Filterable {

   private var musicList: List<MusicModel> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(it: MusicModel, list: List<MusicModel>, position: Int) = with(itemView) {
            val musicName: TextView = this.findViewById(R.id.txtMusic)
            val musicTime: TextView = this.findViewById(R.id.txtMusicTime)
            val ImageView: ImageView = this.findViewById(R.id.imgMusic)
            musicName.text = it.title
            musicTime.text = formatDuration(it.duration)
            itemView.setOnClickListener { click.onMusicClick(position, list) }

        }

        private fun formatDuration(duration: Long): String {
            val hours = duration / (1000 * 60 * 60)
            val minutes = duration % (1000 * 60 * 60) / (1000 * 60)
            val seconds = duration % (1000 * 60) / 1000
            return if (hours == 0L) String.format("%02d:%02d", minutes, seconds)
            else String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_music_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(musicList[position], musicList, position)

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val query = constraint?.toString()?.trim()?.lowercase(Locale.ROOT) ?: ""
            musicList = if (query.isEmpty()) { items.value.orEmpty() }
            else { items.value.orEmpty().filter { it.title.lowercase(Locale.ROOT).startsWith(query) }}
            return FilterResults().apply {
                values = musicList
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val data = results?.values as List<MusicModel>? ?: emptyList()
            musicList = data
            notifyDataSetChanged()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(musics: LiveData<List<MusicModel>>) {
        musics.observe((context as LifecycleOwner)) { newData ->
            musicList = emptyList()
            musicList = newData
            notifyDataSetChanged()
        }
    }

    init {
        setData(items)
    }
}

