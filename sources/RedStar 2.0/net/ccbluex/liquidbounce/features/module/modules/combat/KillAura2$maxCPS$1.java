package net.ccbluex.liquidbounce.features.module.modules.combat;

import kotlin.Metadata;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura2;
import net.ccbluex.liquidbounce.utils.timer.TimeUtils;
import net.ccbluex.liquidbounce.value.IntegerValue;

@Metadata(mv={1, 1, 16}, bv={1, 0, 3}, k=1, d1={"\u0000\n\u0000\n\n\u0000\n\n\u0000\n\b\n\b*\u0000\b\n\u000020J02020H¨"}, d2={"net/ccbluex/liquidbounce/features/module/modules/combat/KillAura2$maxCPS$1", "Lnet/ccbluex/liquidbounce/value/IntegerValue;", "onChanged", "", "oldValue", "", "newValue", "Pride"})
public static final class KillAura2$maxCPS$1
extends IntegerValue {
    final KillAura2 this$0;

    @Override
    protected void onChanged(int oldValue, int newValue) {
        int i = ((Number)this.this$0.minCPS.get()).intValue();
        if (i > newValue) {
            this.set(i);
        }
        this.this$0.attackDelay = TimeUtils.randomClickDelay(((Number)this.this$0.minCPS.get()).intValue(), ((Number)this.get()).intValue());
    }

    KillAura2$maxCPS$1(KillAura2 $outer, String $super_call_param$1, int $super_call_param$2, int $super_call_param$3, int $super_call_param$4) {
        this.this$0 = $outer;
        super($super_call_param$1, $super_call_param$2, $super_call_param$3, $super_call_param$4);
    }
}
