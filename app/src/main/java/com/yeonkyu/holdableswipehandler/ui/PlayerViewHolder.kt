package com.yeonkyu.holdableswipehandler.ui

import androidx.recyclerview.widget.RecyclerView
import com.yeonkyu.holdableswipehandler.data.Player
import com.yeonkyu.holdableswipehandler.databinding.ItemPlayerBinding

class PlayerViewHolder(
    private val binding: ItemPlayerBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(player: Player) {
        binding.playerItem = player
    }
}