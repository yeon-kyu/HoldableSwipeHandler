package com.yeonkyu.HoldableSwipeHelper.deprecated

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yeonkyu.HoldableSwipeHelper.SwipeButtonAction
import com.yeonkyu.HoldableSwipeHelper.SwipedBackgroundHolder


/** @deprecated */
@Deprecated("Use HoldableSwipeHandler")
open class HoldableSwipeHelper(context: Context, private val buttonAction: SwipeButtonAction) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val swipedBackgroundHolder = SwipedBackgroundHolder(context)
    private var currentViewHolder: RecyclerView.ViewHolder? = null
    private var absoluteDx = 0f
    private var scopedX = 0f

    private var firstItemDismissFlag = true
    private val excludeViewTypeSet = mutableSetOf<Int>()

    /** default value : 18f */
    @Deprecated("Use HoldableSwipeHandler")
    fun setFirstItemSideMarginDp(value: Int) {
        swipedBackgroundHolder.firstItemSideMargin = value
    }

    /** default Icon : delete icon */
    @Deprecated("Use HoldableSwipeHandler")
    fun setFirstItemDrawable(drawable: Drawable) {
        swipedBackgroundHolder.firstIcon = drawable
    }

    /** default color : pink */
    @Deprecated("Use HoldableSwipeHandler")
    fun setBackgroundColor(colorString: String) {
        swipedBackgroundHolder.backgroundColor = Color.parseColor(colorString)
    }

    @Deprecated("Use HoldableSwipeHandler")
    fun setBackgroundColor(@ColorInt color: Int) {
        swipedBackgroundHolder.backgroundColor = color
    }

    @Deprecated("Use HoldableSwipeHandler")
    fun excludeFromHoldableViewHolder(itemViewType: Int) {
        excludeViewTypeSet.add(itemViewType)
    }

    @Deprecated("Use HoldableSwipeHandler")
    fun setDismissBackgroundOnClickedFirstItem(value : Boolean) {
        firstItemDismissFlag = value
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

    /** recyclerview의 view가 반응하여 onDraw()할 때 콜백되는 함수 */
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
     * swipe 해서 손을 떼었을 때 콜백된다.
     * setViewHolderTag()를 설정한다.
     */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        if (absoluteDx <= -swipedBackgroundHolder.holderWidth) {
            setViewHolderTag(viewHolder, true)
        } else { // 정확히 currentDx가 rightWidth만큼 당겨져야하는지, 그 중간이 될지는 추가 논의필요
            setViewHolderTag(viewHolder, false)
        }

        return 2f
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 0f
    }

    /**
     * 사용자 interaction이 끝났을때 호출됨(스와이프 등 포함)
     * 하지만 clearView()는 onChildDraw()가 거의 완전히 끝났을때 호출되기 때문에
     * 여기서 setViewHolder() 세팅을하면 ui 적용이 안된다.
     * 또한 getSwipeThreshold() 보다 늦게 호출된다.
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentViewHolder?.let {
            if (getViewHolderTag(it)) {
                addFirstItemClickListener(recyclerView, viewHolder)
            }

        }
    }

    /** holding 되어 화면에 걸쳐있으면 tag 를 true 로 둔다 */
    private fun setViewHolderTag(viewHolder: RecyclerView.ViewHolder, isHolding: Boolean) {
        viewHolder.itemView.tag = isHolding
    }

    /** 현재 인자로 받은 뷰홀더가 화면에 걸쳐있는지를 반환한다 */
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
    @Deprecated("Use HoldableSwipeHandler")
    fun addRecyclerViewListener(recyclerView: RecyclerView) {
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

    @Deprecated("Use HoldableSwipeHandler")
    fun addRecyclerViewDecoration(recyclerView: RecyclerView) {
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
     * currentViewHolder 를 null 로 두면 recyclerView 의
     * ItemDecoration 의 onDraw()가 정상 로직 수행을 못한다
     * 따라서 즉각 삭제되는 상황에서는 currentViewHolder 를 null 로 두지 않는다.
     */
    private fun releaseCurrentViewHolderImmediately() {
        currentViewHolder?.apply {
            setViewHolderTag(this, false)
            itemView.translationX = 0f
        }
    }

    private fun releaseCurrentViewHolder() {
        // 현재 아이탬 holding 제거
        currentViewHolder?.apply {
            setViewHolderTag(this, false)
            itemView.animate().translationX(0f).duration = 300L
            currentViewHolder = null
        }
    }
}