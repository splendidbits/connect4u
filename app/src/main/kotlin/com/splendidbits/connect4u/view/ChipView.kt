package com.splendidbits.connect4u.view

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.splendidbits.connect4u.R
import com.splendidbits.connect4u.model.PositionValue

class ChipView(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : AppCompatImageView(context, attrs, defStyle) {

    init {
        setPositionValue(PositionValue.POSITION_BLANK)
    }

    fun setPositionValue(positionValue: PositionValue): ChipView {

        when (positionValue) {
            PositionValue.POSITION_USER ->
                setImageDrawable(context.resources.getDrawable(R.drawable.chip_blue))

            PositionValue.POSITION_OPPONENT ->
                setImageDrawable(context.resources.getDrawable(R.drawable.chip_red))

            PositionValue.POSITION_BLANK ->
                setImageDrawable(context.resources.getDrawable(R.drawable.chip_grey))

            else -> {
                setImageDrawable(context.resources.getDrawable(R.drawable.chip_grey))
            }
        }
        return this
    }
}