package com.github.sachin.lootin.version.lookup.handle;

import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;

public class ClassLookupCache {

    protected final HashMap<String, ClassLookup> cache = new HashMap<>();

    public void clear() {
        cache.values().forEach(ClassLookup::delete);
        cache.clear();
    }

    public Optional<ClassLookup> get(final String name) {
        return Optional.ofNullable(cache.get(name));
    }

    public boolean has(final String name) {
        return cache.containsKey(name);
    }

    public ClassLookup create(final String name, final String path) {
        if (has(name)) {
            return cache.get(name);
        }
        final ClassLookup reflect = create(path);
        cache.put(name, reflect);
        return reflect;
    }

    public ClassLookup create(final String name, final Class<?> clazz) {
        if (has(name)) {
            return cache.get(name);
        }
        final ClassLookup reflect = create(clazz);
        cache.put(name, reflect);
        return reflect;
    }

    public void delete(final String name) {
        cache.remove(name);
    }

    @SuppressWarnings("unchecked")
    public Entry<String, ClassLookup>[] entries() {
        return cache.entrySet().toArray(Entry[]::new);
    }

    private ClassLookup create(final Class<?> clazz) {
        return ClassLookup.of(clazz);
    }

    private ClassLookup create(final String path) {
        return ClassLookup.of(path);
    }

}