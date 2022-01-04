package com.yeonkyu.holdableswipehandler.ui.paging3_recyclerview

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yeonkyu.holdableswipehandler.data.Player
import com.yeonkyu.holdableswipehandler.util.DataLoader

class MyPagingSource : PagingSource<Int, Player>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Player> {
        return try {
            val nextPage = params.key ?: 1
            val players = DataLoader.loadPagedPlayer(nextPage)

            LoadResult.Page(
                data = players,
                prevKey = null,
                nextKey = if (players.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(Throwable("Paging Error"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Player>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}