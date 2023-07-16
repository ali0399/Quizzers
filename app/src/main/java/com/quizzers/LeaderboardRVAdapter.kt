package com.quizzers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quizzers.databinding.LeaderboardItemLayoutBinding
import com.quizzers.network.models.LeaderboardResponseModelItem

class LeaderboardRVAdapter() :
    RecyclerView.Adapter<LeaderboardRVAdapter.LeaderboardViewHolder>() {

    var list: ArrayList<LeaderboardResponseModelItem> = arrayListOf()

    class LeaderboardViewHolder(
        val context: Context,
        private var binding: LeaderboardItemLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LeaderboardResponseModelItem) {
            if (item.userprofile != null)
                Glide.with(binding.userIV).load(item.userprofile.display_picture)
                    .into(binding.userIV)

            binding.usernameTV.text = if (context.getString(R.string.Username,
                    item.first_name,
                    item.last_name) == " "
            ) "Quiz Master" else context.getString(R.string.Username,
                item.first_name,
                item.last_name)

            binding.userScoreTV.text = item.total_score.toString()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val viewHolder = LeaderboardViewHolder(parent.context, LeaderboardItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
        //we can set clicks n animation here on viewHolder.itemView
        return viewHolder
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}