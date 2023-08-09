package dev.erwin.todo.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.erwin.todo.core.domain.models.Note
import dev.erwin.todo.databinding.NoteItemBinding
import dev.erwin.todo.presentation.utils.OnItemClickListener
import dev.erwin.todo.presentation.utils.randomRatio
import dev.erwin.todo.presentation.utils.withDateFormat
import javax.inject.Inject

class NoteAdapter @Inject constructor() : ListAdapter<Note, NoteAdapter.ViewHolder>(DiffCallback) {
    var onItemClickListener: OnItemClickListener = {}

    class ViewHolder(val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note): Unit = with(binding) {
            noteCard.contentDescription = note.title
            title.text = note.title
            lastUpdatedAt.text = note.lastUpdatedAt.withDateFormat()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
        holder.itemView.setOnClickListener {
            onItemClickListener(note.id)
        }
        val widthPixels = holder.itemView.resources.displayMetrics.widthPixels
        val randomRatio = randomRatio
        val itemHeight = widthPixels / randomRatio
        with(holder.binding) {
            title.maxLines = when(randomRatio){
                2 -> 4
                3 -> 2
                else -> title.maxLines
            }
            noteCard.layoutParams.height = itemHeight
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem == newItem
    }
}