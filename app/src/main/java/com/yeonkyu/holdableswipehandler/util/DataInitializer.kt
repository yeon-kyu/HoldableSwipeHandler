package com.yeonkyu.holdableswipehandler.util

import android.content.Context
import android.util.Log
import com.yeonkyu.holdableswipehandler.data.Player
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

object DataInitializer {
    fun initPlayer(context: Context) : ArrayList<Player> {
        val players = ArrayList<Player>()
        try {
            val `is` = InputStreamReader(context.assets.open("players.csv"))
            val reader = BufferedReader(`is`)
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
        } catch (e: Exception) {
            Log.e("DataInitializer Tag", "initPlayer exception : $e")
        }

        return players
    }
}