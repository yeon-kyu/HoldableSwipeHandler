package com.yeonkyu.HoldableSwipeHelper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

open class HoldableSwipeHelper(context: Context, private val buttonAction: SwipeButtonAction) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val backgroundHolder = BackgroundHolder(context)
    private var currentViewHolder: RecyclerView.ViewHolder? = null
    private var currentDx = 0f

    //default value : 18f,
    fun setFirstItemSideMarginDp(value: Int) {
        backgroundHolder.firstItemSideMargin = value
    }

    //default Icon : delete icon
    fun setFirstItemDrawable(drawable: Drawable) {
        backgroundHolder.firstIcon = drawable
    }

    //default color : pink
    fun setBackgroundColor(colorString : String) {
        backgroundHolder.backgroundColor = Color.parseColor(colorString)
    }

    fun onDraw(canvas: Canvas) {
        currentViewHolder?.let {
            if (getViewHolderTag(it)) {
                backgroundHolder.drawHoldingBackground(canvas, it)
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }

    //recyclerview의 view가 반응하여 onDraw()할 때 콜백되는 함수
    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        currentDx = dX
        backgroundHolder.updateHolderWidth()

        val isHolding = getViewHolderTag(viewHolder)
        val x = holdViewPositionHorizontal(dX, isHolding)

        viewHolder.itemView.translationX = x

        backgroundHolder.drawHoldingBackground(canvas, viewHolder)
        currentViewHolder = viewHolder
    }

    // swipe 해서 손을 떼었을 때 콜백된다.
    // setViewHolderTag()를 설정한다.
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        if(currentDx <= -backgroundHolder.holderWidth) {
            setViewHolderTag(viewHolder, true)
        } else { //정확히 currentDx가 rightWidth만큼 당겨져야하는지, 그 중간이 될지는 추가 논의필요
            setViewHolderTag(viewHolder, false)
        }

        return 2f
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 0f
    }

    /*
    사용자 interaction이 끝났을때 호출됨(스와이프 등 포함)
    하지만 clearView()는 onChildDraw()가 완전히 끝났을때 호출되기 때문에 여기서
    setViewHolder() 세팅을하면 ui 적용이 안된다.
    */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentViewHolder?.let {
            addFirstItemClickListener(recyclerView, viewHolder)
        }
    }

    //holding 되어 화면에 걸쳐있으면 tag를 true로 둔다
    private fun setViewHolderTag(viewHolder: RecyclerView.ViewHolder, isholding: Boolean) {
        viewHolder.itemView.tag = isholding
    }

    private fun getViewHolderTag(viewHolder: RecyclerView.ViewHolder?) : Boolean {
        return viewHolder?.itemView?.tag as? Boolean ?: false
    }

    private fun holdViewPositionHorizontal(
        dX: Float,
        isHolding: Boolean
    ) : Float {
        val min: Float = -backgroundHolder.holderWidth.toFloat()
        val max = 0f

        val x = if (isHolding) {
            dX - backgroundHolder.holderWidth
        } else {
            dX
        }
        return Math.min(Math.max(min, x), max)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addFirstItemClickListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (backgroundHolder.isFirstItemArea(event.x.toInt(), event.y.toInt())) {
                    recyclerView.setOnTouchListener { _, event2 ->
                        if (event2.action == MotionEvent.ACTION_UP) {
                            if (backgroundHolder.isFirstItemArea(event2.x.toInt(), event2.y.toInt())) {
                                buttonAction.onClickFirstButton(viewHolder.absoluteAdapterPosition)
                            }
                        }
                        false
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun addRecyclerViewListener(recyclerView: RecyclerView) {
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    rv.findChildViewUnder(e.x, e.y)?.let {
                        val viewHolder = rv.getChildViewHolder(it)
                        if(viewHolder.absoluteAdapterPosition != currentViewHolder?.absoluteAdapterPosition) {
                            releaseCurrentViewHolder()
                        }
                    }
                }
                return false
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) { }
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) { }
        })

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            releaseCurrentViewHolder()
        }
    }

    private fun releaseCurrentViewHolder() {
        //아이탬 holding 제거
        currentViewHolder?.apply {
            setViewHolderTag(this, false)
            itemView.animate().translationX(0f).duration = 300L
            currentViewHolder = null
        }
    }
}