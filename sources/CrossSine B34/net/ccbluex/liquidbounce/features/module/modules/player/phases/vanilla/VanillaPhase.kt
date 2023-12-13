package net.ccbluex.liquidbounce.features.module.modules.player.phases.vanilla

import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.player.phases.PhaseMode
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.timer.tickTimer
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import kotlin.math.cos
import kotlin.math.sin

class VanillaPhase : PhaseMode("Vanilla") {
    private val tickTimer = tickTimer()
    override fun onEnable() {
        tickTimer.reset()
    }
    override fun onUpdate(event: UpdateEvent) {
        val isInsideBlock = BlockUtils.collideBlockIntersects(mc.thePlayer.entityBoundingBox) { block: Block? -> block !is BlockAir }
        if(isInsideBlock) {
            mc.thePlayer.noClip = true
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.onGround = true
        }
        tickTimer.update()

        if (!mc.thePlayer.onGround || !tickTimer.hasTimePassed(2) || !mc.thePlayer.isCollidedHorizontally || !(!isInsideBlock || mc.thePlayer.isSneaking)) return
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        val x = -sin(yaw) * 0.04
        val z = cos(yaw) * 0.04
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z)
        tickTimer.reset()
    }
}