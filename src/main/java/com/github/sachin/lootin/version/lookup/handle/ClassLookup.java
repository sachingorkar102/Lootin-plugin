package com.github.sachin.lootin.version.lookup.handle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

import com.github.sachin.lootin.utils.ArrayUtils;
import com.github.sachin.lootin.utils.ReflectionUtils;
import com.github.sachin.lootin.version.lookup.handle.field.IFieldHandle;
import com.github.sachin.lootin.version.lookup.handle.field.SafeFieldHandle;
import com.github.sachin.lootin.version.lookup.handle.field.UnsafeDeclaredFieldHandle;
import com.github.sachin.lootin.version.lookup.handle.field.UnsafeStaticFieldHandle;
import com.syntaxphoenix.syntaxapi.reflection.ClassCache;

public class ClassLookup {
    
    public static final Lookup LOOKUP = MethodHandles.lookup();

    private Class<?> owner;
    private Lookup privateLookup;

    private final HashMap<String, MethodHandle> constructors = new HashMap<>();
    private final HashMap<String, MethodHandle> methods = new HashMap<>();
    private final HashMap<String, IFieldHandle<?>> fields = new HashMap<>();

    protected ClassLookup(final String classPath) throws IllegalAccessException {
        this(ClassCache.getClass(classPath));
    }

    protected ClassLookup(final Class<?> owner) throws IllegalAccessException {
        this.owner = owner;
        this.privateLookup = owner != null ? MethodHandles.privateLookupIn(owner, LOOKUP) : null;
    }

    /*
     * 
     */

    public Class<?> getOwner() {
        return owner;
    }

    public Lookup getPrivateLockup() {
        return privateLookup;
    }

    /*
     * 
     */

    public void delete() {
        constructors.clear();
        methods.clear();
        fields.clear();
        owner = null;
        privateLookup = null;
    }

    public boolean isValid() {
        return owner != null;
    }

    /*
     * 
     */

    public Collection<MethodHandle> getConstructors() {
        return constructors.values();
    }

    public Collection<MethodHandle> getMethods() {
        return methods.values();
    }

    public Collection<IFieldHandle<?>> getFields() {
        return fields.values();
    }

    /*
     * 
     */

    public MethodHandle getConstructor(final String name) {
        return isValid() ? constructors.get(name) : null;
    }

    public MethodHandle getMethod(final String name) {
        return isValid() ? methods.get(name) : null;
    }

    public IFieldHandle<?> getField(final String name) {
        return isValid() ? fields.get(name) : null;
    }

    /*
     * 
     */

    public boolean hasConstructor(final String name) {
        return isValid() && constructors.containsKey(name);
    }

    public boolean hasMethod(final String name) {
        return isValid() && methods.containsKey(name);
    }

    public boolean hasField(final String name) {
        return isValid() && fields.containsKey(name);
    }

    /*
     * 
     */

