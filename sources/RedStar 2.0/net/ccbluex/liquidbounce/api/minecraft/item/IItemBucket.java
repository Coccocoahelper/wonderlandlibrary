package net.ccbluex.liquidbounce.api.minecraft.item;

import kotlin.Metadata;
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock;
import net.ccbluex.liquidbounce.api.minecraft.item.IItem;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 1, 16}, bv={1, 0, 3}, k=1, d1={"\u0000\n\n\n\u0000\n\n\b\bf\u000020R0X¦¢\b¨"}, d2={"Lnet/ccbluex/liquidbounce/api/minecraft/item/IItemBucket;", "Lnet/ccbluex/liquidbounce/api/minecraft/item/IItem;", "isFull", "Lnet/ccbluex/liquidbounce/api/minecraft/client/block/IBlock;", "()Lnet/ccbluex/liquidbounce/api/minecraft/client/block/IBlock;", "Pride"})
public interface IItemBucket
extends IItem {
    @NotNull
    public IBlock isFull();
}
