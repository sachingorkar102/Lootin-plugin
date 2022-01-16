package com.github.sachin.lootin.version.lookup.handle.field;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public abstract class UnsafeFieldHandle<O> implements IFieldHandle<O> {

    protected static final Unsafe UNSAFE = getUnsafe();

    protected final IFieldHandle<O> setMemoryValue(final Object base, final long offset, final Object value) {
        UNSAFE.putObject(base, offset, value);
        return this;
    }

    protected final Object getMemoryValue(final Object base, final long offset) {
        return UNSAFE.getObject(base, offset);
    }

    @Override
    public final boolean isUnsafe() {
        return true;
    }

    private static Unsafe getUnsafe() {
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (final Exception ignore) {
            return null;
        }
    }

}