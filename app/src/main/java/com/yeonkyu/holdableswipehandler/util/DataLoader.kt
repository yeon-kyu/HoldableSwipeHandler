package com.yeonkyu.holdableswipehandler.util

import android.content.Context
import android.util.Log
import com.yeonkyu.holdableswipehandler.data.Player
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.util.ArrayList

object DataLoader {
    private var playerList : ArrayList<Player>? = null

    fun initPlayer(context: Context) : ArrayList<Player> {
        playerList?.let {
            return it
        }

        val players = ArrayList<Player>()
        try {
            val inputStreamReader = InputStreamReader(context.assets.open("players.csv"))
            val reader = BufferedReader(inputStreamReader)
            reader.readLine()
            var line: String?
            var st: Array<String>

            while (true) {
                line = reader.readLine()
                if(line == null) {
                    break
                }

                st = line.split(",".toRegex()).toTypedArray()
                val player = Player()
                player.name = st[0]
                player.nationality = st[1]
                player.club = st[4]
                player.rating = st[9]
                player.age = st[14]
                players.add(player)
            }

            playerList = players
        } catch (e: Exception) {
            Log.e("DataInitializer Tag", "initPlayer exception : $e")
        }

        return players
    }

    fun loadPagedPlayer(page: Int): List<Player> {
        val pageSize = 20
        if (playerList == null) {
            throw RuntimeException("PlayerList is null")
        }
        return playerList!!.subList((page - 1) * pageSize, page * pageSize)
    }
}