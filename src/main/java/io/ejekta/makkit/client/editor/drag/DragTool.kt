package io.ejekta.makkit.client.editor.drag

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.ext.autoTrace
import io.ejekta.makkit.common.ext.sizeInDirection
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

internal abstract class DragTool(val region: EditRegion) {

    abstract val keyHandler: KeyStateHandler

    // We can have other preview boxes and draw them in [onDrawPreview], we just need at least one
    open val preview = RenderBox().apply {
        fillColor = RenderColor.BLUE.toAlpha(.4f)
        edgeColor = RenderColor.ORANGE.toAlpha(.2f)
    }

    var dragStart = BoxTraceResult.EMPTY

    fun isDragging(): Boolean {
        return dragStart != BoxTraceResult.EMPTY
    }

    protected fun getMainAxis(): Direction.Axis {
        return dragStart.dir.axis
    }

    protected fun getAlternateAxesDirections(): List<Direction> {
        return enumValues<Direction>().filter { it.axis != dragStart.dir.axis }
    }

    protected fun getAlternateAxes(): List<Direction.Axis> {
        return enumValues<Direction.Axis>().filter { it != dragStart.dir.axis }
    }

    protected fun getSelectionSizeIn(direction: Direction): Double {
        return region.selection.sizeInDirection(direction)
    }

    protected fun getPreviewSizeIn(direction: Direction): Double {
        return preview.box.sizeInDirection(direction)
    }


    /**
     * Calculates a box shape for the tool, given a position
     * @param offset The position of the cursor
     * @param box The starting box region
     */
    abstract fun getPreviewBox(offset: Vec3d, box: Box): Box

    abstract fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box

    /**
     * Calculates the position of the drag cursor. May also be snapped to a block grid
     * @param snapped Whether or not to snap the cursor to the block grid
     */
    abstract fun getCursorOffset(snapped: Boolean = MakkitClient.config.gridSnapping): Vec3d?

    /**
     * Draws the tool to the screen, with the given offset
     */
    abstract fun onDrawPreview(offset: Vec3d)

    open fun onStartDragging(start: BoxTraceResult) {
        // Do nothing by default
    }

    fun updateState(updateSelection: Boolean = true): Box? {
        return getCursorOffset(true)?.let {
            val preview = getPreviewBox(it, region.selection)
            if (updateSelection) {
                region.selection = getSelectionBox(it, region.selection, preview)
            }
            preview
        }
    }



    open fun onStopDragging(stop: BoxTraceResult) {
        val box = updateState(updateSelection = true)
    }

    fun update() {
        // Try to start dragging
        if (MakkitClient.isInEditMode && dragStart == BoxTraceResult.EMPTY && keyHandler.isDown) {
            dragStart = region.selection.autoTrace()
            if (dragStart != BoxTraceResult.EMPTY && MakkitClient.isInEditMode) {
                onStartDragging(dragStart)
            }
        }

        // Try to stop dragging
        if (dragStart != BoxTraceResult.EMPTY && !keyHandler.isDown) {
            onStopDragging(dragStart)
            dragStart = BoxTraceResult.EMPTY
        }
    }

    fun tryDraw() {
        if (isDragging()) {
            val off = getCursorOffset()
            if (off != null) {
                val prevBox = getPreviewBox(off, region.selection)
                preview.box = prevBox
                onDrawPreview(off)
            }
        }
    }

    protected companion object {
        const val DRAG_PLANE_SIZE = 64.0
    }

}
