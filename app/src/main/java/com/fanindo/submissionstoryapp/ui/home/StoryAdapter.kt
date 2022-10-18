package com.fanindo.submissionstoryapp.ui.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fanindo.submissionstoryapp.R
import com.fanindo.submissionstoryapp.data.local.entity.Story
import com.fanindo.submissionstoryapp.databinding.ItemStoryBinding
import com.fanindo.submissionstoryapp.ui.detail.DetailStoryActivity
import com.fanindo.submissionstoryapp.ui.detail.DetailStoryActivity.Companion.EXTRA_DATA

class StoryAdapter :
    PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemStoryBinding.bind(itemView)
        fun bind(story: Story) {
            with(binding) {
                tvName.text = story.name
                tvDesc.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.image_loading_placeholder)
                    .error(R.drawable.image_load_error)
                    .into(ivStory)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(binding.ivStory, "picture"),
                            Pair(binding.tvName, "name"),
                            Pair(binding.tvDesc, "description")
                        )

                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(EXTRA_DATA, story)
                    itemView.context.startActivity(intent, optionsCompat.toBundle())

                }
            }
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }


}