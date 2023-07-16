package com.quizzers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizzers.databinding.CategoryItemLayoutBinding


class CategoryListAdapter(private val onClicked: (Int) -> Unit) :
    ListAdapter<String, CategoryListAdapter.CategoryViewHolder>(DiffCallback) {

    class CategoryViewHolder(
        val context: Context,
        private var binding: CategoryItemLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, onClicked: (Int) -> Unit) {
            binding.categoryLabel.text = item
            binding.categoryIcon.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.icon_float_anim
                )
            )
            var uri = "@drawable/catg_img" // where myresource (without the extension) is the file
            var catId = String.format("%02d", adapterPosition)
            uri += catId
            val imageResource: Int = context.resources.getIdentifier(uri, null, context.packageName)
            binding.categoryIcon.setImageResource(imageResource)
            binding.root.setOnClickListener {
                val position = adapterPosition
                onClicked(position)
                it.setBackgroundColor(context.getColor(R.color.colorScheme3))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val viewHolder = CategoryViewHolder(parent.context, CategoryItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))

        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), onClicked)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String,
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String,
            ): Boolean {
                return false//oldItem.id == newItem.id
            }
        }
    }
}