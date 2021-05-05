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
import kotlin.math.abs

private data class Point(val x: Double, val y: Double)

private const val CANVAS_PADDING = 2f

class TransactionsGraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val points = mutableListOf<Pair<List<Point>, Paint>>()
    private var xMax = 0.0
    private var yMin = 0.0
    private var yMax = 0.0
    private val graphLinePositive = Paint().apply {
        color = getColor(context, R.color.balance_positive)
        strokeWidth = 5f
        isAntiAlias = true
    }
    private val graphLineNegative = Paint().apply {
        color = getColor(context, R.color.balance_negative)
        strokeWidth = 5f
        isAntiAlias = true
    }
    private val axisLinePaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 3f
    }

    fun setTransactions(newTransactions: List<Transaction>, monthLength: Int) {
        xMax = monthLength.toDouble()
        val convertedPoints = convertTransactionsToPoints(newTransactions.reversed())
        yMin = convertedPoints.flatMap { it.first }.minByOrNull { it.y }?.y ?: 0.0
        yMax = convertedPoints.flatMap { it.first }.maxByOrNull { it.y }?.y ?: 0.0
        points.clear()
        points.addAll(convertedPoints)
        invalidate()
    }

    private fun convertTransactionsToPoints(transactions: List<Transaction>): List<Pair<List<Point>, Paint>> {
        val points = mutableListOf<Pair<MutableList<Point>, Paint>>()
        var balance = 0.0

        transactions.groupBy { it.date }.forEach {
            val day = it.key.dayOfMonth - 1.0
            val totalDayExpenses = it.value.map { transaction -> transaction.value }.sum()
            val newBalance = balance + totalDayExpenses
            val newPoint = Point(day, newBalance)
            when {
                points.isEmpty() -> {
                    val startingPaint = if (newBalance > 0) graphLinePositive else graphLineNegative
                    points.add(Pair(mutableListOf(newPoint), startingPaint))
                }
                (newBalance >= 0.0) != (balance >= 0.0) -> {
                    val paint = if (newBalance > 0) graphLinePositive else graphLineNegative
                    val lastPoint = points.last().first.last()
                    val distanceToZero = abs((newPoint.x - lastPoint.x) * lastPoint.y / (newPoint.y - lastPoint.y))
                    val middlePoint = Point(distanceToZero + lastPoint.x, 0.0)
                    points.last().first.add(middlePoint)
                    val newPointsGroup = mutableListOf(middlePoint, newPoint)
                    points.add(Pair(newPointsGroup, paint))
                }
                else -> {
                    points.last().first.add(newPoint)
                }
            }

            balance = newBalance
        }

        return points
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (points.flatMap { it.first }.size < 2) return
        points.forEach { drawGraphLine(canvas, it.first, it.second) }
        drawFinalPoint(canvas)
        drawAxis(canvas)
    }

    private fun drawGraphLine(canvas: Canvas, points: List<Point>, paint: Paint) {
        if (points.isEmpty()) return
        val linesArray = mutableListOf<Float>()
        points.forEach {
            val newX = it.x.canvasX()
            val newY = it.y.canvasY()
            linesArray.add(newX)
            linesArray.add(newY)
            linesArray.add(newX)
            linesArray.add(newY)
        }
        linesArray.removeFirst()
        linesArray.removeFirst()
        linesArray.removeLast()
        linesArray.removeLast()
        canvas.drawLines(linesArray.toFloatArray(), paint)
    }

    private fun drawAxis(canvas: Canvas) {
        canvas.drawLine(CANVAS_PADDING, CANVAS_PADDING, CANVAS_PADDING, height - CANVAS_PADDING, axisLinePaint)
        canvas.drawLine(CANVAS_PADDING, 0.0.canvasY(), width - CANVAS_PADDING, 0.0.canvasY(), axisLinePaint)
    }

    private fun drawFinalPoint(canvas: Canvas) {
        val lastPoint = points.last().first.last()
        canvas.drawCircle(lastPoint.x.canvasX(), lastPoint.y.canvasY(), 10f, points.last().second)
    }

    private fun Double.canvasX() = (this / xMax * (width - (CANVAS_PADDING * 2))).toFloat()

    private fun Double.canvasY() = ((yMax - this) / (yMax - yMin) * (height - (CANVAS_PADDING * 2))).toFloat()
}