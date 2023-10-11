@file:Suppress("UNREACHABLE_CODE")

package net.ccbluex.liquidbounce

import com.google.gson.JsonParser
import net.ccbluex.liquidbounce.discordrpc.CrossSineRPC
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.macro.MacroManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.*
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.file.config.ConfigManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.ui.client.gui.EnumLaunchFilter
import net.ccbluex.liquidbounce.ui.client.gui.GuiLaunchOptionSelectMenu
import net.ccbluex.liquidbounce.ui.client.gui.LaunchFilterInfo
import net.ccbluex.liquidbounce.ui.client.gui.LaunchOption
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.keybind.KeyBindManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.ui.i18n.LanguageManager
import net.ccbluex.liquidbounce.ui.sound.TipSoundManager
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.opengl.Display
import java.util.*
import kotlin.concurrent.thread

object CrossSine {
    // Client information

    const val CLIENT_NAME = "CrossSine"
    val CLIENT_STATUS = false
    var Darkmode = true
    var destruct = false
    const val COLORED_NAME = "Cross§CSine§F"
    const val CLIENT_CREATOR = "CCBlueX, Zywl & SkidderMC TEAM & Shape"
    const val CLIENT_WEBSITE = "crosssine.github.io"
    const val CLIENT_VERSION = "B34"
    @JvmField
    val CLIENT_LOADING = if (CLIENT_STATUS) "Beta Loading" else "Installing CrossSine"
    @JvmField
    val CLIENT_TITLE = "${CLIENT_NAME} ${CLIENT_VERSION} Release" + if (CLIENT_STATUS) " Beta" else " Download : ${CLIENT_WEBSITE}"

    @JvmField
    val gitInfo = Properties().also {
        val inputStream = CrossSine::class.java.classLoader.getResourceAsStream("git.properties")
        if (inputStream != null) {
            it.load(inputStream)
        } else {
            it["git.branch"] = "Main"
        }
    }


    @JvmField
    val CLIENT_BRANCH = (gitInfo["git.branch"] ?: "unknown").let {
        if (it == "main") "Main" else it
    }

    var isStarting = true
    var isLoadingConfig = true
    private var latest = ""

    // Managers
    lateinit var moduleManager: ModuleManager

    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var combatManager: CombatManager
    lateinit var macroManager: MacroManager
    lateinit var configManager: ConfigManager



    // Some UI things
    lateinit var hud: HUD
    lateinit var mainMenu: GuiScreen
    lateinit var keyBindManager: KeyBindManager



    // Discord RPC
    lateinit var clientRichPresence: CrossSineRPC

    // Menu Background
    var background: ResourceLocation? = null

    val launchFilters = mutableListOf<EnumLaunchFilter>()
    val dynamicLaunchOptions: Array<LaunchOption>
        get() = ClassUtils.resolvePackage(
            "${LaunchOption::class.java.`package`.name}.options",
            LaunchOption::class.java
        )
            .filter {
                val annotation = it.getDeclaredAnnotation(LaunchFilterInfo::class.java)
                if (annotation != null) {
                    return@filter annotation.filters.toMutableList() == launchFilters
                }
                false
            }
            .map {
                try {
                    it.newInstance()
                } catch (e: IllegalAccessException) {
                    ClassUtils.getObjectInstance(it) as LaunchOption
                }
            }.toTypedArray()

