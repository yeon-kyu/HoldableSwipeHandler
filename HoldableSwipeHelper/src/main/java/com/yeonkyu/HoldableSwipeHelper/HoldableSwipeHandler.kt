package com.yeonkyu.HoldableSwipeHelper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException

class HoldableSwipeHandler private constructor(builder: Builder) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private var swipedBackgroundHolder: SwipedBackgroundHolder = builder.swipedBackgroundHolder
    private val buttonAction: SwipeButtonAction = builder.buttonAction!!
    private var firstItemDismissFlag: Boolean = builder.firstItemDismissFlag
    private val excludeViewTypeSet: Set<Int> = builder.excludeViewTypeSet

    private var currentViewHolder: RecyclerView.ViewHolder? = null
    private var absoluteDx = 0f
    private var scopedX = 0f

    init {
        addRecyclerViewDecoration(builder.recyclerView!!)
        addRecyclerViewListener(builder.recyclerView!!)

        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(builder.recyclerView!!)
    }

    class Builder(context: Context) {
        internal val swipedBackgroundHolder = SwipedBackgroundHolder(context)
        internal var buttonAction: SwipeButtonAction? = null
        internal var recyclerView: RecyclerView? = null

        internal var firstItemDismissFlag = true
        internal val excludeViewTypeSet = mutableSetOf<Int>()

        fun setSwipeButtonAction(swipeButtonAction: SwipeButtonAction) = this.apply {
            this.buttonAction = swipeButtonAction
        }

        fun setOnRecyclerView(recyclerView: RecyclerView) = this.apply {
            this.recyclerView = recyclerView
        }

        /** default value : 18f */
        fun setFirstItemSideMarginDp(value: Int) = this.apply {
            swipedBackgroundHolder.firstItemSideMargin = value
        }

        /** default Icon : delete icon */
        fun setFirstItemDrawable(drawable: Drawable) = this.apply {
            swipedBackgroundHolder.firstIcon = drawable
        }

        /** default color : pink */
        fun setBackgroundColor(colorString: String) = this.apply {
            swipedBackgroundHolder.backgroundColor = Color.parseColor(colorString)
        }

        fun setBackgroundColor(@ColorInt color: Int) = this.apply {
            swipedBackgroundHolder.backgroundColor = color
        }

        fun excludeFromHoldableViewHolder(itemViewType: Int) = this.apply {
            this.excludeViewTypeSet.add(itemViewType)
        }

        fun setDismissOnClickFirstItem(value : Boolean) = this.apply {
            firstItemDismissFlag = value
        }

        fun build(): HoldableSwipeHandler {
            if (buttonAction == null) {
                throw IllegalArgumentException("SwipeButtonAction should be implemented. Did you forget to call addSwipeButtonAction()?")
            }
            if (recyclerView == null) {
                throw IllegalArgumentException("RecyclerView should be set to HoldableSwipeHandler. Did you forget to call setOnRecyclerView()?")
            }

            return HoldableSwipeHandler(this)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

    /** recyclerview??? view??? ???????????? onDraw()??? ??? ???????????? ?????? */
    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        if (excludeViewTypeSet.contains(viewHolder.itemViewType)) {
            return
        }
        absoluteDx = dX
        swipedBackgroundHolder.updateHolderWidth()

        val isHolding = getViewHolderTag(viewHolder)
        scopedX = holdViewPositionHorizontal(dX, isHolding)

        viewHolder.itemView.translationX = scopedX

        swipedBackgroundHolder.drawHoldingBackground(canvas, viewHolder, scopedX.toInt())
        currentViewHolder = viewHolder
    }

    /**
     * swipe ?????? ?????? ????????? ??? ????????????.
     * setViewHolderTag()??? ????????????.
     */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        if (absoluteDx <= -swipedBackgroundHolder.holderWidth) {
            setViewHolderTag(viewHolder, true)
        } else { // ????????? currentDx??? rightWidth?????? ?????????????????????, ??? ????????? ????????? ?????? ????????????
            setViewHolderTag(viewHolder, false)
        }

        return 2f
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 0f
    }

    /**
     * ????????? interaction??? ???????????? ?????????(???????????? ??? ??????)
     * ????????? clearView()??? onChildDraw()??? ?????? ????????? ???????????? ???????????? ?????????
     * ????????? setViewHolder() ??????????????? ui ????????? ?????????.
     * ?????? getSwipeThreshold() ?????? ?????? ????????????.
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentViewHolder?.let {
            if (getViewHolderTag(it)) {
                addFirstItemClickListener(recyclerView, viewHolder)
            }
        }
    }

    /** holding ?????? ????????? ??????????????? tag ??? true ??? ?????? */
    private fun setViewHolderTag(viewHolder: RecyclerView.ViewHolder, isHolding: Boolean) {
        viewHolder.itemView.tag = isHolding
    }

    /** ?????? ????????? ?????? ???????????? ????????? ?????????????????? ???????????? */
    private fun getViewHolderTag(viewHolder: RecyclerView.ViewHolder?) : Boolean {
        return viewHolder?.itemView?.tag as? Boolean ?: false
    }

    private fun holdViewPositionHorizontal(
        dX: Float,
        isHolding: Boolean
    ): Float {
        val min: Float = -swipedBackgroundHolder.holderWidth.toFloat()
        val max = 0f

        val x = if (isHolding) {
            dX - swipedBackgroundHolder.holderWidth
        } else {
            dX
        }
        return Math.min(Math.max(min, x), max)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addFirstItemClickListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, downEvent ->
            if (downEvent.action == MotionEvent.ACTION_DOWN) {
                if (swipedBackgroundHolder.isFirstItemArea(downEvent.x.toInt(), downEvent.y.toInt())) {
                    recyclerView.setOnTouchListener { _, upEvent ->
                        if (upEvent.action == MotionEvent.ACTION_UP) {
                            if (swipedBackgroundHolder.isFirstItemArea(upEvent.x.toInt(), upEvent.y.toInt())
                                && getViewHolderTag(viewHolder)
                            ) {
                                if (firstItemDismissFlag) {
                                    releaseCurrentViewHolderImmediately()
                                }

                                if (viewHolder.absoluteAdapterPosition >= 0) {
                                    buttonAction.onClickFirstButton(viewHolder.absoluteAdapterPosition)
                                }
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
    private fun addRecyclerViewListener(recyclerView: RecyclerView) {
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    rv.findChildViewUnder(e.x, e.y)?.let {
                        val viewHolder = rv.getChildViewHolder(it)
                        if (viewHolder.absoluteAdapterPosition != currentViewHolder?.absoluteAdapterPosition) {
                            releaseCurrentViewHolder()
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                releaseCurrentViewHolder()
            }
        })
    }

    private fun addRecyclerViewDecoration(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                currentViewHolder?.let {
                    if (getViewHolderTag(it)) {
                        swipedBackgroundHolder.run {
                            drawHoldingBackground(c, it, scopedX.toInt())
                        }
                    }
                }
            }
        })
    }

    /**
     * currentViewHolder ??? null ??? ?????? recyclerView ???
     * ItemDecoration ??? onDraw()??? ?????? ?????? ????????? ?????????
     * ????????? ?????? ???????????? ??????????????? currentViewHolder ??? null ??? ?????? ?????????.
     */
    private fun releaseCurrentViewHolderImmediately() {
        currentViewHolder?.apply {
            setViewHolderTag(this, false)
            itemView.translationX = 0f
        }
    }

    private fun releaseCurrentViewHolder() {
        // ?????? ????????? holding ??????
        currentViewHolder?.apply {
            setViewHolderTag(this, false)
            itemView.animate().translationX(0f).duration = 300L
            currentViewHolder = null
        }
    }
}