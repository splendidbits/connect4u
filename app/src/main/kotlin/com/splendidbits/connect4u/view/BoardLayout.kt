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
    private lateinit var listener: (Int) -> Unit

    /**
     * Add any kind of chip position to a column and row coordinate.
     *
     * Note: All coordinates (column,row) start from bottom left. and move up
     * boardSize first
     */
    fun addChip(chip: ChipView, column: Int, row: Int) {
        val boardMargin = resources.getDimensionPixelSize(R.dimen.boardPadding)
        val appWidth = rootView.width - (boardMargin * 2)
        val appHeight = rootView.height - (boardMargin * 2)
        val boardWidth = if (appHeight > appWidth) appWidth else appHeight
        val chipSize = boardWidth / columns

        val layoutParams = FrameLayout.LayoutParams(chipSize, chipSize, Gravity.BOTTOM)
        layoutParams.leftMargin = (chipSize * column)
        layoutParams.bottomMargin = (chipSize * row)

        val coordinates = Pair(column, row)
        removeChip(coordinates)

        chip.tag = coordinates
        chip.setOnClickListener({
            listener.invoke(coordinates.first)
        })

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
        this.listener = listener

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