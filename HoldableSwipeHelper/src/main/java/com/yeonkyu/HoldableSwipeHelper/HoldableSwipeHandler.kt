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
        val swipedBackgroundHolder = SwipedBackgroundHolder(context)
        var buttonAction: SwipeButtonAction? = null
        var recyclerView: RecyclerView? = null

        var firstItemDismissFlag = true
        val excludeViewTypeSet = mutableSetOf<Int>()

        fun addSwipeButtonAction(swipeButtonAction: SwipeButtonAction): Builder {
            this.buttonAction = swipeButtonAction
            return this
        }

        fun setOnRecyclerView(recyclerView: RecyclerView): Builder {
            this.recyclerView = recyclerView
            return this
        }

        /** default value : 18f */
        fun setFirstItemSideMarginDp(value: Int): Builder {
            swipedBackgroundHolder.firstItemSideMargin = value
            return this
        }

        /** default Icon : delete icon */
        fun setFirstItemDrawable(drawable: Drawable): Builder {
            swipedBackgroundHolder.firstIcon = drawable
            return this
        }

        /** default color : pink */
        fun setBackgroundColor(colorString: String): Builder {
            swipedBackgroundHolder.backgroundColor = Color.parseColor(colorString)
            return this
        }

        fun setBackgroundColor(@ColorInt color: Int): Builder {
            swipedBackgroundHolder.backgroundColor = color
            return this
        }

        fun excludeFromHoldableViewHolder(itemViewType: Int): Builder {
            this.excludeViewTypeSet.add(itemViewType)
            return this
        }

        fun setDismissOnClickFirstItem(value : Boolean): Builder {
            firstItemDismissFlag = value
            return this
        }

        fun build(): HoldableSwipeHandler {
            // buttonAction이 null일때 IllegalArguementException 발생

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