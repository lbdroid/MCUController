package com.syu.jni;

import android.os.Bundle;

public class ToolsJni {
    private static int cmdCpu = -1;

    public static int testJni() {
        Bundle inparam = new Bundle();
        Bundle outparam = new Bundle();
        inparam.putInt("test_param", 100);
        int result = SyuJniNative.getInstance().syu_jni_command(0, inparam, outparam);
        outparam.getDouble("test_param");
        return result;
    }

    public static int cmd_1_reverse_video_mirror(int value) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", value);
        return SyuJniNative.getInstance().syu_jni_command(1, inparam, null);
    }

    public static int cmd_2_soundMix(int value) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", value);
        return SyuJniNative.getInstance().syu_jni_command(2, inparam, null);
    }

    public static int cmd_3_encryption_result() {
        Bundle outparam = new Bundle();
        SyuJniNative.getInstance().syu_jni_command(3, null, outparam);
        return outparam.getInt("param0", 1);
    }

    public static int cmd_4_audio_state() {
        Bundle outparam = new Bundle();
        SyuJniNative.getInstance().syu_jni_command(4, null, outparam);
        return outparam.getInt("param0", 0);
    }

    public static int cmd_5_turnoff_lcdc(int isClose) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", isClose);
        return SyuJniNative.getInstance().syu_jni_command(5, inparam, null);
    }

    public static int cmd_6_mute_amp(int mute) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", mute);
        return SyuJniNative.getInstance().syu_jni_command(6, inparam, null);
    }

    public static int cmd_7_get_amp_state() {
        Bundle outparam = new Bundle();
        SyuJniNative.getInstance().syu_jni_command(7, null, outparam);
        return outparam.getInt("param0", 0);
    }

    public static int cmd_8_reset_gps() {
        return SyuJniNative.getInstance().syu_jni_command(8, null, null);
    }

    public static int cmd_9_poweron_screen(int on) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", on);
        return SyuJniNative.getInstance().syu_jni_command(9, inparam, null);
    }

    public static int cmd_10_little_hom(int on) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", on);
        return SyuJniNative.getInstance().syu_jni_command(10, inparam, null);
    }

    public static int cmd_14_set_reverse_video_type(int type) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", type);
        return SyuJniNative.getInstance().syu_jni_command(14, inparam, null);
    }

    public static int cmd_16_set_led_color(int color, int save) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", color);
        inparam.putInt("param1", save);
        return SyuJniNative.getInstance().syu_jni_command(16, inparam, null);
    }

    public static int cmd_17_get_led_color() {
        Bundle outparam = new Bundle();
        SyuJniNative.getInstance().syu_jni_command(17, null, outparam);
        return outparam.getInt("param0", 0);
    }

    public static int cmd_19_airplane_mode(int on) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", on);
        return SyuJniNative.getInstance().syu_jni_command(19, inparam, null);
    }

    public static int cmd_22_set_video_mode(int value) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", value);
        return SyuJniNative.getInstance().syu_jni_command(22, inparam, null);
    }

    public static void cmd_24_reset_8288a() {
        SyuJniNative.getInstance().syu_jni_command(24, null, null);
    }

    public static int cmd_25_get_video_mode() {
        Bundle outparam = new Bundle();
        SyuJniNative.getInstance().syu_jni_command(25, null, outparam);
        return outparam.getInt("param0", 0);
    }

    public static int cmd_26_get_video_signal_on() {
        Bundle outparam = new Bundle();
        SyuJniNative.getInstance().syu_jni_command(26, null, outparam);
        return outparam.getInt("param0", 1);
    }

    public static int cmd_29_acc_state_to_bsp(int value) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", value);
        return SyuJniNative.getInstance().syu_jni_command(29, inparam, null);
    }

    public static void cmd_31_fan_en(int value) {
        if (cmdCpu != value) {
            cmdCpu = value;
            Bundle inparam = new Bundle();
            inparam.putInt("param0", cmdCpu);
            SyuJniNative.getInstance().syu_jni_command(31, inparam, null);
        }
    }

    public static int cmd_32_get_boot_reverse_status() {
        Bundle outparam = new Bundle();
        SyuJniNative.getInstance().syu_jni_command(32, null, outparam);
        return outparam.getInt("param0", 0);
    }

    public static void cmd_33_reset_videoIc(int value) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", 1);
        SyuJniNative.getInstance().syu_jni_command(33, inparam, null);
    }

    public static byte[] cmd_101_getT132Parama() {
        byte[] result = new byte[16384];
        Bundle outparam = new Bundle();
        outparam.putByteArray("param1", result);
        SyuJniNative.getInstance().syu_jni_command(101, null, outparam);
        return result;
    }

    public static int cmd_104_write_gamma(byte[] data) {
        Bundle inparam = new Bundle();
        inparam.putByteArray("param0", data);
        return SyuJniNative.getInstance().syu_jni_command(104, inparam, null);
    }

    public static int cmd_153_gsensor_power_onoff(int value) {
        Bundle inparam = new Bundle();
        inparam.putInt("param0", value);
        return SyuJniNative.getInstance().syu_jni_command(153, inparam, null);
    }
}