    public Object init() {
        if (!isValid()) {
            return null;
        }
        final MethodHandle handle = constructors.computeIfAbsent("$base#empty", ignore -> {
            try {
                return LOOKUP.unreflectConstructor(owner.getConstructor());
            } catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
                return null;
            }
        });
        if (handle == null) {
            constructors.remove("$base#empty");
            return null;
        }
        try {
            return handle.invoke();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object init(final String name, final Object... args) {
        if (!isValid() || !constructors.containsKey(name)) {
            return null;
        }
        try {
            return constructors.get(name).invokeWithArguments(args);
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 
     */

    public ClassLookup execute(final String name, final Object... args) {
        run(name, args);
        return this;
    }

    public ClassLookup execute(final Object source, final String name, final Object... args) {
        run(source, name, args);
        return this;
    }

    public Object run(final String name, final Object... args) {
        if (!isValid() || !methods.containsKey(name)) {
            return null;
        }
        try {
            return methods.get(name).invokeWithArguments(args);
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object run(final Object source, final String name, final Object... args) {
        if (!isValid() || !methods.containsKey(name)) {
            return null;
        }
        try {
            return methods.get(name).invokeWithArguments(mergeBack(args, source));
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 
     */

    public Object getFieldValue(final String name) {
        return isValid() && fields.containsKey(name) ? fields.get(name).getValue() : null;
    }

    public Object getFieldValue(final Object source, final String name) {
        return isValid() && fields.containsKey(name) ? fields.get(name).getValue(source) : null;
    }

    public void setFieldValue(final String name, final Object value) {
        if (!isValid() || !fields.containsKey(name)) {
            return;
        }
        fields.get(name).setValue(value);
    }

    public void setFieldValue(final Object source, final String name, final Object value) {
        if (!isValid() || !fields.containsKey(name)) {
            return;
        }
        fields.get(name).setValue(source, value);
    }

    /*
     * 
     */

    public ClassLookup searchConstructor(final Predicate<ClassLookup> predicate, final String name, final Class<?>... args) {
        return predicate.test(this) ? searchConstructor(name, args) : this;
    }

    public ClassLookup searchConstructor(final String name, final Class<?>... arguments) {
        if (hasConstructor(name)) {
            return this;
        }
        Constructor<?> constructor = null;
        try {
            constructor = owner.getDeclaredConstructor(arguments);
        } catch (NoSuchMethodException | SecurityException e) {
        }
        if (constructor == null) {
            try {
                constructor = owner.getConstructor(arguments);
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }
        if (constructor != null) {
            try {
                constructors.put(name, unreflect(constructor));
            } catch (final IllegalAccessException e) {
            }
        }
        return this;
    }

    public ClassLookup searchConstructorsByArguments(String base, final Class<?>... arguments) {
        final Constructor<?>[] constructors = ArrayUtils.merge(Constructor<?>[]::new, owner.getDeclaredConstructors(), owner.getConstructors());
        if (constructors.length == 0) {
            return this;
        }
        base += '-';
        int current = 0;
        for (final Constructor<?> constructor : constructors) {
            final Class<?>[] args = constructor.getParameterTypes();
            if (args.length != arguments.length) {
                continue;
            }
            try {
                if (ReflectionUtils.hasSameArguments(arguments, args)) {
                    this.constructors.put(base + current, unreflect(constructor));
                    current++;
                }
            } catch (final IllegalAccessException e) {
            }
        }
        return this;
    }

    /*
     * 
     */

    public ClassLookup searchMethod(final Predicate<ClassLookup> predicate, final String name, final String methodName,
        final Class<?>... arguments) {
        return predicate.test(this) ? searchMethod(name, methodName, arguments) : this;
    }

    public ClassLookup searchMethod(final String name, final String methodName, final Class<?>... arguments) {
        if (hasMethod(name)) {
            return this;
        }
        Method method = null;
        try {
            method = owner.getDeclaredMethod(methodName, arguments);
        } catch (NoSuchMethodException | SecurityException e) {
        }
        if (method == null) {
            try {
                method = owner.getMethod(methodName, arguments);
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }
        if (method != null) {
            try {
                methods.put(name, unreflect(method));
            } catch (IllegalAccessException | SecurityException e) {
            }
        }
        return this;
    }

    public ClassLookup searchMethodsByArguments(String base, final Class<?>... arguments) {
        final Method[] methods = ArrayUtils.merge(Method[]::new, owner.getDeclaredMethods(), owner.getMethods());
        if (methods.length == 0) {
            return this;
        }
        base += '-';
        int current = 0;
        for (final Method method : methods) {
            final Class<?>[] args = method.getParameterTypes();
            if (args.length != arguments.length) {
                continue;
            }
            try {
                if (ReflectionUtils.hasSameArguments(arguments, args)) {
                    this.methods.put(base + current, unreflect(method));
                    current++;
                }
            } catch (IllegalAccessException | SecurityException e) {
            }
        }
        return this;
    }

    /*
     * 
     */

    public ClassLookup searchField(final Predicate<ClassLookup> predicate, final String name, final String fieldName, final Class<?> type) {
        return predicate.test(this) ? searchField(name, fieldName, type) : this;
    }

    public ClassLookup searchField(final String name, final String fieldName) {
        if (hasMethod(name)) {
            return this;
        }
        Field field = null;
        try {
            field = owner.getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
        }
        if (field == null) {
            try {
                field = owner.getField(fieldName);
            } catch (NoSuchFieldException | SecurityException e) {
            }
        }
        if (field != null) {
            storeField(name, field);
        }
        return this;
    }

    public ClassLookup searchField(final String name, final String fieldName, final Class<?> type) {
        if (hasField(name)) {
            return this;
        }
        VarHandle handle = null;
        try {
            handle = privateLookup.findVarHandle(owner, fieldName, type);
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
        if (handle == null) {
            try {
                handle = privateLookup.findStaticVarHandle(owner, fieldName, type);
            } catch (SecurityException | NoSuchFieldException | IllegalAccessException e) {
            }
        }
        if (handle != null) {
            fields.put(name, new SafeFieldHandle(handle));
        }
        return this;
    }

    /*
     * 
     */

    public boolean putField(final String name, final Field field) {
        return putField(name, field, false);
    }

    public boolean putField(final String name, final Field field, final boolean forceSafe) {
        if (field == null || name == null || field.getDeclaringClass() != owner || fields.containsKey(name)) {
            return false;
        }
        storeField(name, field, forceSafe);
        return true;
    }

    /*
     * 
     */

    private void storeField(final String name, final Field field) {
        storeField(name, field, false);
    }

    private void storeField(final String name, final Field field, final boolean forceSafe) {
        if (forceSafe || !Modifier.isFinal(field.getModifiers())) {
            try {
                fields.put(name, new SafeFieldHandle(unreflect(field)));
                return;
            } catch (IllegalAccessException | SecurityException e) {
                if (forceSafe) {
                    return;
                }
            }
        }
        if (!Modifier.isStatic(field.getModifiers())) {
            fields.put(name, new UnsafeDeclaredFieldHandle(field));
            return;
        }
        fields.put(name, new UnsafeStaticFieldHandle(field));
    }

    private VarHandle unreflect(final Field field) throws IllegalAccessException, SecurityException {
        if (Modifier.isStatic(field.getModifiers())) {
            final boolean access = field.canAccess(null);
            if (!access) {
                field.setAccessible(true);
            }
            final VarHandle out = LOOKUP.unreflectVarHandle(field);
            if (!access) {
                field.setAccessible(false);
            }
            return out;
        }
        if (field.trySetAccessible()) {
            final VarHandle out = LOOKUP.unreflectVarHandle(field);
            field.setAccessible(false);
            return out;
        }
        return LOOKUP.unreflectVarHandle(field);
    }

    private MethodHandle unreflect(final Method method) throws IllegalAccessException, SecurityException {
        if (Modifier.isStatic(method.getModifiers())) {
            final boolean access = method.canAccess(null);
            if (!access) {
                method.setAccessible(true);
            }
            final MethodHandle out = LOOKUP.unreflect(method);
            if (!access) {
                method.setAccessible(false);
            }
            return out;
        }
        if (method.trySetAccessible()) {
            final MethodHandle out = LOOKUP.unreflect(method);
            method.setAccessible(false);
            return out;
        }
        return LOOKUP.unreflect(method);
    }

    private MethodHandle unreflect(final Constructor<?> constructor) throws IllegalAccessException {
        final boolean access = constructor.canAccess(null);
        if (!access) {
            constructor.setAccessible(true);
        }
        final MethodHandle out = LOOKUP.unreflectConstructor(constructor);
        if (!access) {
            constructor.setAccessible(false);
        }
        return out;
    }

    /*
     * 
     */

    public static Object[] mergeBack(final Object[] array1, final Object... array2) {
        final Object[] output = new Object[array1.length + array2.length];
        System.arraycopy(array2, 0, output, 0, array2.length);
        System.arraycopy(array1, 0, output, array2.length, array1.length);
        return output;
    }

    /*
     * 
     */

    public static final ClassLookup of(final Class<?> clazz) {
        try {
            return new ClassLookup(clazz);
        } catch (final IllegalAccessException e) {
            return null;
        }
    }

    public static final ClassLookup of(final String path) {
        try {
            return new ClassLookup(path);
        } catch (final IllegalAccessException e) {
            return null;
        }
    }

}
