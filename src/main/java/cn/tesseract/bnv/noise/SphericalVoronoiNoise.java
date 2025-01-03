package cn.tesseract.bnv.noise;


import cn.tesseract.bnv.BNVMath;

public class SphericalVoronoiNoise extends VoronoiNoise {
	@Override
	public float get(double x, double y, double z) {
		float value = super.get(x, y, z);
		value = 1.0F - value * value;
		return BNVMath.sqrt_double(value);
	}

	@Override
	public float get(double x, double y) {
		float value = super.get(x, y);
		value = 1.0F - value * value;
		return BNVMath.sqrt_double(value);
	}
}
