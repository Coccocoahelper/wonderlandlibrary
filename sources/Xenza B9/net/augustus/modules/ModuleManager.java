// 
// Decompiled by Procyon v0.6.0
// 

package net.augustus.modules;

import java.util.Iterator;
import java.util.List;
import net.augustus.modules.misc.StaffDetector;
import net.augustus.modules.misc.AutoWalk;
import net.augustus.modules.misc.Fixes;
import net.augustus.modules.misc.SpinBot;
import net.augustus.modules.misc.AutoRegister;
import net.augustus.modules.misc.AutoPlay;
import net.augustus.modules.misc.Disabler;
import net.augustus.modules.misc.MidClick;
import net.augustus.modules.misc.Spammer;
import net.augustus.modules.world.BlockFly;
import net.augustus.modules.world.FastPlace;
import net.augustus.modules.world.Fucker;
import net.augustus.modules.world.FastBreak;
import net.augustus.modules.player.Regen;
import net.augustus.modules.player.Phase;
import net.augustus.modules.player.Teleport;
import net.augustus.modules.player.FakeLag;
import net.augustus.modules.player.AutoTool;
import net.augustus.modules.render.Notifications;
import net.augustus.modules.player.Inventory;
import net.augustus.modules.player.ChestStealer;
import net.augustus.modules.player.InventoryCleaner;
import net.augustus.modules.player.AutoArmor;
import net.augustus.modules.player.NoFall;
import net.augustus.modules.movement.SafeWalk;
import net.augustus.modules.movement.VClip;
import net.augustus.modules.movement.LongJump;
import net.augustus.modules.movement.Step;
import net.augustus.modules.movement.NoWeb;
import net.augustus.modules.movement.Spider;
import net.augustus.modules.movement.FastLadder;
import net.augustus.modules.movement.TargetStrafe;
import net.augustus.modules.movement.BugUp;
import net.augustus.modules.movement.Fly;
import net.augustus.modules.misc.TestModule;
import net.augustus.modules.movement.Blink;
import net.augustus.modules.movement.Strafe;
import net.augustus.modules.movement.Timer;
import net.augustus.modules.movement.Speed;
import net.augustus.modules.movement.NoSlow;
import net.augustus.modules.movement.Jesus;
import net.augustus.modules.movement.Sprint;
import net.augustus.modules.render.CustomItemPos;
import net.augustus.modules.render.CustomGlint;
import net.augustus.modules.render.Projectiles;
import net.augustus.modules.render.Trajectories;
import net.augustus.modules.render.CrossHair;
import net.augustus.modules.render.Ambiance;
import net.augustus.modules.render.Line;
import net.augustus.modules.render.Barriers;
import net.augustus.modules.render.BlockESP;
import net.augustus.modules.render.Scoreboard;
import net.augustus.modules.render.ItemEsp;
import net.augustus.modules.render.Tracers;
import net.augustus.modules.render.HUD;
import net.augustus.modules.render.NameTags;
import net.augustus.modules.render.Protector;
import net.augustus.modules.render.FullBright;
import net.augustus.modules.render.BlockAnimation;
import net.augustus.modules.render.StorageESP;
import net.augustus.modules.render.ESP;
import net.augustus.modules.render.AttackEffects;
import net.augustus.modules.combat.FastBow;
import net.augustus.modules.render.ClickGUI;
import net.augustus.modules.render.ArrayList;
import net.augustus.modules.combat.MoreKB;
import net.augustus.modules.combat.Criticals;
import net.augustus.modules.combat.TimerRange;
import net.augustus.modules.combat.AntiFireBall;
import net.augustus.modules.combat.AutoClicker;
import net.augustus.modules.combat.AutoSoup;
import net.augustus.modules.combat.BackTrack;
import net.augustus.modules.combat.Teams;
import net.augustus.modules.combat.AntiBot;
import net.augustus.modules.combat.KillAura;
import net.augustus.modules.combat.Velocity;
import net.augustus.utils.interfaces.MA;

