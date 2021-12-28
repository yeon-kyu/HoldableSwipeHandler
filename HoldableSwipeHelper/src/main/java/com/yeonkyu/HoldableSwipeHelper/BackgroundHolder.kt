package com.yeonkyu.HoldableSwipeHelper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BackgroundHolder(context: Context) {
    private val defaultItemSideMarginDp = 18f
    private val itemSideMarginUnit = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultItemSideMarginDp, context.resources.displayMetrics).toInt()
    var firstItemSideMargin = itemSideMarginUnit
        set(value) {
            field = itemSideMarginUnit * (value / defaultItemSideMarginDp).toInt()
        }
    var firstIcon = ContextCompat.getDrawable(context, R.drawable.ic_trash)!!

    private val intrinsicWidth = firstIcon.intrinsicWidth
    private val intrinsicHeight = firstIcon.intrinsicHeight

    var backgroundColor = Color.parseColor("#e45b78")
    private val background = ColorDrawable()

    var holderWidth = 0

    fun updateHolderWidth() {
        holderWidth = intrinsicWidth + 2 * firstItemSideMargin
    }

    fun isFirstItemArea(x: Int, y: Int) : Boolean {
        return firstIcon.bounds.contains(x, y)
    }

    fun drawHoldingBackground(canvas: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val itemView = viewHolder.itemView

        // holding 되는 background 그린다
        drawBackground(canvas, itemView)

        // holding 되는 background 에서 버튼의 위치를 계산하고 그린다
        drawFirstItem(canvas, itemView)
    }

    private fun drawBackground(canvas: Canvas, itemView: View) {
        background.color = backgroundColor
        background.setBounds(0 , itemView.top, itemView.right, itemView.bottom)
        background.draw(canvas)
    }

    private fun drawFirstItem(canvas: Canvas, itemView: View) {
        val itemHeight = itemView.bottom - itemView.top

        // holding 되는 background 에서 버튼의 위치를 계산한다
        val firstIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val firstIconLeft = itemView.right - firstItemSideMargin - intrinsicWidth
        val firstIconRight = itemView.right - firstItemSideMargin
        val firstIconBottom = firstIconTop + intrinsicHeight

        // holding 되는 background 에서 버튼을 그린다.
        firstIcon.setBounds(firstIconLeft, firstIconTop, firstIconRight, firstIconBottom)
        firstIcon.draw(canvas)
    }
}