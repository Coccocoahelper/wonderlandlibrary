/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.types;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class VarLongType
extends Type<Long> {
    public static final VarLongType VAR_LONG = new VarLongType();

    public VarLongType() {
        super("VarLong", Long.class);
    }

    @Override
    public Long read(ByteBuf byteBuf) throws Exception {
        byte b0;
        long i2 = 0L;
        int j2 = 0;
        do {
            b0 = byteBuf.readByte();
            i2 |= (long)((b0 & 0x7F) << j2++ * 7);
            if (j2 <= 10) continue;
            throw new RuntimeException("VarLong too big");
        } while ((b0 & 0x80) == 128);
        return i2;
    }

    @Override
    public void write(ByteBuf byteBuf, Long i2) throws Exception {
        while ((i2 & 0xFFFFFFFFFFFFFF80L) != 0L) {
            byteBuf.writeByte((int)(i2 & 0x7FL) | 0x80);
            i2 = i2 >>> 7;
        }
        byteBuf.writeByte(i2.intValue());
    }
}

