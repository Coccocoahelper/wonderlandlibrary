package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.misc.Teams
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemSword
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S45PacketTitle
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.world.WorldSettings
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.math.*

@ModuleInfo(name = "Aura", description = "Automatically attacks targets around you.",
    category = ModuleCategory.COMBAT, keyBind = Keyboard.KEY_R)
class KillAura : Module() {
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 12, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }
    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 10, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    val rangeValue = FloatValue("Range", 3.2f, 1f, 8f)
    private val throughWallsRangeValue = FloatValue("ThroughWallsRange", 0f, 0f, 8f)
    private val rangeSprintReducementValue = FloatValue("RangeSprintReducement", 0.02f, 0f, 0.4f)
    private val rotations = ListValue("RotationMode", arrayOf("Vanilla", "BackTrack", "Full", "Spin", "Shake", "None"), "Full")
    private val spinHurtTimeValue = IntegerValue("Spin-HitHurtTime", 10, 0, 10)
    private val maxSpinSpeed: FloatValue = object : FloatValue("MaxSpinSpeed", 20.04f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minSpinSpeed.get()
            if (v > newValue) set(v)
        }
    }
    private val minSpinSpeed: FloatValue = object : FloatValue("MinSpinSpeed", 20.04f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxSpinSpeed.get()
            if (v < newValue) set(v)
        }
    }
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 54.59f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }
    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 37.02f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }
    //private val roundTurnAngle = BoolValue("RoundAngle", false)
    //private val roundAngleDirs = IntegerValue("RoundAngle-Directions", 20, 2, 90)
    private val noSendRot = BoolValue("NoSendRotation", false)
    private val noHitCheck = BoolValue("HitBypass", false)
    private val blinkCheck = BoolValue("BlinkCheck", false)
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction", "LivingTime", "Armor", "HurtResistance", "HurtTime", "HealthAbsorption", "RegenAmplifier"), "Direction")
    val targetModeValue = ListValue("TargetMode", arrayOf("AAC", "NCP", "Multi"), "AAC")
    private val switchDelayValue = IntegerValue("SwitchDelay", 1000, 1, 2000)
    private val swingValue = BoolValue("Swing", true)
    private val swingOrderValue = BoolValue("1.9OrderCheck", false)
    private val keepSprintValue = BoolValue("KeepSprint", true)
    private val autoBlockModeValue = ListValue("AutoBlock", arrayOf("None", "Packet", "AfterTick", "NCP", "OldHypixel", "TestHypixel","Vanilla"), "Packet")
    //private val displayAutoBlockSettings = BoolValue("Open-AutoBlock-Settings", false)
    private val interactAutoBlockValue = BoolValue("InteractAutoBlock", false)
    private val verusAutoBlockValue = BoolValue("VerusAutoBlock", true)
    private val abThruWallValue = BoolValue("AutoBlockThroughWalls", true)
    private val smartAutoBlockValue = BoolValue("SmartAutoBlock", false) // thanks czech
    private val smartABItemValue = BoolValue("SmartAutoBlock-ItemCheck", true)
    private val smartABFacingValue = BoolValue("SmartAutoBlock-FacingCheck", true)
    private val smartABRangeValue = FloatValue("SmartAB-Range", 3.5F, 3F, 8F)
    private val smartABTolerationValue = FloatValue("SmartAB-Toleration", 0F, 0F, 2F)
    private val afterTickPatchValue = BoolValue("AfterTickPatch", true)
    private val blockRate = IntegerValue("BlockRate", 100, 1, 100)
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false)
    private val livingRaycastValue = BoolValue("LivingRayCast", false)
    private val aacValue = BoolValue("AAC", false)
    private val rotationModeValue = ListValue(
        "RotationMode",
        arrayOf("None", "LiquidBounce", "ForceCenter", "SmoothCenter", "SmoothLiquid", "LockView", "OldMatrix"),
        "LiquidBounce"
    )
    private val silentRotationValue = BoolValue("SilentRotation", true)
    val rotationStrafeValue = ListValue("Strafe", arrayOf("Off", "Strict", "Silent"), "Off")
    private val backtraceValue = BoolValue("Backtrace", false)
    private val backtraceTickValue = IntegerValue("BacktraceTick", 2, 1, 10)
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)
    private val predictValue = BoolValue("Predict", false)
    private val maxPredictSize: FloatValue = object : FloatValue("MaxPredictSize", 1.5f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSize.get()
            if (v > newValue) set(v)
        }
    }
    private val minPredictSize: FloatValue = object : FloatValue("MinPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSize.get()
            if (v < newValue) set(v)
        }
    }
    private val predictPlayerValue = BoolValue("PredictPlayer", true)
    private val maxPredictPlayerSizeValue: FloatValue = object : FloatValue("MaxPredictPlayerSize", 1f, -1f, 3f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictPlayerSizeValue.get()
            if (v > newValue) set(v)
        }
    }
    private val minPredictPlayerSizeValue: FloatValue = object : FloatValue("MinPredictPlayerSize", 1f, -1f, 3f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictPlayerSizeValue.get()
            if (v < newValue) set(v)
        }
    }
    private val rotationSmoothModeValue = ListValue("SmoothMode", arrayOf("Custom", "Line", "Quad", "Sine", "QuadSine"), "Custom")
    private val rotationSmoothValue = FloatValue("CustomSmooth", 2f, 1f, 10f)
    private val randomCenterValue = BoolValue("RandomCenter", false)
    private val randomCenterModeValue = ListValue("RandomCenter", arrayOf("Off", "Cubic", "Horizonal", "Vertical"), "Off")
    private val randomCenRangeValue = FloatValue("RandomRange", 0.0f, 0.0f, 1.2f)
    private val rotationRevValue = BoolValue("RotationReverse", false)
    private val rotationRevTickValue = IntegerValue("RotationReverseTick", 5, 1, 20)
    private val keepDirectionValue = BoolValue("KeepDirection", true)
    private val keepDirectionTickValue = IntegerValue("KeepDirectionTick", 15, 1, 20)
    private val randomCenterNewValue = BoolValue("NewCalc", true)
    private val minRand: FloatValue = object : FloatValue("MinMultiply", 0.8f, 0f, 2f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxRand.get()
            if (v < newValue) set(v)
        }
    }
    private val maxRand: FloatValue = object : FloatValue("MaxMultiply", 0.8f, 0f, 2f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minRand.get()
            if (v > newValue) set(v)
        }
    }
    private val outborderValue = BoolValue("Outborder", false)
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val fakeSwingValue = BoolValue("FakeSwing", true)
    private val noInventoryAttackValue = BoolValue("NoInvAttack", false)
    private val noInventoryDelayValue = IntegerValue("NoInvDelay", 0, 0, 500)
    private val limitedMultiTargetsValue = IntegerValue("LimitedMultiTargets", 0, 0, 50)
    private val noScaffValue = BoolValue("NoScaffold", true)
    private val debugValue = BoolValue("Debug", false)
    //private val fakeBlock = BoolValue("Fake-Block", true)
    val blockEverything = BoolValue("Block-Everything", false)
    private val circleValue = BoolValue("Circle", false)
    private val accuracyValue = IntegerValue("Accuracy", 59, 0, 59)
    private val fakeSharpValue = BoolValue("FakeSharp", true)
    private val fakeSharpSword = BoolValue("FakeSharp-SwordOnly", true)
    private val red = IntegerValue("Red", 255, 0, 255)
    private val green = IntegerValue("Green", 255, 0, 255)
    private val blue = IntegerValue("Blue", 255, 0, 255)
    private val alpha = IntegerValue("Alpha", 255, 0, 255)

    // Target
    var target: EntityLivingBase? = null
    var currentTarget: EntityLivingBase? = null
    var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()

    private var markEntity: EntityLivingBase? = null
    private val markTimer = MSTimer()

    // Attack delay
    private val attackTimer = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0

    private var lastHitTick = 0

    // Container Delay
    private var containerOpen = -1L

    // Fake block status
    var blockingStatus = false
    var verusBlocking = false
    var fakeBlock = false

    var smartBlocking = false

    // yJitter
    var yJitter = 0.0
    var yJitterStatus = true
    private val canSmartBlock: Boolean
        get() = !smartAutoBlockValue.get() || smartBlocking
    private var predictAmount = 1.0f
    private var predictPlayerAmount = 1.0f
    var spinYaw = 0F

    // I don't know
    //var focusEntityName = mutableListOf<String>()

    /**
     * Enable kill aura module
     */
    private val getAABB: ((Entity) -> AxisAlignedBB) = {
        var aabb = it.entityBoundingBox
        aabb = if (backtraceValue.get()) LocationCache.getPreviousAABB(it.entityId, backtraceTickValue.get(), aabb) else aabb
        aabb = if (predictValue.get()) aabb.offset((it.posX - it.lastTickPosX) * predictAmount, (it.posY - it.lastTickPosY) * predictAmount, (it.posZ - it.lastTickPosZ) * predictAmount) else aabb
        aabb = if (predictPlayerValue.get()) aabb.offset(mc.thePlayer.motionX * predictPlayerAmount * -1f, mc.thePlayer.motionY * predictPlayerAmount * -1f, mc.thePlayer.motionZ * predictPlayerAmount * -1f) else aabb
        aabb
    }

    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
        verusBlocking = false
        smartBlocking = false
        yJitter = 0.0
        yJitterStatus = true

    }

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        target = null
        currentTarget = null
        hitable = false
        prevTargetEntities.clear()
        attackTimer.reset()
        clicks = 0

        stopBlocking()
        if (verusBlocking && !blockingStatus && !mc.thePlayer.isBlocking) {
            verusBlocking = false
            if (verusAutoBlockValue.get())
                PacketUtils.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
        }
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()

            // AutoBlock
            if (autoBlockModeValue.get().equals("AfterTick", true) && canBlock)
                startBlocking(currentTarget!!, hitable)
        }
        if (autoBlockModeValue.get().equals("TestHypixel", true) && canBlock)
            if (mc.thePlayer.swingProgressInt == 1) {
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            } else if (mc.thePlayer.swingProgressInt == 2) {
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()))
            }

        if (rotationStrafeValue.get().equals("Off", true))
            update()
    }


    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (rotationStrafeValue.get().equals("Off", true))
            return

        update()

        if (currentTarget != null && RotationUtils.targetRotation != null) {
            when (rotationStrafeValue.get().toLowerCase()) {
                "strict" -> {
                    val (yaw) = RotationUtils.targetRotation ?: return
                    var strafe = event.strafe
                    var forward = event.forward
                    val friction = event.friction

                    var f = strafe * strafe + forward * forward

                    if (f >= 1.0E-4F) {
                        f = MathHelper.sqrt_float(f)

                        if (f < 1.0F)
                            f = 1.0F

                        f = friction / f
                        strafe *= f
                        forward *= f

                        val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                        val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                        mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                        mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
                    }
                    event.cancelEvent()
                }
                "silent" -> {
                    update()

                    RotationUtils.targetRotation.applyStrafeToPlayer(event)
                    event.cancelEvent()
                }
            }
        }
    }


    fun update() {
        if (cancelRun || (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())))
            return

        // Update target
        updateTarget()

        if (target == null) {
            stopBlocking()
            return
        }

        // Target
        currentTarget = target

        if (!targetModeValue.get().equals("NCP", ignoreCase = true) && isEnemy(currentTarget))
            target = currentTarget
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (verusBlocking
            && ((packet is C07PacketPlayerDigging
                    && packet.status == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)
                    || packet is C08PacketPlayerBlockPlacement)
            && verusAutoBlockValue.get())
            event.cancelEvent()

        if (packet is C09PacketHeldItemChange)
            verusBlocking = false
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        // Update yJitter
        if ((yJitterStatus && yJitter >= 1) || (!yJitterStatus && yJitter <= -1)) {
            yJitterStatus = !yJitterStatus
        }
        yJitter += if (yJitterStatus) 0.1 else -0.1

        updateKA()

        smartBlocking = false
        if (smartAutoBlockValue.get() && target != null) {
            val smTarget = target!!
            if (!smartABItemValue.get() || (smTarget.heldItem != null && smTarget.heldItem.getItem() != null && (smTarget.heldItem.getItem() is ItemSword || smTarget.heldItem.getItem() is ItemAxe))) {
                if (mc.thePlayer.getDistanceToEntityBox(smTarget) < smartABRangeValue.get()) {
                    if (smartABFacingValue.get()) {
                        if (smTarget.rayTrace(smartABRangeValue.get().toDouble(), 1F).typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
                            val eyesVec = smTarget.getPositionEyes(1F)
                            val lookVec = smTarget.getLook(1F)
                            val pointingVec = eyesVec.addVector(lookVec.xCoord * smartABRangeValue.get(), lookVec.yCoord * smartABRangeValue.get(), lookVec.zCoord * smartABRangeValue.get())
                            val border = mc.thePlayer.getCollisionBorderSize() + smartABTolerationValue.get()
                            val bb = mc.thePlayer.entityBoundingBox.expand(border.toDouble(), border.toDouble(), border.toDouble())
                            smartBlocking = bb.calculateIntercept(eyesVec, pointingVec) != null || bb.intersectsWith(smTarget.entityBoundingBox)
                        }
                    } else
                        smartBlocking = true
                }
            }
        }

        if (blockingStatus || mc.thePlayer.isBlocking())
            verusBlocking = true
        else if (verusBlocking) {
            verusBlocking = false
            if (verusAutoBlockValue.get())
                PacketUtils.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
        }
    }

    object CombatListener : Listenable {
        var syncEntity: EntityLivingBase? = null
        var killCounts = 0
        var totalPlayed = 0
        var win = 0
        var startTime = System.currentTimeMillis()

        @EventTarget
        private fun onAttack(event: AttackEvent) { syncEntity = event.targetEntity as EntityLivingBase?
        }

        @EventTarget
        private fun onUpdate(event: UpdateEvent) {
            if(syncEntity != null && syncEntity!!.isDead) {
                ++killCounts
                syncEntity = null
            }
        }

        @EventTarget(ignoreCondition = true)
        private fun onPacket(event: PacketEvent) {
            val packet = event.packet
            if (event.packet is C00Handshake) startTime = System.currentTimeMillis()

            if (packet is S45PacketTitle) {
                val title = packet.message.formattedText
                if(title.contains("Winner")){
                    win++
                }
                if(title.contains("BedWar")){
                    totalPlayed++
                }
                if(title.contains("SkyWar")){
                    totalPlayed++
                }
            }
        }

        override fun handleEvents() = true

        init {
            LiquidBounce.eventManager.registerListener(this)
        }
    }

    private fun updateKA() {
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        if (target != null && currentTarget != null) {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
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
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(1F)
            GL11.glColor4f(red.get().toFloat() / 255.0F, green.get().toFloat() / 255.0F, blue.get().toFloat() / 255.0F, alpha.get().toFloat() / 255.0F)
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 60 - accuracyValue.get()) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(Math.cos(i * Math.PI / 180.0).toFloat() * rangeValue.get(), (Math.sin(i * Math.PI / 180.0).toFloat() * rangeValue.get()))
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
            return
        }

        if (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        target ?: return

        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) &&
            currentTarget!!.hurtTime <= hurtTimeValue.get()) {
            clicks++
            attackTimer.reset()
            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }

    /**
     * Handle entity move
     */
    @EventTarget
    fun onEntityMove(event: EntityMovementEvent) {
        val movedEntity = event.movedEntity

        if (target == null || movedEntity != currentTarget)
            return

        updateHitable()
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return

        // Settings
        val failRate = failRateValue.get()
        val swing = swingValue.get()
        val multi = targetModeValue.get().equals("Multi", ignoreCase = true)
        val openInventory = aacValue.get() && mc.currentScreen is GuiInventory
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Close inventory when open
        if (openInventory)
            mc.netHandler.addToSendQueue(C0DPacketCloseWindow())

        // Check is not hitable or check failrate
        if (!hitable || failHit) {
            if (swing && (fakeSwingValue.get() || failHit))
                mc.thePlayer.swingItem()
        } else {
            // Attack
            if (!multi) {
                attackEntity(currentTarget!!)
            } else {
                var targets = 0

                for (entity in mc.theWorld.loadedEntityList) {
                    val distance = mc.thePlayer.getDistanceToEntityBox(entity)

                    if (entity is EntityLivingBase && isEnemy(entity) && distance <= getRange(entity)) {
                        attackEntity(entity)

                        targets += 1

                        if (limitedMultiTargetsValue.get() != 0 && limitedMultiTargetsValue.get() <= targets)
                            break
                    }
                }
            }

            prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)

            if (target == currentTarget)
                target = null
        }

        if(targetModeValue.get().equals("NCP", ignoreCase = true) && attackTimer.hasTimePassed((switchDelayValue.get()).toLong())) {
            if(switchDelayValue.get() != 0) {
                prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
                attackTimer.reset()
            }
        }

        // Open inventory
        if (openInventory)
            mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Reset fixed target to null
        var searchTarget = null

        // Settings
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()
        val switchMode = targetModeValue.get().equals("NCP", ignoreCase = true)

        // Find possible targets
        val targets = mutableListOf<EntityLivingBase>()
        val lookingTargets = mutableListOf<EntityLivingBase>()

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity !is EntityLivingBase || !isEnemy(entity) || (switchMode && prevTargetEntities.contains(entity.entityId))/* || (!focusEntityName.isEmpty() && !focusEntityName.contains(entity.name.toLowerCase()))*/)
                continue

            val distance = mc.thePlayer.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= maxRange && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime)
                targets.add(entity)
        }

        // Sort targets by priority
        when (priorityValue.get().toLowerCase()) {
            "distance" -> targets.sortBy { mc.thePlayer.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> targets.sortBy { it.health } // Sort by health
            "direction" -> targets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> targets.sortBy { -it.ticksExisted } // Sort by existence
            "hurtresistance" -> targets.sortBy { it.hurtResistantTime } // Sort by armor hurt time
            "hurttime" -> targets.sortBy { it.hurtTime } // Sort by hurt time
            "healthabsorption" -> targets.sortBy { it.health + it.absorptionAmount } // Sort by full health with absorption effect
            "regenamplifier" -> targets.sortBy { if (it.isPotionActive(Potion.regeneration)) it.getActivePotionEffect(Potion.regeneration).amplifier else -1 }
        }

        var found = false

        // Find best target
        for (entity in targets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue

            // Set target to current entity
            target = entity
            found = true
            break
        }

        if (found) {
            if (rotations.get().equals("spin", true)) {
                spinYaw += RandomUtils.nextFloat(minSpinSpeed.get(), maxSpinSpeed.get())
                spinYaw = MathHelper.wrapAngleTo180_float(spinYaw)
                val rot = Rotation(spinYaw, 90F)
                RotationUtils.setTargetRotation(rot, 0)
            }
            return
        }

        if (searchTarget != null) {
            if (target != searchTarget) target = searchTarget
            return
        } else {
            target = null
        }

        // Cleanup last targets when no target found and try again
        if (prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
        }
    }

    /**
     * Check if [entity] is selected as enemy with current target options and other modules
     */
    public fun isEnemy(entity: Entity?): Boolean {
        if (entity is EntityLivingBase && (EntityUtils.targetDead || isAlive(entity)) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible())
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                if (entity.isSpectator || AntiBot.isBot(entity))
                    return false

                if (EntityUtils.isFriend(entity) && !LiquidBounce.moduleManager[NoFriends::class.java]!!.state)
                    return false

                val teams = LiquidBounce.moduleManager[Teams::class.java] as Teams

                return !teams.state || !teams.isInYourTeam(entity)
            }

            return EntityUtils.targetMobs && EntityUtils.isMob(entity) || EntityUtils.targetAnimals &&
                    EntityUtils.isAnimal(entity)
        }

        return false
    }

    /**
     * Attack [entity]
     */
    private fun attackEntity(entity: EntityLivingBase) {
        // Stop blocking
        if (mc.thePlayer.isBlocking || blockingStatus)
            stopBlocking()

        // Call attack event
        LiquidBounce.eventManager.callEvent(AttackEvent(entity))

        markEntity = entity

        if (autoBlockModeValue.equals("Vanilla") && (mc.thePlayer.isBlocking || blockingStatus)) {
            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
        }

        // Get rotation and send packet if possible
        if (rotations.get().equals("spin", true) && !noSendRot.get())
        {
            val targetedRotation = getTargetRotation(entity) ?: return
            mc.netHandler.addToSendQueue(C03PacketPlayer.C05PacketPlayerLook(targetedRotation.yaw, targetedRotation.pitch, mc.thePlayer.onGround))

            if (debugValue.get())
                ClientUtils.displayChatMessage("[KillAura] Silent rotation change.")
        }

        // Attack target
        if (swingValue.get()) // version fix
            mc.thePlayer.swingItem()

        mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        if (swingValue.get() && swingOrderValue.get())
            mc.thePlayer.swingItem()

        if (keepSprintValue.get()) {
            // Critical Effect
            if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder &&
                !mc.thePlayer.isInWater && !mc.thePlayer.isPotionActive(Potion.blindness) && !mc.thePlayer.isRiding)
                mc.thePlayer.onCriticalHit(entity)

            // Enchant Effect
            if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, entity.creatureAttribute) > 0F)
                mc.thePlayer.onEnchantmentCritical(entity)
        } else {
            if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR)
                mc.thePlayer.attackTargetEntityWithCurrentItem(entity)
        }

        // Extra critical effects
        val criticals = LiquidBounce.moduleManager[Criticals::class.java] as Criticals

        for (i in 0..2) {
            // Critical Effect
            if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null || criticals.state && criticals.msTimer.hasTimePassed(criticals.delayValue.toLong()) && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb)
                mc.thePlayer.onCriticalHit(target)

            // Enchant Effect
            if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, target!!.creatureAttribute) > 0.0f || (fakeSharpValue.get() && (!fakeSharpSword.get() || canBlock)))
                mc.thePlayer.onEnchantmentCritical(target)
        }

        // Start blocking after attack
        if ((!afterTickPatchValue.get() || !autoBlockModeValue.get().equals("AfterTick", true)) && (mc.thePlayer.isBlocking || canBlock))
            startBlocking(entity, interactAutoBlockValue.get())
    }

    private fun updateRotations(entity: Entity): Boolean {
        if (rotationModeValue.equals("None")) {
            return true
        }

        if (predictValue.get()) {
            predictAmount = RandomUtils.nextFloat(maxPredictSize.get(), minPredictSize.get())
        }
        if (predictPlayerValue.get()) {
            predictPlayerAmount = RandomUtils.nextFloat(maxPredictPlayerSizeValue.get(), minPredictPlayerSizeValue.get())
        }

        val boundingBox = if (rotationModeValue.get() == "Test") entity.entityBoundingBox else getAABB(entity)

        val rModes = when (rotationModeValue.get()) {
            "LiquidBounce", "SmoothLiquid", "Derp" -> "LiquidBounce"
            "ForceCenter", "SmoothCenter", "OldMatrix", "Spin", "FastSpin" -> "CenterLine"
            "LockView" -> "CenterSimple"
            "Test" -> "HalfUp"
            else -> "LiquidBounce"
        }

        val (_, directRotation) =
            RotationUtils.calculateCenter(
                rModes,
                randomCenterModeValue.get(),
                (randomCenRangeValue.get()).toDouble(),
                boundingBox,
                predictValue.get() && rotationModeValue.get() != "Test",
                mc.thePlayer.getDistanceToEntityBox(entity) <= throughWallsRangeValue.get()
            ) ?: return false

        if (rotationModeValue.get() == "OldMatrix") directRotation.pitch = 89.9f

        var diffAngle = RotationUtils.getRotationDifference(RotationUtils.serverRotation, directRotation)
        if (diffAngle < 0) diffAngle = -diffAngle
        if (diffAngle > 180.0) diffAngle = 180.0

        val calculateSpeed = when (rotationSmoothModeValue.get()) {
            "Custom" -> diffAngle / rotationSmoothValue.get()
            "Line" -> (diffAngle / 360) * maxTurnSpeed.get() + (1 - diffAngle / 360) * minTurnSpeed.get()
            //"Quad" -> Math.pow((diffAngle / 180.0), 2.0) * maxTurnSpeedValue.get() + (1 - Math.pow((diffAngle / 180.0), 2.0)) * minTurnSpeedValue.get()
            "Quad" -> (diffAngle / 360.0).pow(2.0) * maxTurnSpeed.get() + (1 - (diffAngle / 360.0).pow(2.0)) * minTurnSpeed.get()
            "Sine" -> (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5) * maxTurnSpeed.get() + (cos(diffAngle / 360 * Math.PI) * 0.5 + 0.5) * minTurnSpeed.get()
            //"QuadSine" -> Math.pow(-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5, 2.0) * maxTurnSpeedValue.get() + (1 - Math.pow(-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5, 2.0)) * minTurnSpeedValue.get()
            "QuadSine" -> (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5).pow(2.0) * maxTurnSpeed.get() + (1 - (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5).pow(2.0)) * minTurnSpeed.get()
            else -> 360.0
        }

        val rotation = when (rotationModeValue.get()) {
            "LiquidBounce", "ForceCenter" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation, directRotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )
            "LockView" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (180.0).toFloat()
            )
            "SmoothCenter", "SmoothLiquid", "OldMatrix" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
            )
            "Test" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
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
        } else {
            rotation.toPlayer(mc.thePlayer)
        }
        return true
    }

    /**
    private fun updateRotations(entity: Entity): Boolean {
        if (rotations.get().equals("none", true)) return true



        var defRotation = getTargetRotation(entity) ?: return false

        if (defRotation != RotationUtils.serverRotation && roundTurnAngle.get())
            defRotation.yaw = RotationUtils.roundRotation(defRotation.yaw, roundAngleDirs.get())

        if (silentRotationValue.get()) {
            RotationUtils.setTargetRotation(defRotation, if (aacValue.get() && !rotations.get().equals("Spin", ignoreCase = true)) 15 else 0)
        } else {
            defRotation.toPlayer(mc.thePlayer!!)
        }

        return true
    }
    */

    private fun getTargetRotation(entity: Entity): Rotation? {
        var boundingBox = entity.entityBoundingBox
        if (rotations.get().equals("Vanilla", ignoreCase = true)){
            if (maxTurnSpeed.get() <= 0F)
                return RotationUtils.serverRotation

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange,
                RandomUtils.nextFloat(minRand.get(), maxRand.get()),
                randomCenterNewValue.get()
            ) ?: return null

            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            return limitedRotation
        }
        if (rotations.get().equals("Spin", ignoreCase = true)){
            if (maxTurnSpeed.get() <= 0F)
                return RotationUtils.serverRotation

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                false,
                false,
                false,
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return null

            return rotation
        }
        if (rotations.get().equals("Full", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                RotationUtils.OtherRotation(
                    boundingBox, RotationUtils.getCenter(entity.entityBoundingBox), predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(), maxRange
                ), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            return limitedRotation
        }
        if (rotations.get().equals("BackTrack", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                RotationUtils.OtherRotation(boundingBox,RotationUtils.getCenter(entity.entityBoundingBox), predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),maxRange), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            return limitedRotation
        }
        if (rotations.get().equals("Shake", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val targetH = boundingBox.maxY - boundingBox.minY

            val targetX = boundingBox.maxX - (boundingBox.maxX - boundingBox.minX) / 2
            val targetY = (boundingBox.maxY - targetH / 2) - (((targetH - 0.2) / 2) * yJitter)
            val targetZ = boundingBox.maxZ - (boundingBox.maxZ - boundingBox.minZ) / 2

            val rotToTarget = RotationUtils.getRotations(targetX, targetY, targetZ)

            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotToTarget, (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (debugValue.get()) ClientUtils.displayChatMessage("[Rot] ${limitedRotation.yaw} ${limitedRotation.pitch}")

            return limitedRotation
        }
        return RotationUtils.serverRotation
    }

    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        if (rotations.get().equals("none", true)) {
            hitable = true
            return
        }


        // Modify hit check for some situations
        if (rotations.get().equals("spin", true)) {
            hitable = target!!.hurtTime <= spinHurtTimeValue.get()
            return
        }

        // Completely disable rotation check if turn speed equals to 0 or NoHitCheck is enabled
        if(maxTurnSpeed.get() <= 0F || noHitCheck.get()) {
            hitable = true
            return
        }

        val reach = min(maxRange.toDouble(), mc.thePlayer.getDistanceToEntityBox(target!!)) + 1

        if (raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach) {
                (!livingRaycastValue.get() || it is EntityLivingBase && it !is EntityArmorStand) &&
                        (isEnemy(it) || raycastIgnoredValue.get() || aacValue.get() && mc.theWorld.getEntitiesWithinAABBExcludingEntity(it, it.entityBoundingBox).isNotEmpty())
            }

            if (raycastValue.get() && raycastedEntity is EntityLivingBase
                && (LiquidBounce.moduleManager[NoFriends::class.java]!!.state || !EntityUtils.isFriend(raycastedEntity)))
                currentTarget = raycastedEntity

            hitable = if(maxTurnSpeed.get() > 0F) currentTarget == raycastedEntity else true
        } else
            hitable = RotationUtils.isFaced(currentTarget, reach)
    }

    /**
     * Start blocking
     */


    private fun startBlocking(interactEntity: Entity, interact: Boolean) {
        if (!canSmartBlock || autoBlockModeValue.get().equals("none", true) || !(blockRate.get() > 0 && Random().nextInt(100) <= blockRate.get()))
            return

        if (!abThruWallValue.get() && interactEntity is EntityLivingBase) {
            val entityLB = interactEntity as EntityLivingBase
            if (!entityLB.canEntityBeSeen(mc.thePlayer!!)) {
                fakeBlock = true
                return
            }
        }

        if (autoBlockModeValue.get().equals("ncp", true)) {
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f))
            blockingStatus = true
            return
        }

        if (autoBlockModeValue.get().equals("oldhypixel", true)) {
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f))
            blockingStatus = true
            return
        }

        if (interact) {
            //mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, interactEntity.positionVector))
            val positionEye = mc.renderViewEntity?.getPositionEyes(1F)

            val expandSize = interactEntity.collisionBorderSize.toDouble()
            val boundingBox = interactEntity.entityBoundingBox.expand(expandSize, expandSize, expandSize)

            val (yaw, pitch) = RotationUtils.targetRotation ?: Rotation(mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch)
            val yawCos = cos(-yaw * 0.017453292F - Math.PI.toFloat())
            val yawSin = sin(-yaw * 0.017453292F - Math.PI.toFloat())
            val pitchCos = -cos(-pitch * 0.017453292F)
            val pitchSin = sin(-pitch * 0.017453292F)
            val range = min(maxRange.toDouble(), mc.thePlayer!!.getDistanceToEntityBox(interactEntity)) + 1
            val lookAt = positionEye!!.addVector(yawSin * pitchCos * range, pitchSin * range, yawCos * pitchCos * range)

            val movingObject = boundingBox.calculateIntercept(positionEye, lookAt) ?: return
            val hitVec = movingObject.hitVec

            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, Vec3(
                hitVec.xCoord - interactEntity.posX,
                hitVec.yCoord - interactEntity.posY,
                hitVec.zCoord - interactEntity.posZ)
            ))
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, C02PacketUseEntity.Action.INTERACT))
        }

        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
        blockingStatus = true
    }

    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        fakeBlock = false

        if (blockingStatus) {
            if (autoBlockModeValue.get().equals("oldhypixel", true))
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(1.0, 1.0, 1.0), EnumFacing.DOWN))
            else
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))

            blockingStatus = false
        }
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        get() = mc.thePlayer.isSpectator || !isAlive(mc.thePlayer)
                || (blinkCheck.get() && LiquidBounce.moduleManager[Blink::class.java]!!.state) || LiquidBounce.moduleManager[FreeCam::class.java]!!.state ||
                (noScaffValue.get() && LiquidBounce.moduleManager[Scaffold::class.java]!!.state)

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0 ||
            aacValue.get() && entity.hurtTime > 5


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
        get() = targetModeValue.get()
}