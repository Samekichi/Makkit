package ejektaflex.kalpis.common.world

import ejektaflex.kalpis.common.network.EditIntentPacket
import net.minecraft.item.BlockItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Box
import java.util.*

object WorldEditor {

    private val stackMap = mutableMapOf<String, Stack<EditAction>>()

    fun handleNetworkOperation(player: ServerPlayerEntity, intent: EditIntentPacket) {
        val action = EditAction(
                player,
                Box(intent.start, intent.end),
                intent.side,
                intent.op,
                // will crash if item is not a BlockItem, change this eventually
                intent.palette.map { (it.item as BlockItem).block.defaultState }
        )

        try {
            action.operation.execute(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun handleUndo(player: ServerPlayerEntity) {
        if (player.uuidAsString !in stackMap) {
            return
        } else {

        }
    }









}