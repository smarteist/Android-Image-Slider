package com.smarteist.autoimageslider.IndicatorView.draw.drawer

import android.graphics.Canvas
import android.graphics.Paint
import com.smarteist.autoimageslider.IndicatorView.animation.data.Value
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator
import com.smarteist.autoimageslider.IndicatorView.draw.drawer.type.*

class Drawer(indicator: Indicator) {
    private val basicDrawer: BasicDrawer
    private val colorDrawer: ColorDrawer?
    private val scaleDrawer: ScaleDrawer?
    private val wormDrawer: WormDrawer?
    private val slideDrawer: SlideDrawer?
    private val fillDrawer: FillDrawer?
    private val thinWormDrawer: ThinWormDrawer?
    private val dropDrawer: DropDrawer?
    private val swapDrawer: SwapDrawer?
    private val scaleDownDrawer: ScaleDownDrawer?
    private var position = 0
    private var coordinateX = 0
    private var coordinateY = 0
    fun setup(position: Int, coordinateX: Int, coordinateY: Int) {
        this.position = position
        this.coordinateX = coordinateX
        this.coordinateY = coordinateY
    }

    fun drawBasic(canvas: Canvas, isSelectedItem: Boolean) {
        if (colorDrawer != null) {
            basicDrawer.draw(canvas, position, isSelectedItem, coordinateX, coordinateY)
        }
    }

    fun drawColor(canvas: Canvas, value: Value) {
        colorDrawer?.draw(canvas, value, position, coordinateX, coordinateY)
    }

    fun drawScale(canvas: Canvas, value: Value) {
        scaleDrawer?.draw(canvas, value, position, coordinateX, coordinateY)
    }

    fun drawWorm(canvas: Canvas, value: Value) {
        wormDrawer?.draw(canvas, value, coordinateX, coordinateY)
    }

    fun drawSlide(canvas: Canvas, value: Value) {
        slideDrawer?.draw(canvas, value, coordinateX, coordinateY)
    }

    fun drawFill(canvas: Canvas, value: Value) {
        fillDrawer?.draw(canvas, value, position, coordinateX, coordinateY)
    }

    fun drawThinWorm(canvas: Canvas, value: Value) {
        thinWormDrawer?.draw(canvas, value, coordinateX, coordinateY)
    }

    fun drawDrop(canvas: Canvas, value: Value) {
        dropDrawer?.draw(canvas, value, coordinateX, coordinateY)
    }

    fun drawSwap(canvas: Canvas, value: Value) {
        swapDrawer?.draw(canvas, value, position, coordinateX, coordinateY)
    }

    fun drawScaleDown(canvas: Canvas, value: Value) {
        scaleDownDrawer?.draw(canvas, value, position, coordinateX, coordinateY)
    }

    init {
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        basicDrawer = BasicDrawer(paint, indicator)
        colorDrawer = ColorDrawer(paint, indicator)
        scaleDrawer = ScaleDrawer(paint, indicator)
        wormDrawer = WormDrawer(paint, indicator)
        slideDrawer = SlideDrawer(paint, indicator)
        fillDrawer = FillDrawer(paint, indicator)
        thinWormDrawer = ThinWormDrawer(paint, indicator)
        dropDrawer = DropDrawer(paint, indicator)
        swapDrawer = SwapDrawer(paint, indicator)
        scaleDownDrawer = ScaleDownDrawer(paint, indicator)
    }
}