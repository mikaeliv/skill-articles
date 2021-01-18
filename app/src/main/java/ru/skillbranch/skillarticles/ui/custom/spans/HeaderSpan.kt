package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import android.text.style.LineHeightSpan
import android.text.style.MetricAffectingSpan
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

class HeaderSpan(
    @IntRange(from = 1, to = 6)
    private val level: Int,
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val dividerColor: Int,
    @Px
    private val marginTop: Float,
    @Px
    private val marginBottom: Float
): MetricAffectingSpan(), LineHeightSpan, LeadingMarginSpan {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val linePadding = 0.4f

    private var originAscent = 0

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val sizes = mapOf(
        1 to 2f,
        2 to 1.5f,
        3 to 1.25f,
        4 to 1f,
        5 to 0.875f,
        6 to 0.85f
    )

    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: Paint.FontMetricsInt?
    ) {
        fm ?: return

        text as Spanned
        val spanStart = text.getSpanStart(this)
        val spanEnd = text.getSpanEnd(this)

        if (spanStart == start) {
            originAscent = fm.ascent
            fm.ascent = (fm.ascent - marginTop).toInt()
        } else {
            fm.ascent = originAscent
        }

        // line break +1 character
        if (spanEnd == end.dec()) {
            val originHeight = fm.descent - originAscent
            fm.descent = (originHeight * linePadding + marginBottom).toInt()
        }

        fm.top = fm.ascent
        fm.bottom = fm.descent
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        with(textPaint) {
            textSize *= sizes.getOrElse(level) { 1f }
            isFakeBoldText = true
        }
    }

    override fun updateDrawState(tp: TextPaint) {
        with(tp) {
            textSize *= sizes.getOrElse(level) { 1f }
            isFakeBoldText = true
            color = textColor
        }
    }

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
        // for 1 or 2 level and last line
        if ((level == 1 || level == 2) && (text as Spanned).getSpanEnd(this) == end) {
            p.forLine {
                val lh = (p.descent() - p.ascent()) * sizes.getOrElse(level) { 1f }
                val lineOffset = baseline + lh * linePadding

                c.drawLine(
                    0f,
                    lineOffset,
                    c.width.toFloat(),
                    lineOffset,
                    p
                )
            }
        }

        // c.drawFontLines(top, bottom, baseline, p)
    }

    override fun getLeadingMargin(first: Boolean): Int = 0

    private inline fun Paint.forLine(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWidth = strokeWidth

        color = dividerColor
        style = Paint.Style.STROKE
        strokeWidth = 0f

        block()

        color = oldColor
        style = oldStyle
        strokeWidth = oldWidth
    }

    private fun Canvas.drawFontLines(
        top: Int,
        bottom: Int,
        lineBaseline: Int,
        paint: Paint
    ) {
        drawLine(
            0f,
            top + 0f,
            width + 0f,
            top + 0f,
            Paint().apply { color = Color.BLUE }
        )
        drawLine(
            0f,
            bottom + 0f,
            width + 0f,
            bottom + 0f,
            Paint().apply { color = Color.GREEN }
        )
        drawLine(
            0f,
            lineBaseline + 0f,
            width + 0f,
            lineBaseline + 0f,
            Paint().apply { color = Color.RED }
        )
    }
}