    /**
     * Execute if client will be started
     */
    fun initClient() {
        ClientUtils.logInfo("Loading $CLIENT_NAME $CLIENT_VERSION, by $CLIENT_CREATOR")
        ClientUtils.logInfo("Initialzing...")
        val startTime = System.currentTimeMillis()
        // Create file manager
        fileManager = FileManager()
        configManager = ConfigManager()

        // Create event manager
        eventManager = EventManager()

        // Load language
        LanguageManager.switchLanguage(Minecraft.getMinecraft().gameSettings.language)

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(ClientFixes)
        eventManager.registerListener(ClientSpoof())
        eventManager.registerListener(InventoryUtils)
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(ServerSpoof)
        eventManager.registerListener(SessionUtils())
        eventManager.registerListener(StatisticsUtils())
        eventManager.registerListener(LocationCache())


        // Init Discord RPC
        clientRichPresence = CrossSineRPC

        // Create command manager
        commandManager = CommandManager()

        fileManager.loadConfigs(
            fileManager.accountsConfig,
            fileManager.friendsConfig,
            fileManager.specialConfig,
            fileManager.subscriptsConfig
        )
        // Load client fonts
        Fonts.loadFonts()

        macroManager = MacroManager()
        eventManager.registerListener(macroManager)

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        try {
            // ScriptManager, Remapper will be lazy loaded when scripts are enabled
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.logError("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        tipSoundManager = TipSoundManager()

        // KeyBindManager
        keyBindManager = KeyBindManager()

        combatManager = CombatManager()
        eventManager.registerListener(combatManager)

        mainMenu = GuiLaunchOptionSelectMenu()

        Display.setTitle(CLIENT_TITLE)

        // Set HUD
        hud = HUD.createDefault()

        fileManager.loadConfigs(fileManager.hudConfig, fileManager.xrayConfig)

        // run update checker
        if (CLIENT_VERSION != "unknown") {
            thread(block = this::checkUpdate)
        }
        ClientUtils.logInfo("Loading Script Subscripts...")
        for (subscript in fileManager.subscriptsConfig.subscripts) {
            scriptManager.disableScripts()
            scriptManager.unloadScripts()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        }
        CLIENT_TITLE
        ClientUtils.logInfo("$CLIENT_NAME $CLIENT_VERSION loaded in ${(System.currentTimeMillis() - startTime)}ms!")
    }

    private fun checkUpdate() {
        try {
            val get = HttpUtils.get("https://api.github.com/repos/SkidderMC/FDPClient/commits/${gitInfo["git.branch"]}")

            val jsonObj = JsonParser()
                .parse(get).asJsonObject

            latest = jsonObj.get("sha").asString.substring(0, 7)

            if (latest != gitInfo["git.commit.id.abbrev"]) {
                ClientUtils.logInfo("New version available: $latest")
            } else {
                ClientUtils.logInfo("No new version available")
            }
        } catch (t: Throwable) {
            ClientUtils.logError("Failed to check for updates.", t)
        }
    }

    /**
     * Execute if client ui type is selected
     */
    fun startClient() {
        dynamicLaunchOptions.forEach {
            it.start()
        }


        // Load configs
        configManager.loadLegacySupport()
        configManager.loadConfigSet()

        // Set is starting status
        isStarting = false
        isLoadingConfig = false

        ClientUtils.logInfo("$CLIENT_NAME $CLIENT_VERSION started!")

        if (clientRichPresence.showRichPresenceValue) {
            thread {
                try {
                    clientRichPresence.run()
                } catch (throwable: Throwable) {
                    ClientUtils.logError("Failed to setup Discord RPC.", throwable)
                }
            }
        }

    }


    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        if (!isStarting && !isLoadingConfig) {
            ClientUtils.logInfo("Shutting down $CLIENT_NAME $CLIENT_VERSION!")

            // Call client shutdown
            eventManager.callEvent(ClientShutdownEvent())

            // Save all available configs
            configManager.save(true, true)
            fileManager.saveAllConfigs()

            dynamicLaunchOptions.forEach {
                it.stop()
            }
        }
        clientRichPresence.stop()
    }
    fun onDestruct() {
        if (mc.currentScreen != null && mc.thePlayer != null) {
            mc.thePlayer.closeScreen()
        }
        destruct = true
        MinecraftForge.EVENT_BUS.unregister(this)
        for (m in moduleManager.modules) {
            m.toggled = false
            MinecraftForge.EVENT_BUS.unregister(m)
        }
    }
}
