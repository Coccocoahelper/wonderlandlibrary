// 
// Decompiled by Procyon v0.5.36
// 

package ru.fluger.client.feature.impl.combat;

import ru.fluger.client.event.EventTarget;
import ru.fluger.client.helpers.math.MathematicHelper;
import ru.fluger.client.event.events.impl.player.EventUpdate;
import ru.fluger.client.settings.Setting;
import ru.fluger.client.feature.impl.Type;
import ru.fluger.client.settings.impl.NumberSetting;
import ru.fluger.client.feature.Feature;

public class Reach extends Feature
{
    public static NumberSetting reachValue;
    
    public Reach() {
        super("Reach", "\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044e \u0443\u0434\u0430\u0440\u0430", Type.Combat);
        Reach.reachValue = new NumberSetting("Expand", 3.2f, 3.0f, 5.0f, 0.01f, () -> true);
        this.addSettings(Reach.reachValue);
    }
    
    @EventTarget
    public void onUpdate(final EventUpdate event) {
        this.setSuffix("" + MathematicHelper.round(Reach.reachValue.getCurrentValue(), 1));
    }
}
