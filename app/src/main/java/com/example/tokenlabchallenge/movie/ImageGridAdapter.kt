package com.example.tokenlabchallenge.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tokenlabchallenge.databinding.GridViewItemBinding
import com.example.tokenlabchallenge.database.MovieProperty

//This class implements a RecyclerView ListAdapter which uses Data Binding to present data.
class ImageGridAdapter (private val onClickListener: OnClickListener) : ListAdapter<MovieProperty, ImageGridAdapter.MoviePropertyViewHolder>(DiffCallback) {

    // This class takes the binding variable from the associated GridViewItem, which gives it access to the full MovieProperty information
    class MoviePropertyViewHolder(private var binding: GridViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movieProperty: MovieProperty) {
            binding.property = movieProperty
            binding.executePendingBindings()
        }
    }

    // Creates new RecyclerView item views (invoked by the layout manager).
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MoviePropertyViewHolder {
        return MoviePropertyViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    // Replaces the contents of a view (invoked by the layout manager).
    override fun onBindViewHolder(holder: MoviePropertyViewHolder, position: Int) {
        val movieProperty = getItem(position)
        holder.bind(movieProperty)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(movieProperty)
        }
    }

    // This companion object allows the RecyclerView to determine which items have changed when the list of MovieProperty has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<MovieProperty>() {
        override fun areItemsTheSame(oldItem: MovieProperty, newItem: MovieProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MovieProperty, newItem: MovieProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickListener: (movieProperty: MovieProperty) -> Unit) {
        fun onClick(movieProperty:MovieProperty) = clickListener(movieProperty)
    }
}