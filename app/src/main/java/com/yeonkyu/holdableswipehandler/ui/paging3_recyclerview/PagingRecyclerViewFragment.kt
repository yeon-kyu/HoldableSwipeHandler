package com.yeonkyu.holdableswipehandler.ui.paging3_recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.yeonkyu.HoldableSwipeHelper.deprecated.HoldableSwipeHelper
import com.yeonkyu.HoldableSwipeHelper.SwipeButtonAction
import com.yeonkyu.holdableswipehandler.R
import com.yeonkyu.holdableswipehandler.databinding.FragmentRecyclerviewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingRecyclerViewFragment : Fragment() {
    private lateinit var binding : FragmentRecyclerviewBinding
    private lateinit var adapter : MyPagingAdapter
    private val playerFlow = Pager(PagingConfig(pageSize = 20)) {
        MyPagingSource()
    }.flow.cachedIn(lifecycleScope)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_recyclerview, container, false)
        binding.lifecycleOwner = this

        setUpRecyclerView()
        setUpHoldableSwipeHandler()

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = MyPagingAdapter()

        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            playerFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setUpHoldableSwipeHandler() {
        val swipeHelper = HoldableSwipeHelper(requireContext(), object : SwipeButtonAction {
            override fun onClickFirstButton(absoluteAdapterPosition: Int) {
                Toast.makeText(requireContext(),
                    "name : ${adapter.getPlayer(absoluteAdapterPosition).name}, position : $absoluteAdapterPosition",
                    Toast.LENGTH_SHORT).show()
            }
        })

        swipeHelper.addRecyclerViewListener(binding.recyclerView)
        swipeHelper.addRecyclerViewDecoration(binding.recyclerView)
        swipeHelper.setDismissBackgroundOnClickedFirstItem(false) // 여기선 아이템 삭제 안하는 것을 테스트한다
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }
}