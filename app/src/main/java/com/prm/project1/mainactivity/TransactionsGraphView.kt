package com.prm.project1.mainactivity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.prm.project1.R
import com.prm.project1.database.Transaction

data class Point(val x: Int, val y: Int)

class TransactionsGraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val points = mutableListOf<Point>()
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0
    private val dataPointLinePaint = Paint().apply {
        color = getColor(context, R.color.balance_positive)
        strokeWidth = 5f
        isAntiAlias = true
    }
    private val axisLinePaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 3f
    }

    fun setTransactions(newTransactions: List<Transaction>, monthLength: Int) {
        val convertedPoints = convertTransactionsToPoints(newTransactions.reversed())
        xMax = monthLength
        yMin = convertedPoints.minByOrNull { it.y }?.y ?: 0
        yMax = convertedPoints.maxByOrNull { it.y }?.y ?: 0
        points.clear()
        points.addAll(convertedPoints)
        invalidate()
    }

    private fun convertTransactionsToPoints(transactions: List<Transaction>): List<Point> {
        val points = mutableListOf<Point>()
        var balance = 0
        for (transaction in transactions) {
            balance += transaction.value.toInt()
            val point = Point(transaction.date.dayOfMonth - 1, balance)
            if (points.size > 0 && point.x == points.last().x) {
                points[points.size - 1] = point
            } else {
                points.add(point)
            }
        }
        return points
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (points.size < 2) return
        drawGraphLine(canvas)
        drawFinalPoint(canvas)
        drawAxis(canvas)
    }

    private fun drawGraphLine(canvas: Canvas) {
        val linesArray = mutableListOf<Float>()
        linesArray.add(points.first().x.toRealX())
        linesArray.add(points.first().y.toRealX())
        points.forEach {
            val newX = it.x.toRealX()
            val newY = it.y.toRealY()
            linesArray.add(newX)
            linesArray.add(newY)
            linesArray.add(newX)
            linesArray.add(newY)
        }
        canvas.drawLines(linesArray.toFloatArray(), dataPointLinePaint)
    }

    private fun drawAxis(canvas: Canvas) {
        canvas.drawLine(1f, 1f, 1f, height.toFloat(), axisLinePaint)
        canvas.drawLine(1f, 0.toRealY(), width.toFloat(), 0.toRealY(), axisLinePaint)
    }

    private fun drawFinalPoint(canvas: Canvas) {
        canvas.drawCircle(points.last().x.toRealX(), points.last().y.toRealY(), 10f, dataPointLinePaint)
    }

    private fun Int.toRealX() = toFloat() / xMax * (width - 10f)
    private fun Int.toRealY() = (yMax - toFloat()) / (yMax - yMin) * (height - 10f)

}