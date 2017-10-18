package com.syu.jni;

@SuppressWarnings("JniMissingFunction")
class SyuJniNative {
    private static final SyuJniNative INSTANCE = new SyuJniNative();

    public native int syu_jni_command(int i, Object obj, Object obj2);

    static {
        System.loadLibrary("syu_jni");
    }

    static SyuJniNative getInstance() {
        return INSTANCE;
    }
}
