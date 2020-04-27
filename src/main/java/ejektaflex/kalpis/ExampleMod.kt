package ejektaflex.kalpis

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.event.Events
import ejektaflex.kalpis.render.RenderHelper
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

class ExampleMod : ModInitializer {

    val mc: MinecraftClient = MinecraftClient.getInstance()




    val region = EditRegion().apply {
        moveTo(4, 4, 4, 4, 3, 2)
    }

    override fun onInitialize() {

        println("Hello Fabric world!")

        KeyBindingRegistry.INSTANCE.addCategory("KEdit")

        KeyBindingRegistry.INSTANCE.register(moveDragBinding)
        KeyBindingRegistry.INSTANCE.register(sizeDragBinding)

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)

    }


    private fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers)

        RenderHelper.drawInWorld {
            region.update()
            region.draw()
        }
    }

    companion object {
        val moveDragBinding = FabricKeyBinding.Builder.create(
                Identifier("kedit", "move_drag"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "KEdit"
        ).build()

        val sizeDragBinding = FabricKeyBinding.Builder.create(
                Identifier("kedit", "size_drag"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "KEdit"
        ).build()
    }


}