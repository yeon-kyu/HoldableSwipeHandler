package com.yeonkyu.holdableswipehandler.ui.normal_recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.yeonkyu.holdableswipehandler.ui.PlayerViewHolder
import com.yeonkyu.holdableswipehandler.R
import com.yeonkyu.holdableswipehandler.data.Player
import com.yeonkyu.holdableswipehandler.databinding.ItemPlayerBinding

class NormalAdapter : RecyclerView.Adapter<PlayerViewHolder>() {

    private lateinit var playerList : MutableList<Player>

    fun setPlayer(list : List<Player>) {
        playerList = list.toMutableList()
    }

    fun removePlayer(position : Int) {
        playerList.removeAt(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding: ItemPlayerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_player,parent,false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.onBind(playerList[position])
    }

    override fun getItemCount(): Int {
        return playerList.size
    }
}