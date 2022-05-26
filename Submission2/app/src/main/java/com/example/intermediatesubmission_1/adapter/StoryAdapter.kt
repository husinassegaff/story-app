package com.example.intermediatesubmission_1.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Intent
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.intermediatesubmission_1.R
import com.example.intermediatesubmission_1.databinding.ItemStoryBinding
import com.example.intermediatesubmission_1.response.ListStoryItem
import com.example.intermediatesubmission_1.story.DetailStoryActivity

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context),
        parent,
        false)
        return MyViewHolder(binding)
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }

    }

    class MyViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ListStoryItem) {
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .apply(RequestOptions().override(360, 150))
                .into(binding.imgItemStory)
            binding.nameStory.text = data.name
            binding.dateStory.text = data.createdAt.substringBefore("T"," ")

            val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_story)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("story", data)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        imgPhoto,
                        "image"
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }


}

