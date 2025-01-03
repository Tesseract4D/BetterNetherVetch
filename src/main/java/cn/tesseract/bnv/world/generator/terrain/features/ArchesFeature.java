package cn.tesseract.bnv.world.generator.terrain.features;

import cn.tesseract.bnv.BNV;
import cn.tesseract.bnv.BNVMath;

import cn.tesseract.bnv.noise.FractalNoise;
import cn.tesseract.bnv.noise.PerlinNoise;
import cn.tesseract.bnv.noise.SDFScatter2D;
import cn.tesseract.bnv.world.generator.BNBWorldGenerator;
import cn.tesseract.bnv.world.generator.terrain.TerrainMap;
import org.joml.Matrix3d;
import org.joml.Vector3d;

import java.util.Random;

public class ArchesFeature extends TerrainFeature {
    private static final String FEATURE_ID = BNV.id("arches");
    private final SDFScatter2D scatter = new SDFScatter2D(this::getArches);
    private final FractalNoise noise = new FractalNoise(PerlinNoise::new);
    private final PerlinNoise distortionX = new PerlinNoise();
    private final PerlinNoise distortionY = new PerlinNoise();
    private final PerlinNoise distortionZ = new PerlinNoise();
    private final Matrix3d matrix = new Matrix3d();
    private final Vector3d axis = new Vector3d(0, 0, 0);
    private final Random random = new Random();
    private TerrainMap map;
    private int lastSeed;

    public ArchesFeature() {
        noise.setOctaves(5);
    }

    @Override
    public float getDensity(int x, int y, int z) {
        float density = gradient(y, 96, 128, 1, -1);
        density = Math.max(density, gradient(y, 224, 256, -1, 1));
        density += noise.get(x * 0.03, z * 0.03) * 0.8F - 0.4F;

        float arches = scatter.get(x * 0.03, y * 0.03, z * 0.03);
        arches += noise.get(x * 0.02, y * 0.02, z * 0.02) * 0.1F;
        density = smoothMax(density, arches, 0.75F);

        return density;
    }

    @Override
    public void setSeed(int seed) {
        RANDOM.setSeed(seed);
        scatter.setSeed(RANDOM.nextInt());
        noise.setSeed(RANDOM.nextInt());
        distortionX.setSeed(RANDOM.nextInt());
        distortionY.setSeed(RANDOM.nextInt());
        distortionZ.setSeed(RANDOM.nextInt());
    }

    private float getArches(int seed, Vector3d relativePos, Vector3d worldPos) {
        int px = BNVMath.floor_double(worldPos.x / 0.03);
        int pz = BNVMath.floor_double(worldPos.z / 0.03);
        if (map == null) map = BNBWorldGenerator.getMapCopy();
        if (map != null && map.getData(px, pz) != FEATURE_ID) {
            return 0;
        }

        random.setSeed(seed);

        float radiusBig = BNVMath.lerp(random.nextFloat(), 0.1F, 0.75F);
        float radiusSmall = BNVMath.lerp(random.nextFloat(), radiusBig * 0.13F, radiusBig * 0.25F);
        float angle = random.nextFloat() * 6.283F;

        relativePos.y -= (random.nextFloat() * 20 + 96) * 0.03;

        if (seed != lastSeed) {
            lastSeed = seed;
            set(axis, random.nextFloat() * 0.2F - 0.1F, 1F, random.nextFloat() * 0.2F - 0.1F);
            axis.normalize();
            matrix.rotation(angle, axis);
        }

        matrix.transform(relativePos);

        float dx = seed / 300.0F;
        float dy = seed / 600.0F;
        float dz = seed / 900.0F;

        float x = (float) (relativePos.x + distortionX.get(relativePos.x + dx, relativePos.y + dy, relativePos.z + dz) * 0.3F - 0.15F);
        float y = (float) (relativePos.y + distortionY.get(relativePos.x + dx, relativePos.y + dy, relativePos.z + dz) * 0.3F - 0.15F);
        float z = (float) (relativePos.z + distortionZ.get(relativePos.x + dx, relativePos.y + dy, relativePos.z + dz) * 0.3F - 0.15F);

        return 0.5F - torus(x, y, z, radiusBig, radiusSmall);
    }

    float torus(float x, float y, float z, float radiusBig, float radiusSmall) {
        float l = BNVMath.sqrt_double(x * x + y * y);
        l = l - radiusBig;
        l = BNVMath.sqrt_double(l * l + z * z);
        return l - radiusSmall;
    }

    private static void set(Vector3d v, float x, float y, float z) {
        v.x = x;
        v.y = y;
        v.z = z;
    }
}
