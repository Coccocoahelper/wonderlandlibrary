package net.ccbluex.liquidbounce.features.module.modules.player.phases.vanilla

import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.player.phases.PhaseMode
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.timer.tickTimer
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.util.BlockPos
import kotlin.math.cos
import kotlin.math.sin

class ClipPhase : PhaseMode("Clip") {
    private val tickTimer = tickTimer()
    override fun onEnable() {
        tickTimer.reset()
    }

    override fun onUpdate(event: UpdateEvent) {
        val isInsideBlock =
            BlockUtils.collideBlockIntersects(mc.thePlayer.entityBoundingBox) { block: Block? -> block !is BlockAir }
        if (isInsideBlock) {
            mc.thePlayer.noClip = true
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.onGround = true
        }
        tickTimer.update()

        if (!tickTimer.hasTimePassed(2) || !mc.thePlayer.isCollidedHorizontally || !(!isInsideBlock || mc.thePlayer.isSneaking)) return
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        val oldX = mc.thePlayer.posX
        val oldZ = mc.thePlayer.posZ
        var i = 1
        while (i <= 10) {
            val x = -sin(yaw) * i
            val z = cos(yaw) * i
            if (BlockUtils.getBlock(BlockPos(oldX + x, mc.thePlayer.posY, oldZ + z)) is BlockAir
                && BlockUtils.getBlock(BlockPos(oldX + x, mc.thePlayer.posY + 1, oldZ + z)) is BlockAir) {
                mc.thePlayer.setPosition(oldX + x, mc.thePlayer.posY, oldZ + z)
                break
            }
            i++
        }
        tickTimer.reset()
    }
}