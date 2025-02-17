/*
 * Decompiled with CFR 0.152.
 */
package net.ccbluex.liquidbounce.ui.client;

import java.io.IOException;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiButton;
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiScreen;
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen;
import net.ccbluex.liquidbounce.features.special.AntiForge;
import net.ccbluex.liquidbounce.ui.font.Fonts;

public class GuiAntiForge
extends WrappedGuiScreen {
    private IGuiButton payloadButton;
    private IGuiButton fmlButton;
    private IGuiButton enabledButton;
    private final IGuiScreen prevGui;
    private IGuiButton proxyButton;

    @Override
    public void actionPerformed(IGuiButton iGuiButton) {
        switch (iGuiButton.getId()) {
            case 1: {
                AntiForge.enabled = !AntiForge.enabled;
                this.enabledButton.setDisplayString("Enabled (" + (AntiForge.enabled ? "On" : "Off") + ")");
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig);
                break;
            }
            case 2: {
                AntiForge.blockFML = !AntiForge.blockFML;
                this.enabledButton.setDisplayString("Block FML (" + (AntiForge.blockFML ? "On" : "Off") + ")");
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig);
                break;
            }
            case 3: {
                AntiForge.blockProxyPacket = !AntiForge.blockProxyPacket;
                this.enabledButton.setDisplayString("Block FML Proxy Packet (" + (AntiForge.blockProxyPacket ? "On" : "Off") + ")");
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig);
                break;
            }
            case 4: {
                AntiForge.blockPayloadPackets = !AntiForge.blockPayloadPackets;
                this.enabledButton.setDisplayString("Block Payload Packets (" + (AntiForge.blockPayloadPackets ? "On" : "Off") + ")");
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig);
                break;
            }
            case 0: {
                mc.displayGuiScreen(this.prevGui);
            }
        }
    }

    @Override
    public void initGui() {
        this.enabledButton = classProvider.createGuiButton(1, this.representedScreen.getWidth() / 2 - 100, this.representedScreen.getHeight() / 4 + 35, "Enabled (" + (AntiForge.enabled ? "On" : "Off") + ")");
        this.representedScreen.getButtonList().add(this.enabledButton);
        this.fmlButton = classProvider.createGuiButton(2, this.representedScreen.getWidth() / 2 - 100, this.representedScreen.getHeight() / 4 + 50 + 25, "Block FML (" + (AntiForge.blockFML ? "On" : "Off") + ")");
        this.representedScreen.getButtonList().add(this.fmlButton);
        this.proxyButton = classProvider.createGuiButton(3, this.representedScreen.getWidth() / 2 - 100, this.representedScreen.getHeight() / 4 + 50 + 50, "Block FML Proxy Packet (" + (AntiForge.blockProxyPacket ? "On" : "Off") + ")");
        this.representedScreen.getButtonList().add(this.proxyButton);
        this.payloadButton = classProvider.createGuiButton(4, this.representedScreen.getWidth() / 2 - 100, this.representedScreen.getHeight() / 4 + 50 + 75, "Block Payload Packets (" + (AntiForge.blockPayloadPackets ? "On" : "Off") + ")");
        this.representedScreen.getButtonList().add(this.payloadButton);
        this.representedScreen.getButtonList().add(classProvider.createGuiButton(0, this.representedScreen.getWidth() / 2 - 100, this.representedScreen.getHeight() / 4 + 55 + 100 + 5, "Back"));
    }

    @Override
    public void drawScreen(int n, int n2, float f) {
        this.representedScreen.drawBackground(0);
        Fonts.robotoBold180.drawCenteredString("AntiForge", (int)((float)this.representedScreen.getWidth() / 2.0f), (int)((float)this.representedScreen.getHeight() / 8.0f + 5.0f), 4673984, true);
        super.drawScreen(n, n2, f);
    }

    @Override
    public void keyTyped(char c, int n) throws IOException {
        if (1 == n) {
            mc.displayGuiScreen(this.prevGui);
            return;
        }
        super.keyTyped(c, n);
    }

    public GuiAntiForge(IGuiScreen iGuiScreen) {
        this.prevGui = iGuiScreen;
    }
}

