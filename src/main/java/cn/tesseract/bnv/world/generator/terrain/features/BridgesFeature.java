package cn.tesseract.bnv.world.generator.terrain.features;

import cn.tesseract.bnv.BNVMath;
import cn.tesseract.bnv.noise.FractalNoise;
import cn.tesseract.bnv.noise.PerlinNoise;
import cn.tesseract.bnv.noise.VoronoiNoise;

public class BridgesFeature extends TerrainFeature {
	private final FractalNoise noise = new FractalNoise(PerlinNoise::new);
	private final FractalNoise distortionX = new FractalNoise(PerlinNoise::new);
	private final FractalNoise distortionZ = new FractalNoise(PerlinNoise::new);
	private final VoronoiNoise bridgesNoise = new VoronoiNoise();

	public BridgesFeature() {
		noise.setOctaves(2);
	}

	@Override
	public float getDensity(int x, int y, int z) {
		float density = gradient(y, 80, 96, 1, -1);
		density = Math.max(density, gradient(y, 224, 256, -1, 1));
		density += noise.get(x * 0.03, z * 0.03) * 0.8F - 0.4F;

		//float bridges = ChunkTerrainMap.TERRAIN_MAP.getBridges(x, z) - 0.9F;

		float height = 112;// + noise.get(x * 0.01, z * 0.01) * 5.0F;
		float depth = gradient(y, height - 10, height + 10, -1.0F, 1.0F);
		depth = BNVMath.sqrt_double(1 - depth * depth);
		return density + depth * 2.5F;//depth * 20.5F * bridges;
	}

	@Override
	public void setSeed(int seed) {
		noise.setSeed(seed);
	}
}
