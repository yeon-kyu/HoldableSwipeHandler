package com.yeonkyu.holdableswipehandler.ui.paging3_recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import com.yeonkyu.holdableswipehandler.PlayerViewHolder
import com.yeonkyu.holdableswipehandler.R
import com.yeonkyu.holdableswipehandler.data.Player
import com.yeonkyu.holdableswipehandler.databinding.ItemPlayerBinding
import com.yeonkyu.holdableswipehandler.util.PlayerDiffCallback

class MyPagingAdapter : PagingDataAdapter<Player, PlayerViewHolder>(PlayerDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding: ItemPlayerBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_player,
                parent,
                false)

        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it)
        }
    }

    fun getPlayer(position: Int) = getItem(position) as Player
}