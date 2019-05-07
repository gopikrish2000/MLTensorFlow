package com.mlrecommendation.gopi.gopimlrecommendation.utils;

class CommonUtils {

    public static long getCurrentUsedMemoryInMB() {
        final Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1048576L; // final long usedMemInMB
    }
}
