package cn.tesseract.bnv.world.generator.terrain.features;

import cn.tesseract.bnv.noise.FractalNoise;
import cn.tesseract.bnv.noise.PerlinNoise;

public class FlatCliffFeature extends TerrainFeature {
	private final FractalNoise noise = new FractalNoise(PerlinNoise::new);

	public FlatCliffFeature() {
		noise.setOctaves(3);
	}

	@Override
	public float getDensity(int x, int y, int z) {
		float density = gradient(y, 98, 135, -0.5F, 1.0F);
		float top = 140 + noise.get(x * 0.05, z * 0.05) * 10.0F;
		density = density * density;
		density = Math.min(density * density * 2.0F + 1.0F, gradient(y, 135, top, 3.0F, -1.0F));
		density = Math.max(density, gradient(y, 224, 256, -1, 1));
		return density + noise.get(x * 0.02, y * 0.02, z * 0.02) * 1.4F - 0.7F;
	}

	@Override
	public void setSeed(int seed) {
		noise.setSeed(seed);
	}
}
