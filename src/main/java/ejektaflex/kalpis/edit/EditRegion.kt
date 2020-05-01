package ejektaflex.kalpis.edit

import ejektaflex.kalpis.ExampleMod
import ejektaflex.kalpis.edit.drag.tools.MoveToolDualAxis
import ejektaflex.kalpis.edit.drag.tools.MoveToolSingleAxis
import ejektaflex.kalpis.edit.drag.tools.ResizeToolDualAxis
import ejektaflex.kalpis.edit.drag.tools.ResizeToolSingleAxis
import ejektaflex.kalpis.ext.getBlockArray
import ejektaflex.kalpis.ext.wallBlocks
import ejektaflex.kalpis.render.MyLayers
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import org.lwjgl.glfw.GLFW

class EditRegion(var drawDragPlane: Boolean = false, var smoothDrag: Boolean = true) {

    val samplePlaneSize = 16.0

    val area = RenderBox().apply {
        color = RenderColor.GREEN.toAlpha(0.45f)
    }

    val preview = RenderBox().apply {
        color = RenderColor.BLUE.toAlpha(0.45f)
    }

    private val moveToolDual = MoveToolDualAxis(this, ExampleMod.moveDragBinding)
    private val resizeTool = ResizeToolSingleAxis(this, ExampleMod.resizeSideBinding)
    private val moveToolSingle = MoveToolSingleAxis(this, ExampleMod.moveDragSingleBinding)
    //private val resizeToolDual = ResizeToolDualAxis(this, ExampleMod.resizeDualSideBinding)

    private val tools = listOf(
            moveToolDual,
            resizeTool,
            moveToolSingle//,
            //resizeToolDual
    )

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        area.box = Box(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz))
    }

    fun update() {
        tools.forEach { tool -> tool.update() }

        if (ExampleMod.deleteBinding.isPressed) {

            val blocks = area.getBlockArray()

            val mc = MinecraftClient.getInstance()
            val player = mc.player!!
            val item = player.mainHandStack.item

            if (item is BlockItem) {
                blocks.forEach { pos ->
                    mc.world!!.setBlockState(pos, item.block.defaultState)
                }
            } else if (item is AirBlockItem) {
                blocks.forEach { pos ->
                    mc.world!!.setBlockState(pos, Blocks.AIR.defaultState)
                }
            }

        }

        if (ExampleMod.wallsBinding.isPressed) {

            val blocks = area.box.wallBlocks()

            val mc = MinecraftClient.getInstance()
            val player = mc.player!!
            val item = player.mainHandStack.item

            if (item is BlockItem) {
                blocks.forEach { pos ->
                    mc.world!!.setBlockState(pos, item.block.defaultState)
                }
            } else if (item is AirBlockItem) {
                blocks.forEach { pos ->
                    mc.world!!.setBlockState(pos, Blocks.AIR.defaultState)
                }
            }

        }

    }

    fun draw() {

        area.draw()

        val anyToolsDragging = tools.any { it.isDragging() }

        if (anyToolsDragging) {
            tools.forEach { tool ->
                tool.update()
                tool.tryDraw()
            }
        } else {
            // default state when no drag tool is being used
            val hit = area.trace(MinecraftClient.getInstance().options.keySprint.isPressed)
            hit?.let {
                area.drawFace(it.dir, RenderColor.YELLOW.toAlpha(.45f))
                area.drawAxisSizes()
            }

            for (pos in area.getBlockArray()) {
                if (MinecraftClient.getInstance().world!!.getBlockState(pos).isAir) {
                    RenderHelper.drawBlockFaces(pos, RenderColor.ORANGE.toAlpha(.2f), MyLayers.OVERLAY_QUADS_BEHIND)
                }
            }

        }

    }


}