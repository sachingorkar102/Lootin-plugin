package com.github.sachin.lootin.utils;

public final class ReflectionUtils {
    
    private ReflectionUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Class<?> getClass(String classPath) {
        try {
            return Class.forName(classPath);
        } catch(ClassNotFoundException ignored) {
            return null;
        }
    }

    public static boolean hasSameArguments(Class<?>[] compare1, Class<?>[] compare2) {
        if (compare1.length == 0 && compare2.length == 0) {
            return true;
        } else if (compare1.length != compare2.length) {
            return false;
        }
        for (Class<?> arg1 : compare1) {
            boolean found = true;
            for (Class<?> arg2 : compare2) {
                if (!arg1.isAssignableFrom(arg2)) {
                    found = false;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

}
