package com.lazureleming.financemanager.mainactivity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.lazureleming.financemanager.R
import com.lazureleming.financemanager.database.Transaction
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private data class Point(val x: Double, val y: Double)

private const val VIEW_PADDING = 2f

/**
 * [View] representing distribution of [Transaction] in a given month.
 */
class MonthBalanceGraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

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
        isAntiAlias = true
    }
    private lateinit var pointsToDraw: List<Pair<List<Point>, Paint>>
    private var xMax = 0.0
    private var yMin = 0.0
    private var yMax = 0.0

    fun setTransactions(newTransactions: List<Transaction>, monthLength: Int) {
        xMax = monthLength.toDouble()
        pointsToDraw = convertTransactionsToPoints(newTransactions.reversed())
        yMin = min(pointsToDraw.flatMap { it.first }.minByOrNull { it.y }?.y ?: 0.0, 0.0)
        yMax = max(pointsToDraw.flatMap { it.first }.maxByOrNull { it.y }?.y ?: 0.0, 0.0)
        invalidate()
    }

    private fun convertTransactionsToPoints(transactions: List<Transaction>): List<Pair<MutableList<Point>, Paint>> {
        val points = mutableListOf<Pair<MutableList<Point>, Paint>>()
        var balance = 0.0

        transactions.groupBy { it.date }.forEach {
            val day = it.key.dayOfMonth - 1.0
            val totalDayExpenses = it.value.map { transaction -> transaction.value }.sum()
            val newBalance = balance + totalDayExpenses
            val newPoint = Point(day, newBalance)
            when {
                points.isEmpty() -> points.add(prepareInitialPointData(newBalance, newPoint))
                (newBalance >= 0.0) != (balance >= 0.0) -> preparePointDataCrossingXAxis(points, newBalance, newPoint)
                else -> points.last().first.add(newPoint)
            }
            balance = newBalance
        }

        return points
    }

    private fun prepareInitialPointData(newBalance: Double, newPoint: Point): Pair<MutableList<Point>, Paint> {
        val startingPaint = if (newBalance > 0) graphLinePositive else graphLineNegative
        return Pair(mutableListOf(newPoint), startingPaint)
    }

    private fun preparePointDataCrossingXAxis(
        points: MutableList<Pair<MutableList<Point>, Paint>>,
        newBalance: Double,
        newPoint: Point
    ) {
        val paint = if (newBalance > 0) graphLinePositive else graphLineNegative
        val lastPoint = points.last().first.last()
        val distanceToZero = abs((newPoint.x - lastPoint.x) * lastPoint.y / (newPoint.y - lastPoint.y))
        val middlePoint = Point(distanceToZero + lastPoint.x, 0.0)
        points.last().first.add(middlePoint)
        val newPointsGroup = mutableListOf(middlePoint, newPoint)
        points.add(Pair(newPointsGroup, paint))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!this::pointsToDraw.isInitialized || pointsToDraw.flatMap { it.first }.size < 2) return
        pointsToDraw.forEach { canvas.drawGraphLine(it.first, it.second) }
        canvas.drawFinalPoint()
        canvas.drawAxis()
    }

    private fun Canvas.drawGraphLine(points: List<Point>, paint: Paint) {
        if (points.isEmpty()) return
        val linesArray = mutableListOf<Float>()
        points.forEach {
            val newX = it.x.viewX()
            val newY = it.y.viewY()
            // Adding double to ensure that lines are continuous.
            linesArray.add(newX)
            linesArray.add(newY)
            linesArray.add(newX)
            linesArray.add(newY)
        }
        // Removing first and last point so that each quadruplet is between two different points.
        linesArray.removeFirst()
        linesArray.removeFirst()
        linesArray.removeLast()
        linesArray.removeLast()
        drawLines(linesArray.toFloatArray(), paint)
    }

    private fun Canvas.drawAxis() {
        // Y axis
        drawLine(VIEW_PADDING, VIEW_PADDING, VIEW_PADDING, height - VIEW_PADDING, axisLinePaint)
        // X axis is either on the very top, very bottom, or somewhere in the middle, depending on given data
        val xAxisYCoordinate = when {
            yMin > 0 -> height - VIEW_PADDING
            yMax < 0 -> VIEW_PADDING
            else -> 0.0.viewY()
        }
        drawLine(VIEW_PADDING, xAxisYCoordinate, width - VIEW_PADDING, xAxisYCoordinate, axisLinePaint)
    }

    private fun Canvas.drawFinalPoint() {
        val lastPoint = pointsToDraw.last().first.last()
        drawCircle(lastPoint.x.viewX(), lastPoint.y.viewY(), 10f, pointsToDraw.last().second)
    }

    private fun Double.viewX() = (this / xMax * (width - (VIEW_PADDING * 2))).toFloat()

    private fun Double.viewY() = ((yMax - this) / (yMax - yMin) * (height - (VIEW_PADDING * 2))).toFloat()
}