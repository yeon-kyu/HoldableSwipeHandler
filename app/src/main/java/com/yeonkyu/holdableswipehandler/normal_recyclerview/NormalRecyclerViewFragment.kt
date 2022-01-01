package com.yeonkyu.holdableswipehandler.normal_recyclerview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yeonkyu.holdableswipehandler.R
import com.yeonkyu.holdableswipehandler.databinding.FragmentNormalRecyclerviewBinding
import com.yeonkyu.holdableswipehandler.util.DataInitializer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.yeonkyu.HoldableSwipeHelper.HoldableSwipeHelper
import com.yeonkyu.HoldableSwipeHelper.SwipeButtonAction

class NormalRecyclerViewFragment : Fragment() {

    private lateinit var binding : FragmentNormalRecyclerviewBinding
    private lateinit var adapter : NormalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_normal_recyclerview, container, false)
        binding.lifecycleOwner = activity

        setUpRecyclerView()

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = NormalAdapter().apply {
            setPlayer(DataInitializer.initPlayer(requireContext()))
        }
        binding.recyclerView.adapter = adapter

        val swipeHelper = HoldableSwipeHelper(requireContext(), object : SwipeButtonAction {
            override fun onClickFirstButton(absoluteAdapterPosition: Int) {

            }
        })
        swipeHelper.addRecyclerViewListener(binding.recyclerView)
        swipeHelper.addRecyclerViewDecoration(binding.recyclerView)
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


    }
}