package com.github.sachin.lootin.utils;

import java.util.function.IntFunction;

public final class ArrayUtils {
    
    private ArrayUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    @SafeVarargs
    public static <E> E[] merge(IntFunction<E[]> function, E[] array1, E... array2) {
        E[] output = function.apply(array1.length + array2.length);
        System.arraycopy(array1, 0, output, 0, array1.length);
        System.arraycopy(array2, 0, output, array1.length, array2.length);
        return output;
    }
    
}
