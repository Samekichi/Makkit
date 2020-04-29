package ejektaflex.kalpis.edit.drag

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.ext.flipMask
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class SinglePlaneDragTool(region: EditRegion, binding: FabricKeyBinding) : DragTool(region, binding) {

    protected val plane = RenderBox()

    override fun onStartDragging(start: BoxTraceResult) {
        println("Dragging move")
        val areaSize = Vec3d(12.0, 12.0, 12.0).flipMask(start.dir)

        plane.box = Box(
                start.hit.subtract(areaSize),
                start.hit.add(areaSize)
        )
    }

    override fun onStopDragging(stop: BoxTraceResult) {
        val box = calcDragBox(false)
        box?.let { region.area.box = it }
    }

    override fun onDraw() {
        if (isDragging()) {
            region.preview.box = calcDragBox(true) ?: region.preview.box

            plane.draw(RenderColor.PINK)
        }
    }

}