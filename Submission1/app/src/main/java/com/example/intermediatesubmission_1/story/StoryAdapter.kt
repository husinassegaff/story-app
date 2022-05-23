package com.example.intermediatesubmission_1.story

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.intermediatesubmission_1.databinding.ItemStoryBinding
import com.example.intermediatesubmission_1.response.ListStoryItem

class StoryAdapter(private val listStory: List<ListStoryItem>): RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = listStory[position]
        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .apply(RequestOptions().override(360, 150))
            .into(holder.binding.imgItemStory)
        holder.binding.nameStory.text = story.name
        holder.binding.dateStory.text = story.createdAt.substringBefore("T"," ")

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, DetailStoryActivity::class.java)
            intent.putExtra("story", story)

            it.context.startActivity(intent)
        }
    }

    override fun getItemCount() = listStory.size

    class ListViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

}