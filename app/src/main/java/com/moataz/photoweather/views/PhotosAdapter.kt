package com.moataz.photoweather.views

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.moataz.photoweather.databinding.PhotoListItemBinding
import com.moataz.photoweather.utils.ViewUtils
import java.io.File

class PhotosAdapter(
    private val photoList: List<File>,
    private val onItemClicked: (File) -> Unit
) : RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: PhotoListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PhotoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val bitmap = BitmapFactory.decodeFile(photoList[position].absolutePath)
            val rotatedBitmap = ViewUtils.setImageOrientation(bitmap,photoList[position].absolutePath) ?:bitmap
            binding.photoItemImageView.setImageBitmap(rotatedBitmap)
            binding.root.setOnClickListener {
                onItemClicked.invoke(photoList[position])
            }
        }
    }

    override fun getItemCount(): Int = photoList.size
}