public class ModuleManager implements MA
{
    public final Velocity velocity;
    public final KillAura killAura;
    public final AntiBot antiBot;
    public final Teams teams;
    public final BackTrack backTrack;
    public final AutoSoup autoSoup;
    public final AutoClicker autoClicker;
    public final AntiFireBall antiFireBall;
    public final TimerRange timerRange;
    public final Criticals criticals;
    public final MoreKB moreKB;
    public final ArrayList arrayList;
    public final ClickGUI clickGUI;
    public final FastBow fastBow;
    public final AttackEffects attackEffects;
    public final ESP esp;
    public final StorageESP storageESP;
    public final BlockAnimation blockAnimation;
    public final FullBright fullBright;
    public final Protector protector;
    public final NameTags nameTags;
    public final HUD hud;
    public final Tracers tracers;
    public final ItemEsp itemEsp;
    public final Scoreboard scoreboard;
    public final BlockESP blockESP;
    public final Barriers barriers;
    public final Line line;
    public final Ambiance ambiance;
    public final CrossHair crossHair;
    public final Trajectories trajectories;
    public final Projectiles projectiles;
    public final CustomGlint customGlint;
    public final CustomItemPos customItemPos;
    public final Sprint sprint;
    public final Jesus jesus;
    public final NoSlow noSlow;
    public final Speed speed;
    public final Timer timer;
    public final Strafe strafe;
    public final Blink blink;
    public final TestModule testModule;
    public final Fly fly;
    public final BugUp bugUp;
    public final TargetStrafe targetStrafe;
    public final FastLadder fastLadder;
    public final Spider spider;
    public final NoWeb noWeb;
    public final Step step;
    public final LongJump longJump;
    public final VClip vclip;
    public final SafeWalk safeWalk;
    public final NoFall noFall;
    public final AutoArmor autoArmor;
    public final InventoryCleaner inventoryCleaner;
    public final ChestStealer chestStealer;
    public static boolean cracked;
    public final Inventory inventory;
    public final Notifications notifications;
    public final AutoTool autoTool;
    public final FakeLag fakeLag;
    public final Teleport teleport;
    public final Phase phase;
    public final Regen regen;
    public final FastBreak fastBreak;
    public final Fucker fucker;
    public final FastPlace fastPlace;
    public final BlockFly blockFly;
    public final Spammer spammer;
    public final MidClick midClick;
    public final Disabler disabler;
    public final AutoPlay autoPlay;
    public final AutoRegister autoRegister;
    public final SpinBot spinBot;
    public final Fixes fixes;
    public final AutoWalk autoWalk;
    public final StaffDetector staffDetector;
    
    public ModuleManager() {
        this.velocity = new Velocity();
        this.killAura = new KillAura();
        this.antiBot = new AntiBot();
        this.teams = new Teams();
        this.backTrack = new BackTrack();
        this.autoSoup = new AutoSoup();
        this.autoClicker = new AutoClicker();
        this.antiFireBall = new AntiFireBall();
        this.timerRange = new TimerRange();
        this.criticals = new Criticals();
        this.moreKB = new MoreKB();
        this.arrayList = new ArrayList();
        this.clickGUI = new ClickGUI();
        this.fastBow = new FastBow();
        this.attackEffects = new AttackEffects();
        this.esp = new ESP();
        this.storageESP = new StorageESP();
        this.blockAnimation = new BlockAnimation();
        this.fullBright = new FullBright();
        this.protector = new Protector();
        this.nameTags = new NameTags();
        this.hud = new HUD();
        this.tracers = new Tracers();
        this.itemEsp = new ItemEsp();
        this.scoreboard = new Scoreboard();
        this.blockESP = new BlockESP();
        this.barriers = new Barriers();
        this.line = new Line();
        this.ambiance = new Ambiance();
        this.crossHair = new CrossHair();
        this.trajectories = new Trajectories();
        this.projectiles = new Projectiles();
        this.customGlint = new CustomGlint();
        this.customItemPos = new CustomItemPos();
        this.sprint = new Sprint();
        this.jesus = new Jesus();
        this.noSlow = new NoSlow();
        this.speed = new Speed();
        this.timer = new Timer();
        this.strafe = new Strafe();
        this.blink = new Blink();
        this.testModule = new TestModule();
        this.fly = new Fly();
        this.bugUp = new BugUp();
        this.targetStrafe = new TargetStrafe();
        this.fastLadder = new FastLadder();
        this.spider = new Spider();
        this.noWeb = new NoWeb();
        this.step = new Step();
        this.longJump = new LongJump();
        this.vclip = new VClip();
        this.safeWalk = new SafeWalk();
        this.noFall = new NoFall();
        this.autoArmor = new AutoArmor();
        this.inventoryCleaner = new InventoryCleaner();
        this.chestStealer = new ChestStealer();
        this.inventory = new Inventory();
        this.notifications = new Notifications();
        this.autoTool = new AutoTool();
        this.fakeLag = new FakeLag();
        this.teleport = new Teleport();
        this.phase = new Phase();
        this.regen = new Regen();
        this.fastBreak = new FastBreak();
        this.fucker = new Fucker();
        this.fastPlace = new FastPlace();
        this.blockFly = new BlockFly();
        this.spammer = new Spammer();
        this.midClick = new MidClick();
        this.disabler = new Disabler();
        this.autoPlay = new AutoPlay();
        this.autoRegister = new AutoRegister();
        this.spinBot = new SpinBot();
        this.fixes = new Fixes();
        this.autoWalk = new AutoWalk();
        this.staffDetector = new StaffDetector();
    }
    
    public List<Module> getModules() {
        return ModuleManager.ma.getModules();
    }
    
    public List<Module> getModules(final Categorys cat) {
        final List<Module> modules = new java.util.ArrayList<Module>();
        for (final Module mod : this.getModules()) {
            if (mod.getCategory().equals(cat)) {
                modules.add(mod);
            }
        }
        return modules;
    }
    
    public void setModules(final List<Module> modules) {
        ModuleManager.ma.setModules(modules);
    }
    
    public java.util.ArrayList<Module> getActiveModules() {
        return ModuleManager.ma.getActiveModules();
    }
    
    public void setActiveModules(final java.util.ArrayList<Module> activeModules) {
        ModuleManager.ma.setActiveModules(activeModules);
    }
    
    static {
        ModuleManager.cracked = true;
    }
}
