package com.splendidbits.connect4u.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import com.splendidbits.connect4u.R

class BoardLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {
    private var columns = 4
    private var rows = 4

    /**
     * Add any kind of chip position to a column and row coordinate.
     *
     * Note: All coordinates (column,row) start from bottom left. and move up
     * columns first
     */
    fun addChip(chip: ChipView, column: Int, row: Int) {
        val chipSize = resources.getDimensionPixelSize(R.dimen.chipSize)
        val chipPadding = resources.getDimensionPixelSize(R.dimen.chipPadding)

        setPadding(chipPadding, chipPadding, chipPadding, chipPadding)

        val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM)

        layoutParams.leftMargin = (chipSize * column)
        layoutParams.bottomMargin = (chipSize * row)

        val coordinates = Pair(column, row)
        removeChip(coordinates)

        chip.tag = coordinates
        addView(chip, layoutParams)
    }

    /**
     * Adds blank spacers to the dimensions of the board which
     * adds the "open holes" ui, but also embiggens the board to the maximum
     * row / column from the start.
     */
    fun setBoardDimensions(columns: Int, rows: Int, listener: (Int) -> Unit) {
        this.columns = columns
        this.rows = rows

        removeAllViews()
        for (column in 0 until columns) {
            for (row in 0 until rows) {
                val chip = ChipView(context)
                chip.setOnClickListener {
                    listener.invoke(column)
                }
                addChip(chip, column, row)
            }
        }
    }

    /**
     * Removes a chip at a specific location
     */
    private fun removeChip(columnRow: Pair<Int, Int>) {
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView.tag.equals(columnRow)) {
                childView.setOnClickListener(null)
                removeView(childView)
                return
            }
        }
    }

}