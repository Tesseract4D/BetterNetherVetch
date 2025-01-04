package cn.tesseract.bnv;

import net.minecraft.util.MathHelper;

public class BNVMath extends MathHelper {
    public static int lerp(double delta, int start, int end) {
        return (int)Math.round((double)start + (double)(end - start) * delta);
    }

    public static float lerp(double delta, float start, float end) {
        return start + (end - start) * (float) delta;
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static long hashCode(int x, int y, int z) {
        long l = (long)x * 3129871L ^ (long)z * 116129781L ^ (long)y;
        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }
}
