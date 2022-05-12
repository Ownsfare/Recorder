package com.hk.recorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.hk.recorder.VisualizerView
import java.util.ArrayList

class VisualizerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var amplitudes // amplitudes for line lengths
            : MutableList<Float>? = null
//    private var width = 0    // width of this View
//
//    private var height = 0 // height of this View

    private val linePaint // specifies line drawing characteristics
            : Paint = Paint()

    // called when the dimensions of the View change
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        var width = w // new width of this View
        var height = h // new height of this View
        amplitudes = ArrayList(width / LINE_WIDTH)
    }

    // clear all amplitudes to prepare for a new visualization
    fun clear() {
        amplitudes!!.clear()
    }

    // add the given amplitude to the amplitudes ArrayList
    fun addAmplitude(amplitude: Float) {
        amplitudes!!.add(amplitude) // add newest to the amplitudes ArrayList
//        amplitudes!!.add(0.01F)

        // if the power lines completely fill the VisualizerView
        if (amplitudes!!.size * LINE_WIDTH >= width) {
            amplitudes!!.removeAt(0) // remove oldest power value
        }
    }

    // draw the visualizer with scaled lines representing the amplitudes
    public override fun onDraw(canvas: Canvas) {
        val middle = height / 2 // get the middle of the View
        var curX = 0f // start curX at zero

        // for each item in the amplitudes ArrayList
        for (power in amplitudes!!) {
            val scaledHeight = power / LINE_SCALE // scale the power
            curX += LINE_WIDTH.toFloat() + 3.0F // increase X by LINE_WIDTH     //1.0F

            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(
                curX, middle + scaledHeight / 2, curX, middle
                        - scaledHeight / 2, linePaint
            )
        }
    }

    companion object {
        private const val LINE_WIDTH = 10 // width of visualizer lines 1    10   5
        private const val LINE_SCALE = 75  // scales visualizer lines  75    30
    }

    // constructor
    init {
        // create Paint for lines
        linePaint.color = ContextCompat.getColor(context!!,R.color.myColor6) // set color to green
        linePaint.strokeWidth = LINE_WIDTH.toFloat() // set stroke width
    }

}