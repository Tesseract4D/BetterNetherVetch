package cn.tesseract.bnv.world.generator.terrain.features;

import cn.tesseract.bnv.noise.FractalNoise;
import cn.tesseract.bnv.noise.PerlinNoise;

public class PlainsFeature extends TerrainFeature {
    private final FractalNoise noise = new FractalNoise(PerlinNoise::new);

    public PlainsFeature() {
        noise.setOctaves(2);
    }

    @Override
    public float getDensity(int x, int y, int z) {
        float density = gradient(y, 96, 128, 1, -1);
        density = Math.max(density, gradient(y, 224, 256, -1, 1));
        return density + noise.get(x * 0.03, z * 0.03) * 0.8F - 0.4F;
    }

    @Override
    public void setSeed(int seed) {
        noise.setSeed(seed);
    }
}
