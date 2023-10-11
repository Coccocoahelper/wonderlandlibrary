package net.ccbluex.liquidbounce.features.module.modules.other

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.BoolValue

@ModuleInfo(name = "NoRotate", spacedName = "No Rotate", category = ModuleCategory.OTHER)
class NoRotate : Module() {
    val noLoadingValue = BoolValue("NoLoading", true)
    val overwriteTeleportValue = BoolValue("SilentConfirm", true)
    val rotateValue = BoolValue("SilentConfirmSetRotation", true).displayable { overwriteTeleportValue.get() }
}