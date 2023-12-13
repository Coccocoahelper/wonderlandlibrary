package net.ccbluex.liquidbounce.features.module.modules.ghost

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.minecraft.item.ItemBlock

@ModuleInfo(name = "FastPlace", "Fast Place",category = ModuleCategory.GHOST)
class FastPlace : Module() {
    val speedValue = IntegerValue("Speed", 0, 0, 4)
    val blockonlyValue = BoolValue("BlockOnly", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!blockonlyValue.get() || mc.thePlayer.heldItem.item is ItemBlock) {
            mc.rightClickDelayTimer = speedValue.get()
        }
    }
}
