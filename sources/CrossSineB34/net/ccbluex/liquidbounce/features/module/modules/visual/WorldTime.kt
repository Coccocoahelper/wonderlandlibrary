/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.visual

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.minecraft.network.play.server.S03PacketTimeUpdate
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "WorldTime", spacedName = "World Time", category = ModuleCategory.VISUAL)
class WorldTime : Module() {
    private val customWorldTimeValue = IntegerValue("CustomTime", 1, 0, 20)
    private val arrowValue = BoolValue("ArrowButton", false)
    private var c = false
    private var a = false
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.theWorld.worldTime = customWorldTimeValue.get().toLong() * 1000

        if (arrowValue.get()) {
            if (!c && Keyboard.isKeyDown(203) && customWorldTimeValue.value != 0) {
                customWorldTimeValue.set(customWorldTimeValue.value - 1)
            }
            if (!a && Keyboard.isKeyDown(205) && customWorldTimeValue.value != 20) {
                customWorldTimeValue.set(customWorldTimeValue.value + 1)
            }
            c = Keyboard.isKeyDown(203)
            a = Keyboard.isKeyDown(205)
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S03PacketTimeUpdate) {
            event.cancelEvent()
        }
    }
}