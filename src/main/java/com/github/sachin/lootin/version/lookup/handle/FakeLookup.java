package com.github.sachin.lootin.version.lookup.handle;

public final class FakeLookup extends ClassLookup {

    public static final FakeLookup FAKE = build();

    private FakeLookup() throws IllegalAccessException {
        super((Class<?>) null);
    }

    private static final FakeLookup build() {
        try {
            return new FakeLookup();
        } catch (final IllegalAccessException e) {
            return null;
        }
    }

}