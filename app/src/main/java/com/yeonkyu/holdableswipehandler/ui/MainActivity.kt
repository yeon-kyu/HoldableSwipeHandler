package com.yeonkyu.holdableswipehandler.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yeonkyu.holdableswipehandler.R
import com.yeonkyu.holdableswipehandler.ui.list_adapter_recyclerview.ListAdapterRecyclerViewFragment
import com.yeonkyu.holdableswipehandler.ui.normal_recyclerview.NormalRecyclerViewFragment
import com.yeonkyu.holdableswipehandler.ui.paging3_recyclerview.PagingRecyclerViewFragment

class MainActivity : AppCompatActivity() {

    lateinit var viewPager : ViewPager2
    lateinit var bottomNavigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.actvity_main)
        viewPager = findViewById(R.id.main_view_pager)
        viewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        viewPager.registerOnPageChangeCallback(PageChangeCallback())

        bottomNavigation = findViewById(R.id.main_bottom_navigation)
        bottomNavigation.setOnItemSelectedListener {
            navigationSelected(it)
        }
    }

    private fun navigationSelected(item: MenuItem): Boolean {
        val checked = item.setChecked(true)
        when (checked.itemId) {
            R.id.normal_screen -> {
                viewPager.currentItem = 0
                return true
            }
            R.id.list_adapter_screen -> {
                viewPager.currentItem = 1
                return true
            }
            R.id.paging_screen -> {
                viewPager.currentItem = 2
                return true
            }
        }
        return false
    }

    private inner class PagerAdapter(fm: FragmentManager, lc: Lifecycle): FragmentStateAdapter(fm, lc) {
        override fun getItemCount() = 3
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> NormalRecyclerViewFragment()
                1 -> ListAdapterRecyclerViewFragment()
                2 -> PagingRecyclerViewFragment()
                else -> error("no such position: $position")
            }
        }
    }

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            bottomNavigation.selectedItemId = when (position) {
                0 -> {
                    supportActionBar?.title = "Normal RecyclerView"
                    R.id.normal_screen
                }
                1 -> {
                    supportActionBar?.title = "ListAdapter RecyclerView"
                    R.id.list_adapter_screen
                }
                2 -> {
                    supportActionBar?.title = "Paging3 RecyclerView"
                    R.id.paging_screen
                }
                else -> error("no such position: $position")
            }
        }
    }
}