package com.example.quizzers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quizzers.LeaderboardListAdapter.LeaderboardViewHolder
import com.example.quizzers.databinding.LeaderboardItemLayoutBinding
import com.example.quizzers.network.models.LeaderboardResponseModelItem

class LeaderboardListAdapter() :
    ListAdapter<LeaderboardResponseModelItem, LeaderboardViewHolder>(DiffCallback) {

    class LeaderboardViewHolder(
        val context: Context,
        private var binding: LeaderboardItemLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LeaderboardResponseModelItem) {
            if (item.userprofile != null)
                Glide.with(binding.userIV).load(item.userprofile.display_picture)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(binding.userIV)

            binding.usernameTV.text = if (context.getString(R.string.Username,
                    item.first_name,
                    item.last_name) == " "
            ) "Quiz Master" else context.getString(R.string.Username,
                item.first_name,
                item.last_name)

            binding.userScoreTV.text = item.total_score.toString()

            when (adapterPosition) {
                0 -> with(binding) {
                    positionIV.setImageResource(R.drawable.gold_medal)
                    positionTV.visibility = View.GONE
                }
                1 -> with(binding) {
                    positionIV.setImageResource(R.drawable.silver_medal)
                    positionTV.visibility = View.GONE
                }
                2 -> with(binding) {
                    positionIV.setImageResource(R.drawable.bronze_medal)
                    positionTV.visibility = View.GONE
                }
                else -> with(binding) {
                    positionIV.visibility = View.INVISIBLE
                    positionTV.text = (adapterPosition + 1).toString()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val viewHolder = LeaderboardViewHolder(parent.context, LeaderboardItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
        //we can set clicks n animation here on viewHolder.itemView
        return viewHolder
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LeaderboardResponseModelItem>() {
            override fun areItemsTheSame(
                oldItem: LeaderboardResponseModelItem,
                newItem: LeaderboardResponseModelItem,
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: LeaderboardResponseModelItem,
                newItem: LeaderboardResponseModelItem,
            ): Boolean {
                return false//oldItem.id == newItem.id
            }
        }
    }
}