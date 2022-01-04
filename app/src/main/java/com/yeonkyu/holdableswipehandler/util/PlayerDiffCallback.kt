package com.yeonkyu.holdableswipehandler.util

import androidx.recyclerview.widget.DiffUtil
import com.yeonkyu.holdableswipehandler.data.Player

class PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.name == newItem.name
                && oldItem.club == newItem.club
    }
}