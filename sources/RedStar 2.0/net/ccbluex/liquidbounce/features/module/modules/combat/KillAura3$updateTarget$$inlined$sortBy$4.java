// ERROR: Unable to apply inner class name fixup
package net.ccbluex.liquidbounce.features.module.modules.combat;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase;

@Metadata(mv={1, 1, 16}, bv={1, 0, 3}, k=3, d1={"\u0000\n\u0000\n\b\n\b\n\b\n\b\n\b\n\b\n\b\u00000\"\b\u00002\n *HH2\n *HHH\n¢\b¨\b"}, d2={"<anonymous>", "", "T", "a", "kotlin.jvm.PlatformType", "b", "compare", "(Ljava/lang/Object;Ljava/lang/Object;)I", "kotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2"})
public static final class KillAura3$updateTarget$.inlined.sortBy.4<T>
implements Comparator<T> {
    @Override
    public final int compare(T a, T b) {
        boolean bl = false;
        IEntityLivingBase it = (IEntityLivingBase)a;
        boolean bl2 = false;
        Comparable comparable = Integer.valueOf(-it.getTicksExisted());
        it = (IEntityLivingBase)b;
        Comparable comparable2 = comparable;
        bl2 = false;
        Integer n = -it.getTicksExisted();
        return ComparisonsKt.compareValues(comparable2, (Comparable)n);
    }
}
