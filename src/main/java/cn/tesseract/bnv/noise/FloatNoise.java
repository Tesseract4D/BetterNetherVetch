package cn.tesseract.bnv.noise;


import cn.tesseract.bnv.BNVMath;

public abstract class FloatNoise {
	public abstract float get(double x, double y);
	public abstract float get(double x, double y, double z);
	public abstract void setSeed(int seed);

	protected long hash(int x, int y, int z) {
		return BNVMath.hashCode(x, y, z);
	}

	protected int wrap(long value, int side) {
		int result = (int) (value - value / side * side);
		return result < 0 ? result + side : result;
	}

	public float getRange(double x, double y, float min, float max) {
		float value = get(x, y);
		return BNVMath.lerp(value, min, max);
	}

	public float getRange(double x, double y, double z, float min, float max) {
		float value = get(x, y, z);
		return BNVMath.lerp(value, min, max);
	}
}
