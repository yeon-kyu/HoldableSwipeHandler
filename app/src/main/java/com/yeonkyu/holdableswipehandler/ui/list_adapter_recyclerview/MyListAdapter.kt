package com.yeonkyu.holdableswipehandler.ui.list_adapter_recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.yeonkyu.holdableswipehandler.ui.PlayerViewHolder
import com.yeonkyu.holdableswipehandler.R
import com.yeonkyu.holdableswipehandler.data.Player
import com.yeonkyu.holdableswipehandler.databinding.ItemPlayerBinding
import com.yeonkyu.holdableswipehandler.util.PlayerDiffCallback

class MyListAdapter(

): ListAdapter<Player, PlayerViewHolder>(
    PlayerDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding: ItemPlayerBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_player,
                parent,
                false)

        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}