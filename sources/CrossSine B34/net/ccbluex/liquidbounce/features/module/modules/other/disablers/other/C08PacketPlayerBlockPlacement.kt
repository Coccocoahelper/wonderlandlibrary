package net.ccbluex.liquidbounce.features.module.modules.other.disablers.other

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.other.disablers.DisablerMode
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

class C08PacketPlayerBlockPlacement : DisablerMode("C08PacketPlayerBlockPlacement") {
    @EventTarget
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C08PacketPlayerBlockPlacement) event.cancelEvent()
    }
}