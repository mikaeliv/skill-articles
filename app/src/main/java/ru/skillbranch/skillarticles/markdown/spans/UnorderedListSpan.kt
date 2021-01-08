package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px

class UnorderedListSpan(
    @Px
    private val gapWidth: Float,
    @Px
    private val bulletRadius: Float,
    @ColorInt
    private val bulletColor: Int
) : LeadingMarginSpan {

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence?,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        // only for first line draw bullet
        if (first) {
            p.withCustomColor {
                c.drawCircle(
                    gapWidth + x + bulletRadius,
                    (top + bottom) / 2f,
                    bulletRadius,
                    p
                )
            }
        }
    }

    override fun getLeadingMargin(first: Boolean): Int = (4 * bulletRadius + gapWidth).toInt()

    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style

        color = bulletColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle
    }
}