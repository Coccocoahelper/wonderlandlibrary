/*
 * Decompiled with CFR 0.152.
 */
package jdk.nashorn.internal.runtime.arrays;

import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.runtime.ECMAErrors;
import jdk.nashorn.internal.runtime.ScriptRuntime;
import jdk.nashorn.internal.runtime.arrays.ArrayData;
import jdk.nashorn.internal.runtime.arrays.ArrayFilter;

final class NonExtensibleArrayFilter
extends ArrayFilter {
    NonExtensibleArrayFilter(ArrayData underlying) {
        super(underlying);
    }

    @Override
    public ArrayData copy() {
        return new NonExtensibleArrayFilter(this.underlying.copy());
    }

    @Override
    public ArrayData slice(long from, long to) {
        return new NonExtensibleArrayFilter(this.underlying.slice(from, to));
    }

    private ArrayData extensionCheck(boolean strict2, int index) {
        if (!strict2) {
            return this;
        }
        throw ECMAErrors.typeError(Global.instance(), "object.non.extensible", String.valueOf(index), ScriptRuntime.safeToString(this));
    }

    @Override
    public ArrayData set(int index, Object value, boolean strict2) {
        if (this.has(index)) {
            return this.underlying.set(index, value, strict2);
        }
        return this.extensionCheck(strict2, index);
    }

    @Override
    public ArrayData set(int index, int value, boolean strict2) {
        if (this.has(index)) {
            return this.underlying.set(index, value, strict2);
        }
        return this.extensionCheck(strict2, index);
    }

    @Override
    public ArrayData set(int index, double value, boolean strict2) {
        if (this.has(index)) {
            return this.underlying.set(index, value, strict2);
        }
        return this.extensionCheck(strict2, index);
    }
}

