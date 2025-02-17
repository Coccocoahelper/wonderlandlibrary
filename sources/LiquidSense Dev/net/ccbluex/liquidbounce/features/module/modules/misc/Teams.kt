/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemArmor

@ModuleInfo(name = "Teams", description = "Prevents Killaura from attacking team mates.", category = ModuleCategory.MISC)
class Teams : Module() {
    private val scoreboardValue = BoolValue("ScoreboardTeam", true)
    private val colorValue = BoolValue("Color", true)
    private val armorColorValue = BoolValue("ArmorColor", false)

    /**
     * Check if [entity] is in your own team using scoreboard or name color
     */
    fun isInYourTeam(entity: EntityLivingBase): Boolean {
        mc.thePlayer ?: return false

        if (scoreboardValue.get() && mc.thePlayer.team != null && entity.team != null &&
                mc.thePlayer.team.isSameTeam(entity.team))
            return true



        if (colorValue.get() && mc.thePlayer.displayName != null && entity.displayName != null) {
            val targetName = entity.displayName.formattedText.replace("§r", "")
            val clientName = mc.thePlayer.displayName.formattedText.replace("§r", "")
            return targetName.startsWith("§${clientName[1]}")
        }



        return false
    }
}