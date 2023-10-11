package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.visual.HUD
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.features.module.modules.player.FreeCam
import net.ccbluex.liquidbounce.features.module.modules.player.Scaffold
import net.ccbluex.liquidbounce.features.module.modules.world.BedNuker
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.FloatValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.ccbluex.liquidbounce.features.value.ListValue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.world.WorldSettings
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Cylinder
import java.awt.Color
import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@ModuleInfo(name = "KillAura", "Kill Aura", category = ModuleCategory.COMBAT, keyBind = Keyboard.KEY_R)
class KillAura : Module() {
    /**
     * OPTIONS
     */

    // CPS - Attack speed
    private val tagModeValue = ListValue("TagMode", arrayOf("Target", "Priority", "AutoBlock", "None"), "Target")
    private val maxCpsValue: IntegerValue = object : IntegerValue("MaxCPS", 12, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCpsValue.get()
            if (i > newValue) set(i)

            attackDelay = getAttackDelay(minCpsValue.get(), this.get())
        }
    }

    private val minCpsValue: IntegerValue = object : IntegerValue("MinCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCpsValue.get()
            if (i < newValue) set(i)

            attackDelay = getAttackDelay(this.get(), maxCpsValue.get())
        }
    }

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val combatDelayValue = BoolValue("1.9CombatDelay", false)

    // Range
    val rangeValue = object : FloatValue("Range", 3.7f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val throughWallsRangeValue = object : FloatValue("ThroughWallsRange", 1.5f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = rangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val rangeSprintReducementValue = FloatValue("RangeSprintReducement", 0f, 0f, 0.4f)
    private val swingRangeValue = object : FloatValue("SwingRange", 5f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val discoverRangeValue = FloatValue("DiscoverRange", 6f, 0f, 8f)
    private val blinkCheck = BoolValue("BlinkCheck", true)
    private val noScaffValue = BoolValue("NoScaffold", true)
    private val noFlyValue = BoolValue("NoFly", false)
    // Modes
    private val priorityValue = ListValue(
        "Priority",
        arrayOf("Health", "Distance", "Fov", "LivingTime", "Armor", "HurtTime", "RegenAmplifier"),
        "Armor"
    )
    val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Switch")

    // Bypass
    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val attackTimingValue = ListValue("AttackTiming", arrayOf("All", "Pre", "Post"), "All")
    private val keepSprintValue = BoolValue("KeepSprint", true)

    private val noBadPacketsValue = BoolValue("NoBadPackets", false)

    // AutoBlock
    val autoBlockValue = ListValue("AutoBlock", arrayOf("Range", "Damage", "Fake", "Off"), "Off")

    // vanilla will send block packet at pre
    private val autoBlockRangeValue = object : FloatValue("AutoBlockRange", 2.5f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
        }
    }.displayable { autoBlockValue.equals("Range") }
    private val blockTimingValue =          ListValue("BlockTiming", arrayOf("Pre", "Post", "Both"), "Pre").displayable { autoBlockValue.equals("Range") }
    val autoBlockPacketValue =      ListValue("AutoBlockPacket", arrayOf("AfterTick", "AfterAttack", "Vanilla", "NCP", "Grim", "Legit", "OldIntave"), "Vanilla").displayable { autoBlockValue.equals("Range") }
    private val interactAutoBlockValue =    BoolValue("InteractAutoBlock", false).displayable { autoBlockValue.equals("Range") }
    private val smartAutoBlockValue = BoolValue("SmartAutoBlock", false).displayable { autoBlockValue.equals("Range") }
    private val blockRateValue =            IntegerValue("BlockRate", 100, 1, 100).displayable { autoBlockValue.equals("Range") }

    // Raycast
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false).displayable { raycastValue.get() }
    private val livingRaycastValue = BoolValue("LivingRayCast", true).displayable { raycastValue.get() }

    // Bypass
    private val aacValue = BoolValue("AAC", true)
    // TODO: Divide AAC Opinion into three separated opinions

    // Rotations
    private val rotationModeValue = ListValue(
        "RotationMode",
        arrayOf("None", "Center", "Smooth", "Normal", "LockView", "SmoothCenter", "Full"),
        "Smooth"
    )
    private val gcdValue = BoolValue("GDC", false).displayable { !rotationModeValue.equals("None") }
    // TODO: RotationMode Bypass Intave

    private val maxTurnSpeedValue: IntegerValue = object : IntegerValue("MaxTurnSpeed", 90, 0, 90) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = minTurnSpeedValue.get()
            if (v > newValue) set(v)
        }
    }

    private val minTurnSpeedValue: IntegerValue = object : IntegerValue("MinTurnSpeed", 90, 0, 90) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = maxTurnSpeedValue.get()
            if (v < newValue) set(v)
        }
    }
    private val rotationSmoothValue =
        FloatValue("Smooth", 2f, 1f, 10f).displayable { rotationModeValue.equals("Smooth") || rotationModeValue.equals("SmoothCenter") }
    // Random
    private val randomCenterModeValue =
        ListValue("RandomCenter", arrayOf("Off", "Cubic", "Horizonal", "Vertical"), "Off")
    private val randomCenRangeValue = FloatValue("RandomRange", 0.0f, 0.0f, 1.2f)

    // rotation keep
    private val rotationRevValue = BoolValue("RotationReverse", false).displayable { !rotationModeValue.equals("None") }
    private val rotationRevTickValue =
        IntegerValue("RotationReverseTick", 5, 1, 20).displayable { !rotationModeValue.equals("None") }
    private val keepDirectionValue = BoolValue("KeepDirection", true).displayable { !rotationModeValue.equals("None") }
    private val keepDirectionTickValue =
        IntegerValue("KeepDirectionTick", 15, 1, 20).displayable { !rotationModeValue.equals("None") }

    // Strafe
    private val silentRotationValue =
        BoolValue("SilentRotation", true).displayable { !rotationModeValue.equals("None") }
    private val rotationStrafeValue = ListValue(
        "Strafe",
        arrayOf("Off", "Strict", "Silent"),
        "Silent"
    ).displayable { silentRotationValue.get() && !rotationModeValue.equals("None") }
    private val StrafeFixValue = BoolValue("StrafeFix", false).displayable { !rotationStrafeValue.equals("Off") }

    // Backtrace
    private val backtraceValue = BoolValue("Backtrace", false)
    private val backtraceTickValue = IntegerValue("BacktraceTick", 2, 1, 10).displayable { backtraceValue.get() }


    private val hitableValue = BoolValue("AlwaysHitable", true).displayable { !rotationModeValue.equals("None") }
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)

    // Predict
    private val predictValue = BoolValue("Predict", true).displayable { !rotationModeValue.equals("None") }

    private val maxPredictSizeValue: FloatValue = object : FloatValue("MaxPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSizeValue.get()
            if (v > newValue) set(v)
        }
    }.displayable { predictValue.displayable && predictValue.get() } as FloatValue

    private val minPredictSizeValue: FloatValue = object : FloatValue("MinPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSizeValue.get()
            if (v < newValue) set(v)
        }
    }.displayable { predictValue.displayable && predictValue.get() } as FloatValue

    // Bypass
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val fakeSwingValue = BoolValue("FakeSwing", true).displayable { failRateValue.get() != 0f }
    private val noInventoryAttackValue = ListValue("NoInvAttack", arrayOf("Spoof", "CancelRun", "Off"), "Off")

    private val noInventoryDelayValue = IntegerValue("NoInvDelay", 200, 0, 500)
    private val switchDelayValue =
        IntegerValue("SwitchDelay", 15, 1, 2000).displayable { targetModeValue.equals("Switch") }
    private val limitedMultiTargetsValue =
        IntegerValue("LimitedMultiTargets", 0, 0, 50).displayable { targetModeValue.equals("Multi") }

    // Visuals
    private val circleValue = BoolValue("Circle", false)
    private val circleRedValue = IntegerValue("CircleRed", 255, 0, 255).displayable { circleValue.get() }
    private val circleGreenValue = IntegerValue("CircleGreen", 255, 0, 255).displayable { circleValue.get() }
    private val circleBlueValue = IntegerValue("CircleBlue", 255, 0, 255).displayable { circleValue.get() }
    private val circleAlphaValue = IntegerValue("CircleAlpha", 255, 0, 255).displayable { circleValue.get() }
    private val circleThicknessValue = FloatValue("CircleThickness", 2F, 1F, 5F).displayable { circleValue.get() }
    /**
     * MODULE
     */

    // Target
    var target: EntityLivingBase? = null
    var currentTarget: EntityLivingBase? = null
    private var hitable = false
    private var lastPacketSent = false
    private var packetSent = false
    private val prevTargetEntities = mutableListOf<Int>()
    private val discoveredTargets = mutableListOf<EntityLivingBase>()
    private val inRangeDiscoveredTargets = mutableListOf<EntityLivingBase>()
    val canFakeBlock: Boolean
        get() = inRangeDiscoveredTargets.isNotEmpty()

    // Attack delay
    private val attackTimer = MSTimer()
    private val switchTimer = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0

    // Container Delay
    private var containerOpen = -1L

    //third view
    private var previousPerspective: Int = 0

    var perspectiveToggled: Boolean = false

    // Swing
    private val swingTimer = MSTimer()
    private var swingDelay = 0L
    private var canSwing = false

    // Fake block status
    var blockingStatus = false
    private var espAnimation = 0.0
    var strictStrafe = false

    val displayBlocking: Boolean
        get() = blockingStatus || (autoBlockValue.equals("Fake") && canFakeBlock)

    private var predictAmount = 1.0f

    private val getAABB: ((Entity) -> AxisAlignedBB) = {
        var aabb = it.entityBoundingBox
        aabb = if (backtraceValue.get()) LocationCache.getPreviousAABB(
            it.entityId,
            backtraceTickValue.get(),
            aabb
        ) else aabb
        aabb = if (predictValue.get()) aabb.offset(
            (it.posX - it.lastTickPosX) * predictAmount,
            (it.posY - it.lastTickPosY) * predictAmount,
            (it.posZ - it.lastTickPosZ) * predictAmount
        ) else aabb
        aabb
    }

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        strictStrafe = false
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
        if (StrafeFixValue.get() && !rotationStrafeValue.equals("Off")) CrossSine.moduleManager[MovementFix::class.java]!!.state =
            true

    }

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        strictStrafe = false
        CrossSine.moduleManager[TargetStrafe::class.java]!!.doStrafe = false
        target = null
        currentTarget = null
        hitable = false
        packetSent = false
        prevTargetEntities.clear()
        discoveredTargets.clear()
        inRangeDiscoveredTargets.clear()
        attackTimer.reset()
        clicks = 0
        canSwing = false
        swingTimer.reset()

        stopBlocking()
        RotationUtils.setTargetRotationReverse(RotationUtils.serverRotation, 0, 0)
        if (StrafeFixValue.get() && !rotationStrafeValue.equals("Off")) CrossSine.moduleManager[MovementFix::class.java]!!.state =
            false
    }

    fun resetPerspective() {
        perspectiveToggled = false
        mc.gameSettings.thirdPersonView = previousPerspective
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            packetSent = false
        }
        if (mc.thePlayer.isRiding) {
            return
        }

        if (attackTimingValue.equals("All") ||
            (attackTimingValue.equals("Pre") && event.eventState == EventState.PRE) ||
            (attackTimingValue.equals("Post") && event.eventState == EventState.POST)
        ) {
            runAttackLoop()
        }

        if (blockTimingValue.equals("Both") ||
            (blockTimingValue.equals("Pre") && event.eventState == EventState.PRE) ||
            (blockTimingValue.equals("Post") && event.eventState == EventState.POST)
        ) {
            if (packetSent && noBadPacketsValue.get()) {
                return
            }
            // AutoBlock
            if (autoBlockValue.equals("Range") && discoveredTargets.isNotEmpty() && (!autoBlockPacketValue.equals("AfterAttack")
                        || discoveredTargets.any { mc.thePlayer.getDistanceToEntityBox(it) > maxRange }) && canBlock
            ) {
                val target = this.target ?: discoveredTargets.first()
                if (mc.thePlayer.getDistanceToEntityBox(target) <= autoBlockRangeValue.get()) {
                    startBlocking(
                        target,
                        interactAutoBlockValue.get() && (mc.thePlayer.getDistanceToEntityBox(target) < maxRange)
                    )
                } else {
                    if (!mc.thePlayer.isBlocking) {
                        stopBlocking()
                    }
                }
            }
        }

        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()

            return
        }

        if (rotationStrafeValue.equals("Off")) {
            update()
        }
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (cancelRun) return
        strictStrafe = false
        if (!CrossSine.moduleManager[TargetStrafe::class.java]!!.modifyStrafe(event)) {
            strictStrafe = true
        }
        if (rotationStrafeValue.equals("Off") && !mc.thePlayer.isRiding) {
            strictStrafe = false
            return
        }

        // if(event.eventState == EventState.PRE)
        update()

        // TODO: Fix Rotation issue on Strafe POST Event

        if (discoveredTargets.isNotEmpty() && RotationUtils.targetRotation != null) {
            val (yaw) = RotationUtils.targetRotation ?: return
            var strafe = event.strafe
            var forward = event.forward
            var friction = event.friction
            var factor = strafe * strafe + forward * forward
            var calcYaw = yaw

            var calcMoveDir = Math.max(Math.abs(strafe), Math.abs(forward)).toFloat()
            calcMoveDir = calcMoveDir * calcMoveDir
            var calcMultiplier = MathHelper.sqrt_float(calcMoveDir / Math.min(1.0f, calcMoveDir * 2.0f))

            when (rotationStrafeValue.get().lowercase()) {
                "strict" -> {
                    if (strictStrafe) {
                        if (factor >= 1.0E-4F) {
                            factor = MathHelper.sqrt_float(factor)

                            if (factor < 1.0F) {
                                factor = 1.0F
                            }

                            factor = friction / factor
                            strafe *= factor
                            forward *= factor

                            val yawSin = MathHelper.sin((calcYaw * Math.PI / 180F).toFloat())
                            val yawCos = MathHelper.cos((calcYaw * Math.PI / 180F).toFloat())

                            mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                            mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
                        }
                        event.cancelEvent()
                    }
                }

                "silent" -> {
                    if (strictStrafe) {
                        if ((Math.abs(forward) > 0.005 || Math.abs(strafe) > 0.005) && !(Math.abs(forward) > 0.005 && Math.abs(
                                strafe
                            ) > 0.005)
                        ) {
                            friction = friction / calcMultiplier
                        } else if (Math.abs(forward) > 0.005 && Math.abs(strafe) > 0.005) {
                            friction = friction * calcMultiplier
                        }
                    }
                }
            }
        }
    }

    fun update() {
        if (cancelRun) {
            return
        }

        // Update target
        updateTarget()

        if (discoveredTargets.isEmpty()) {
            stopBlocking()
            return
        }

        // Target
        currentTarget = target

        if (!targetModeValue.equals("Switch") && (currentTarget != null && EntityUtils.isSelected(
                currentTarget!!,
                true
            ))
        ) {
            target = currentTarget
        }

        CrossSine.moduleManager[TargetStrafe::class.java]!!.targetEntity = currentTarget ?: return
        CrossSine.moduleManager[TargetStrafe::class.java]!!.doStrafe =
            CrossSine.moduleManager[TargetStrafe::class.java]!!.toggleStrafe()
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(ignoredEvent: UpdateEvent) {
        strictStrafe = !rotationStrafeValue.equals("Off") && !mc.thePlayer.isRiding
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            discoveredTargets.clear()
            inRangeDiscoveredTargets.clear()
            return
        }

        if (noInventoryAttackValue.equals("CancelRun") && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())
        ) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        if (!rotationStrafeValue.equals("Off") && !mc.thePlayer.isRiding) {
            return
        }

        if (mc.thePlayer.isRiding) {
            update()
        }

        if (attackTimingValue.equals("All")) {
            runAttackLoop()
        }
    }
    /**
     * Render event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (circleValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(circleThicknessValue.get())
            GL11.glColor4f(
                circleRedValue.get().toFloat() / 255.0F,
                circleGreenValue.get().toFloat() / 255.0F,
                circleBlueValue.get().toFloat() / 255.0F,
                circleAlphaValue.get().toFloat() / 255.0F
            )
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 5) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * rangeValue.get(),
                    (sin(i * Math.PI / 180.0).toFloat() * rangeValue.get())
                )
            }

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
        }

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            discoveredTargets.clear()
            inRangeDiscoveredTargets.clear()
        }
        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) && currentTarget!!.hurtTime <= hurtTimeValue.get()) {
            clicks++
            attackTimer.reset()
            attackDelay = getAttackDelay(minCpsValue.get(), maxCpsValue.get())
        }
    }


    private fun esp(entity: EntityLivingBase, partialTicks: Float, radius: Float) {
        GL11.glPushMatrix()
        GL11.glDisable(3553)
        RenderUtils.startSmooth()
        GL11.glDisable(2929)
        GL11.glDepthMask(false)
        GL11.glLineWidth(1.0F)
        GL11.glBegin(3)
        val x: Double =
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.renderManager.viewerPosX
        val y: Double =
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.renderManager.viewerPosY
        val z: Double =
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.renderManager.viewerPosZ
        for (i in 0..360) {
            val rainbow = Color(
                Color.HSBtoRGB(
                    (mc.thePlayer.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f,
                    0.7f,
                    1.0f
                )
            )
            GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
            GL11.glVertex3d(
                x + radius * cos(i * 6.283185307179586 / 45.0),
                y + espAnimation,
                z + radius * sin(i * 6.283185307179586 / 45.0)
            )
        }
        GL11.glEnd()
        GL11.glDepthMask(true)
        GL11.glEnable(2929)
        RenderUtils.endSmooth()
        GL11.glEnable(3553)
        GL11.glPopMatrix()
    }

    private fun runAttackLoop() {
        if (clicks <= 0 && canSwing && swingTimer.hasTimePassed(swingDelay)) {
            swingTimer.reset()
            swingDelay = getAttackDelay(minCpsValue.get(), maxCpsValue.get())
            runSwing()
            return
        }

        try {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        } catch (e: java.lang.IllegalStateException) {
            return
        }
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return

        // Settings
        val failRate = failRateValue.get()
        val openInventory = noInventoryAttackValue.equals("Spoof") && mc.currentScreen is GuiInventory
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Check is not hitable or check failrate
        if (hitable && !failHit) {
            // Close inventory when open
            if (openInventory) {
                mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
            }

            // Attack
            if (!targetModeValue.equals("Multi")) {
                attackEntity(currentTarget!!)
            } else {
                inRangeDiscoveredTargets.forEachIndexed { index, entity ->
                    if (limitedMultiTargetsValue.get() == 0 || index < limitedMultiTargetsValue.get()) {
                        attackEntity(entity)
                    }
                }
            }

            if (targetModeValue.equals("Switch")) {
                if (switchTimer.hasTimePassed(switchDelayValue.get().toLong())) {
                    prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
                    switchTimer.reset()
                }
            } else {
                prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
            }

            if (target == currentTarget) {
                target = null
            }

            // Open inventory
            if (openInventory) {
                mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
            }
        } else if (fakeSwingValue.get()) {
            runSwing()
        }
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Settings
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()
        val switchMode = targetModeValue.equals("Switch")

        // Find possible targets
        discoveredTargets.clear()

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity !is EntityLivingBase || !EntityUtils.isSelected(
                    entity,
                    true
                ) || (switchMode && prevTargetEntities.contains(entity.entityId))
            ) {
                continue
            }

            val distance = mc.thePlayer.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= discoverRangeValue.get() && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime) {
                discoveredTargets.add(entity)
            }
        }

        // Sort targets by priority
        when (priorityValue.get().lowercase()) {
            "distance" -> discoveredTargets.sortBy { mc.thePlayer.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> discoveredTargets.sortBy { it.health + it.absorptionAmount } // Sort by health
            "fov" -> discoveredTargets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> discoveredTargets.sortBy { -it.ticksExisted } // Sort by existence
            "armor" -> discoveredTargets.sortBy { it.totalArmorValue } // Sort by armor
            "hurtresistanttime" -> discoveredTargets.sortBy { it.hurtResistantTime } // Sort by armor
            "regenamplifier" -> discoveredTargets.sortBy {
                if (it.isPotionActive(Potion.regeneration)) it.getActivePotionEffect(
                    Potion.regeneration
                ).amplifier else -1
            }
        }

        inRangeDiscoveredTargets.clear()
        inRangeDiscoveredTargets.addAll(discoveredTargets.filter { mc.thePlayer.getDistanceToEntityBox(it) < getRange(it) })

        // Cleanup last targets when no targets found and try again
        if (inRangeDiscoveredTargets.isEmpty() && prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
            return
        }

        // Find best target
        for (entity in discoveredTargets) {
            // Update rotations to current target
            if (!updateRotations(entity)) { // when failed then try another target
                continue
            }

            // Set target to current entity
            if (mc.thePlayer.getDistanceToEntityBox(entity) < maxRange) {
                target = entity
                canSwing = false
                CrossSine.moduleManager[TargetStrafe::class.java]!!.targetEntity = target ?: return
                CrossSine.moduleManager[TargetStrafe::class.java]!!.doStrafe =
                    CrossSine.moduleManager[TargetStrafe::class.java]!!.toggleStrafe()
                return
            }
        }

        target = null
        CrossSine.moduleManager[TargetStrafe::class.java]!!.doStrafe = false
        canSwing = discoveredTargets.find { mc.thePlayer.getDistanceToEntityBox(it) < swingRangeValue.get() } != null
    }

    private fun runSwing() {
        val swing = swingValue.get()
        if (swing.equals("packet", true)) {
            mc.netHandler.addToSendQueue(C0APacketAnimation())
        } else if (swing.equals("normal", true)) {
            mc.thePlayer.swingItem()
        }
    }

    /**
     * Attack [entity]
     * @throws IllegalStateException when bad packets protection
     */
    private fun attackEntity(entity: EntityLivingBase) {
        if (packetSent && noBadPacketsValue.get()) return

        // Call attack event
        val event = AttackEvent(entity)
        CrossSine.eventManager.callEvent(event)
        if (event.isCancelled) return

        // Stop blocking
        preAttack()

        // Attack target
        runSwing()
        packetSent = true
        mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))


        if (keepSprintValue.get()) {
            // Enchant Effect
            if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, entity.creatureAttribute) > 0F) {
                mc.thePlayer.onEnchantmentCritical(entity)
            }
        } else {
            if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR) {
                mc.thePlayer.attackTargetEntityWithCurrentItem(entity)
            }
        }

        postAttack(entity)

        CooldownHelper.resetLastAttackedTicks()
    }

    private fun preAttack() {
        if (mc.thePlayer.isBlocking || blockingStatus) {
            when (autoBlockPacketValue.get().lowercase()) {
                "aftertick", "afterattack"-> stopBlocking()
                "oldintave" -> {
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1))
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    blockingStatus = false
                }
                else -> null
            }
        }
    }

    private fun postAttack(entity: EntityLivingBase) {
        if (mc.thePlayer.isBlocking || (autoBlockValue.equals("Range") && canBlock)) {
            if (blockRateValue.get() > 0 && Random().nextInt(100) <= blockRateValue.get()) {
                if (smartAutoBlockValue.get() && clicks != 1 && mc.thePlayer.hurtTime < 4 && mc.thePlayer.getDistanceToEntityBox(entity) < 4) {
                    return
                }
                when (autoBlockPacketValue.get().lowercase()) {
                    "vanilla", "afterattack", "oldintave" -> startBlocking(entity, interactAutoBlockValue.get() && (mc.thePlayer.getDistanceToEntityBox(entity) < maxRange))
                    else -> null
                }
            }
        }
    }

    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: Entity): Boolean {
        var bn = CrossSine.moduleManager.getModule(BedNuker::class.java)!!
        if (bn.state && bn.currentDamage > 0F) return true
        if (rotationModeValue.equals("None")) {
            return true
        }

        if (predictValue.get()) {
            predictAmount = RandomUtils.nextFloat(maxPredictSizeValue.get(), minPredictSizeValue.get())
        }

        val boundingBox = if (rotationModeValue.get() == "Normal") entity.entityBoundingBox else getAABB(entity)

        val rModes = when (rotationModeValue.get()) {
            "Center", "Smooth" -> "CenterLine"
            "Normal" -> "HalfUp"
            "LockView" -> "CenterSimple"
            "SmoothCenter" -> "CenterHead"
            "Full" -> "Full"
            else -> "LiquidBounce"
        }

        val (_, directRotation) =
            RotationUtils.calculateCenter(
                rModes,
                randomCenterModeValue.get(),
                (randomCenRangeValue.get()).toDouble(),
                boundingBox,
                predictValue.get() && rotationModeValue.get() != "Normal",
                mc.thePlayer.getDistanceToEntityBox(entity) <= throughWallsRangeValue.get()
            ) ?: return false


        var diffAngle = RotationUtils.getRotationDifference(RotationUtils.serverRotation, directRotation)
        if (diffAngle < 0) diffAngle = -diffAngle
        if (diffAngle > 180.0) diffAngle = 180.0

        val calculateSpeed = diffAngle / rotationSmoothValue.get()

        val rotation = when (rotationModeValue.get()) {
            "Center" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation, directRotation,
                (Math.random() * (maxTurnSpeedValue.get() - minTurnSpeedValue.get()) + minTurnSpeedValue.get()).toFloat()
            )

            "LockView" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (1000.0).toFloat()
            )

            "Smooth" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
            )
            "SmoothCenter" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
            )
            "Full" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
            )
            "Normal" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (diffAngle / 1.25).toFloat()
            )

            else -> return true
        }
        if (silentRotationValue.get()) {
            if (rotationRevTickValue.get() > 0 && rotationRevValue.get()) {
                if (keepDirectionValue.get()) {
                    RotationUtils.setTargetRotationReverse(
                        rotation,
                        keepDirectionTickValue.get(),
                        rotationRevTickValue.get()
                    )
                } else {
                    RotationUtils.setTargetRotationReverse(rotation, 1, rotationRevTickValue.get())
                }
            } else {
                if (keepDirectionValue.get()) {
                    RotationUtils.setTargetRotation(rotation, keepDirectionTickValue.get())
                } else {
                    RotationUtils.setTargetRotation(rotation, 1)
                }
            }
            if (gcdValue.get()) {
                rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity)
            }
        } else {
            rotation.toPlayer(mc.thePlayer, gcdValue.get())
        }
        return true
    }

    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        if (hitableValue.get()) {
            hitable = true
            return
        }
        // Disable hitable check if turn speed is zero
        if (maxTurnSpeedValue.get() <= 0F) {
            hitable = true
            return
        }

        val reach = maxRange.toDouble()

        if (raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach) {
                (!livingRaycastValue.get() || it is EntityLivingBase && it !is EntityArmorStand) &&
                        (EntityUtils.isSelected(
                            it,
                            true
                        ) || raycastIgnoredValue.get() || aacValue.get() && mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                            it,
                            it.entityBoundingBox
                        ).isNotEmpty())
            }

            if (raycastValue.get() && raycastedEntity is EntityLivingBase &&
                !EntityUtils.isFriend(raycastedEntity)
            ) {
                currentTarget = raycastedEntity
            }

            hitable = if (!rotationModeValue.equals("None")) currentTarget == raycastedEntity else true
        } else {
            hitable = RotationUtils.isFaced(currentTarget, reach)
        }
    }

    /**
     * Start blocking
     */
    private fun startBlocking(interactEntity: Entity, interact: Boolean) {
        if (autoBlockValue.equals("Range") && mc.thePlayer.getDistanceToEntityBox(interactEntity) > autoBlockRangeValue.get()) {
            return
        }

        if (blockingStatus) {
            return
        }
        if (packetSent && noBadPacketsValue.get()) {
            return
        }
        if (autoBlockPacketValue.equals("NCP")) {
            PacketUtils.sendPacketNoEvent(
                C08PacketPlayerBlockPlacement(
                    BlockPos(-1, -1, -1),
                    255,
                    null,
                    0.0f,
                    0.0f,
                    0.0f
                )
            )
            blockingStatus = true
            return
        }
        if (autoBlockPacketValue.get().equals("grim", true)) {
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
        }
        if (interact) {
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, interactEntity.positionVector))
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, C02PacketUseEntity.Action.INTERACT))
        }

        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
        blockingStatus = true
        packetSent = true
    }

    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        if (blockingStatus) {
            if (packetSent && noBadPacketsValue.get()) {
                return
            }
            mc.netHandler.addToSendQueue(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    if (MovementUtils.isMoving()) BlockPos(-1, -1, -1) else BlockPos.ORIGIN,
                    EnumFacing.DOWN
                )
            )
            blockingStatus = false
            packetSent = true
        }
    }

    /**
     * Attack Delay
     */
    private fun getAttackDelay(minCps: Int, maxCps: Int): Long {
        var delay = TimeUtils.randomClickDelay(minCps.coerceAtMost(maxCps), minCps.coerceAtLeast(maxCps))
        if (combatDelayValue.get()) {
            var value = 4.0
            if (mc.thePlayer.inventory.getCurrentItem() != null) {
                when (mc.thePlayer.inventory.getCurrentItem().item) {
                    is ItemSword -> {
                        value -= 2.4
                    }

                    is ItemPickaxe -> {
                        value -= 2.8
                    }

                    is ItemAxe -> {
                        value -= 3
                    }
                }
            }
            delay = delay.coerceAtLeast((1000 / value).toLong())
        }
        return delay
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        get() = mc.thePlayer.isSpectator || !isAlive(mc.thePlayer)
                || (blinkCheck.get() && CrossSine.moduleManager[Blink::class.java]!!.state) || CrossSine.moduleManager[FreeCam::class.java]!!.state ||
                (noScaffValue.get() && CrossSine.moduleManager[Scaffold::class.java]!!.state) || (noFlyValue.get() && CrossSine.moduleManager[Flight::class.java]!!.state) || (noInventoryAttackValue.equals(
            "CancelRun"
        ) && (mc.currentScreen is GuiContainer ||
                System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get()))


    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0 ||
            aacValue.get() && entity.hurtTime > 3

    /**
     * Check if player is able to block
     */
    private val canBlock: Boolean
        get() = mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    /**
     * Range
     */
    private val maxRange: Float
        get() = max(rangeValue.get(), throughWallsRangeValue.get())

    private fun getRange(entity: Entity) =
        (if (mc.thePlayer.getDistanceToEntityBox(entity) >= throughWallsRangeValue.get()) rangeValue.get() else throughWallsRangeValue.get()) - if (mc.thePlayer.isSprinting) rangeSprintReducementValue.get() else 0F

    /**
     * HUD Tag
     */

    override val tag: String?
        get() = if (!tagModeValue.equals("None")) {
            if (tagModeValue.equals("Target")) {
                targetModeValue.get()
            } else {
                if (tagModeValue.equals("Priority")) {
                    priorityValue.get()
                } else {
                    if (autoBlockValue.equals("Range")) {
                        autoBlockPacketValue.get()
                    } else null
                }
            }
        } else null
}