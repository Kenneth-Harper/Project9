package com.example.project9

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.example.project9.model.Image

class ImageDiffItemCallback : DiffUtil.ItemCallback<Uri>()
{
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri) = (oldItem == newItem)
    override fun areContentsTheSame(oldItem: Uri, newItem: Uri) = (oldItem == newItem)
}