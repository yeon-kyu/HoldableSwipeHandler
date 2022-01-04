package com.yeonkyu.holdableswipehandler.ui.list_adapter_recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.yeonkyu.HoldableSwipeHelper.HoldableSwipeHelper
import com.yeonkyu.HoldableSwipeHelper.SwipeButtonAction
import com.yeonkyu.holdableswipehandler.R
import com.yeonkyu.holdableswipehandler.data.Player
import com.yeonkyu.holdableswipehandler.databinding.FragmentRecyclerviewBinding
import com.yeonkyu.holdableswipehandler.util.DataLoader

class ListAdapterRecyclerViewFragment : Fragment() {

    private lateinit var binding : FragmentRecyclerviewBinding
    private lateinit var adapter : MyListAdapter

    private lateinit var playerList : ArrayList<Player>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_recyclerview, container, false)
        binding.lifecycleOwner = activity

        playerList = DataLoader.initPlayer(requireContext())

        setUpRecyclerView()
        setUpHoldableSwipeHandler()

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = MyListAdapter()
        adapter.submitList(playerList.toList())

        binding.recyclerView.adapter = adapter
    }

    private fun setUpHoldableSwipeHandler() {
        val swipeHelper = HoldableSwipeHelper(requireContext(), object : SwipeButtonAction {
            override fun onClickFirstButton(absoluteAdapterPosition: Int) {
                playerList.removeAt(absoluteAdapterPosition)
                adapter.submitList(playerList.toList()) // .toList()를 빼면 ListAdapter가 변경사항을 캐치할 수 없다
            }
        })

        swipeHelper.addRecyclerViewListener(binding.recyclerView)
        swipeHelper.addRecyclerViewDecoration(binding.recyclerView)
        swipeHelper.setDismissBackgroundOnClickedFirstItem(true)
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }
}