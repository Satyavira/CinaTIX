package com.garpoo.cinatix.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.garpoo.cinatix.databinding.ItemCastBinding
import com.garpoo.cinatix.data.api.CastMember

class CastAdapter(private val castList: List<CastMember>) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val castMember = castList[position]
        holder.bind(castMember)
    }

    override fun getItemCount(): Int = castList.size

    class CastViewHolder(private val binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(castMember: CastMember) {
            binding.actorName.text = castMember.name
//            binding.characterName.text = castMember.character
            Glide.with(binding.root.context)
                .load("https://image.tmdb.org/t/p/w500${castMember.profile_path}")
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(60))) // Center crop with rounded corners
                .into(binding.actorImage)
        }
    }
}
