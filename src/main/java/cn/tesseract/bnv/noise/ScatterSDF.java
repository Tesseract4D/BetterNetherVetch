package cn.tesseract.bnv.noise;

import org.joml.Vector3d;

@FunctionalInterface
public interface ScatterSDF {
	float getDensity(int seed, Vector3d relativePos, Vector3d worldPos);
}
