package net.ccbluex.liquidbounce.ui.client;

import net.ccbluex.liquidbounce.CrossSine;
import net.ccbluex.liquidbounce.font.FontLoaders;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType;
import net.ccbluex.liquidbounce.utils.render.ColorManager;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.*;

import org.lwjgl.input.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;

public class GuiImportConfig extends GuiScreen
{
    public static GuiTextField username;
    public GuiButton loginButton;
    public GuiButton freeButton;
    public static boolean logined;
    public static boolean Passed;
    public static String message;
    public double[] aaa={0,0,0,0,0,0};
    public GuiImportConfig() {
        super();
        message = "Waitting...";
    }

    @Override
    protected void actionPerformed(final GuiButton button)  {
        switch (button.id) {
            case 1:
                if (!username.getText().isEmpty()) {
                    try {
                        String url = username.getText();
                        String name = username.getText().startsWith("https://") ? username.getText().substring(8,username.getText().replaceAll("https://","").indexOf("/")+8) : username.getText().startsWith("http://") ? username.getText().substring(7,username.getText().replaceAll("http://","").indexOf("/")+7) : "No name config";
                        try {
                            long startTime = System.currentTimeMillis();
                            CrossSine.hud.addNotification(new Notification("Script Manager", "Reloading Scripts...", NotifyType.INFO, 1500, 500));
                            CrossSine.scriptManager.disableScripts();
                            CrossSine.scriptManager.unloadScripts();
                            CrossSine.scriptManager.loadScripts();
                            CrossSine.scriptManager.enableScripts();
                            CrossSine.hud.addNotification(new Notification("Script Manager", "Added Subscribe: " + name + " | " + url + " (" + (System.currentTimeMillis() - startTime) + "ms)", NotifyType.SUCCESS, 1500, 500));
                            message = "Done!";
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    message = "username is empty.";
                    mc.displayGuiScreen(this);
                }

                break;
            case 3:
                try {
                    copyText("");
                }catch (Exception E){

                }
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        aaa[0]=Math.pow(aaa[0]+1,0.96)-1;
        FontLoaders.F18.drawString(CrossSine.CLIENT_NAME,10,10,new Color(255,255,255).getRGB());
        final int h;
        final int Height = h = new ScaledResolution(this.mc).getScaledHeight();
        final int w;
        final int Width = w = new ScaledResolution(this.mc).getScaledWidth();
        final FontRenderer var4 = this.mc.fontRendererObj;
        final ScaledResolution s1 = new ScaledResolution(this.mc);
        final ScaledResolution res = new ScaledResolution(this.mc);
        drawBackground(0);
        drawRect(0,0, (int) ((1-aaa[0])*(w/3 + 132)),h,new Color(31, 31, 31, 255).getRGB());
        RenderUtils.drawRect((float) ((1-aaa[0])*(w/3 + 130)), (float) 0, (float) ((1-aaa[0])*(w/3 + 132)),h, ColorManager.astolfoRainbow(1,50,1));
        username.drawTextBox();
        FontLoaders.F18.drawString(message, (float) ((1-aaa[0])*(15)), 15, new Color(200,200,200,150).getRGB());
        FontLoaders.F14.drawString("Tips: CrossSine", (float) ((1-aaa[0])*(18))+4, this.height / 2 + 50, new Color(189, 189, 189).getRGB());
        FontLoaders.F14.drawString("You need to upload full body unlock link used by each developer", (float) ((1-aaa[0])*(18))+4, this.height / 2 + 58, new Color(189, 189, 189).getRGB());
        FontLoaders.F18.drawString(" URL:", (float) ((1-aaa[0])*(18)), this.height / 2 + 8, new Color(189, 189, 189).getRGB());
        FontLoaders.F40.drawString("LIFrame", (float) ((1-aaa[0])*(18)), this.height / 2 - 20, new Color(255, 255, 255).getRGB());
        super.drawScreen(mouseX, mouseY, delta);
    }
    public static void copyText(String v) {
        StringSelection stringSelection = new StringSelection(v);
        Clipboard clapboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clapboard.setContents(stringSelection, null);
    }
    @Override
    public void initGui() {
        aaa[0]=5;
        logined = false;
        final FontRenderer var1 = this.mc.fontRendererObj;
        final int var2 = this.height / 2;
        super.initGui();
        this.buttonList.add(this.loginButton);  //Designed By XiGua
        this.buttonList.add(this.freeButton);
        username = new GuiTextField(var2, var1, 20, this.height / 2 + 20, this.width/3 + 60, 20);
        username.setMaxStringLength(114514);
        username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(final char var1, final int var2) {
        if (var1 == '\t') {
            if (!username.isFocused()) {
                username.setFocused(true);
            }
            else {
                username.setFocused(username.isFocused());
            }
        }
        if (var1 == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        if (var1 == '\u001b') {}
        username.textboxKeyTyped(var1, var2);
    }

    @Override
    protected void mouseClicked(final int var1, final int var2, final int var3) {
        try {
            super.mouseClicked(var1, var2, var3);
        }
        catch (IOException var4) {
            var4.printStackTrace();
        }
        username.mouseClicked(var1, var2, var3);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
    }

    static {
        logined = false;
        Passed = false;
    }
}
