package cn.tesseract.bnv.world.generator.terrain.features;

import cn.tesseract.bnv.BNV;
import cn.tesseract.bnv.BNVMath;
import cn.tesseract.bnv.noise.FractalNoise;
import cn.tesseract.bnv.noise.PerlinNoise;
import cn.tesseract.bnv.noise.SDFScatter2D;
import cn.tesseract.bnv.world.generator.BNBWorldGenerator;
import cn.tesseract.bnv.world.generator.terrain.TerrainMap;
import org.joml.Vector3d;

import java.util.Random;

public class LandPillarsFeature extends TerrainFeature {
    private static final String FEATURE_ID = BNV.id("land_pillars");
    private final SDFScatter2D scatter = new SDFScatter2D(this::getPillar);
    private final FractalNoise noise = new FractalNoise(PerlinNoise::new);
    private final Random random = new Random();
    private TerrainMap map;

    public LandPillarsFeature() {
        noise.setOctaves(2);
    }

    @Override
    public float getDensity(int x, int y, int z) {
        float density = gradient(y, 96, 128, 1, -1);
        density = Math.max(density, gradient(y, 224, 256, -1, 1));
        density += noise.get(x * 0.03, z * 0.03) - 0.5F;

        float feature = scatter.get(x * 0.03, y * 0.03, z * 0.03);
        feature += noise.get(x * 0.01, y * 0.01, z * 0.01) * 0.15F;
        feature += noise.get(x * 0.07, y * 0.07, z * 0.07) * 0.1F;
        density = smoothMax(density, feature, 1F);

        return density;
    }

    @Override
    public void setSeed(int seed) {
        RANDOM.setSeed(seed);
        scatter.setSeed(RANDOM.nextInt());
        noise.setSeed(RANDOM.nextInt());
    }

    private float getPillar(int seed, Vector3d localPos, Vector3d worldPos) {
        int px = BNVMath.floor_double(worldPos.x / 0.03);
        int pz = BNVMath.floor_double(worldPos.z / 0.03);
        if (map == null) map = BNBWorldGenerator.getMapCopy();
        if (map != null) {
            if (
                !map.getData(px, pz).equals(FEATURE_ID) ||
                    !map.getData(px + 16, pz).equals(FEATURE_ID) ||
                    !map.getData(px - 16, pz).equals(FEATURE_ID) ||
                    !map.getData(px, pz + 16).equals(FEATURE_ID) ||
                    !map.getData(px, pz - 16).equals(FEATURE_ID)
            ) return 0;
        }

        random.setSeed(seed);

        float height = (100 + random.nextFloat() * 80) * 0.03F;
        float tip = height + (10 + random.nextFloat() * 10) * 0.03F;
        float dist = 0.7F - BNVMath.sqrt_double(localPos.x * localPos.x + localPos.z * localPos.z);

        float support = gradient((float) localPos.y, 100 * 0.03F, height, -1.0F, 1.0F);
        float top = gradient((float) localPos.y, height, tip, 0.0F, 1.0F);
        support = Math.min(support * support, (1.0F - top * top * top) * 8.0F - 7.0F);

        return dist + support * 0.2F;
    }
}
