package com.yeonkyu.holdableswipehandler.ui.normal_recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yeonkyu.holdableswipehandler.R
import com.yeonkyu.holdableswipehandler.util.DataLoader
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.yeonkyu.HoldableSwipeHelper.HoldableSwipeHelper
import com.yeonkyu.HoldableSwipeHelper.SwipeButtonAction
import com.yeonkyu.holdableswipehandler.databinding.FragmentRecyclerviewBinding

class NormalRecyclerViewFragment : Fragment() {

    private lateinit var binding : FragmentRecyclerviewBinding
    private lateinit var adapter : NormalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_recyclerview, container, false)
        binding.lifecycleOwner = activity

        setUpRecyclerView()
        setUpHoldableSwipeHandler()

        return binding.root
    }

    private fun setUpHoldableSwipeHandler() {
        val swipeHelper = HoldableSwipeHelper(requireContext(), object : SwipeButtonAction {
            override fun onClickFirstButton(absoluteAdapterPosition: Int) {
                adapter.removePlayer(absoluteAdapterPosition)
                //adapter.notifyDataSetChanged()
                adapter.notifyItemRemoved(absoluteAdapterPosition)
                adapter.notifyItemRangeChanged(absoluteAdapterPosition,adapter.itemCount)
            }
        })

        swipeHelper.addRecyclerViewListener(binding.recyclerView)
        swipeHelper.addRecyclerViewDecoration(binding.recyclerView)
        swipeHelper.setDismissBackgroundOnClickedFirstItem(true)
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = NormalAdapter().apply {
            setPlayer(DataLoader.initPlayer(requireContext()))
        }
        binding.recyclerView.adapter = adapter
    }
}