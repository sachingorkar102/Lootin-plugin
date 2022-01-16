package com.github.sachin.lootin.version.lookup.handle.field;

import java.lang.invoke.VarHandle;

public class SafeFieldHandle implements IFieldHandle<VarHandle> {

    private final VarHandle handle;

    public SafeFieldHandle(final VarHandle handle) {
        this.handle = handle;
    }

    @Override
    public Object getValue() {
        return handle.get();
    }

    @Override
    public Object getValue(final Object source) {
        return handle.get(source);
    }

    @Override
    public IFieldHandle<VarHandle> setValue(final Object value) {
        handle.set(value);
        return this;
    }

    @Override
    public IFieldHandle<VarHandle> setValue(final Object source, final Object value) {
        handle.set(source, value);
        return this;
    }

    @Override
    public VarHandle getHandle() {
        return handle;
    }

    @Override
    public boolean isUnsafe() {
        return false;
    }

}
