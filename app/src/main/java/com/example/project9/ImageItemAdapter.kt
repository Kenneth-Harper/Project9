package com.example.project9

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project9.databinding.ImageItemBinding
import com.example.project9.model.Image

class ImageItemAdapter (val context: Context, val imageClickListener: (image: Uri) -> Unit) : ListAdapter<Uri, ImageItemAdapter.ImageItemViewHolder>(ImageDiffItemCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ImageItemViewHolder = ImageItemViewHolder.inflateFrom(parent)
    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int)
    {
        val item = getItem(position)
        holder.bind(item, context, imageClickListener)
    }

    class ImageItemViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object
        {
            fun inflateFrom(parent: ViewGroup) : ImageItemViewHolder
            {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageItemBinding.inflate(layoutInflater, parent, false)
                return ImageItemViewHolder(binding)
            }
        }

        fun bind(item: Uri, context: Context, imageClickListener: (image: Uri) -> Unit)
        {
            binding.uri = item
            Glide.with(context).load(item).into(binding.imageview)
            binding.viewImage.setOnClickListener{ imageClickListener(item) }

        }
    }
}
