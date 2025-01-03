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

public class CubesFeature extends TerrainFeature {
    private static final String FEATURE_ID = BNV.id("cubes");
    private final SDFScatter2D scatterFloor = new SDFScatter2D(this::getCubesFloor);
    private final SDFScatter2D scatterCeiling = new SDFScatter2D(this::getCubesCeiling);
    private final FractalNoise noise = new FractalNoise(PerlinNoise::new);
    private final Matrix3d matrixFloor = new Matrix3d();
    private final Matrix3d matrixCeiling = new Matrix3d();
    private final Vector3d axis = new Vector3d(0);
    private final Random random = new Random();
    private int lastSeedFloor;
    private int lastSeedCeiling;
    private TerrainMap map;

    public CubesFeature() {
        noise.setOctaves(3);
    }

    @Override
    public float getDensity(int x, int y, int z) {
        float density = gradient(y, 112, 144, 1, -1);
        density = Math.max(density, gradient(y, 224, 256, -1, 1));
        density += noise.get(x * 0.03, z * 0.03) * 0.8F - 0.4F;

        if (map == null) map = BNBWorldGenerator.getMapCopy();

        float cubes = scatterFloor.get(x * 0.03, y * 0.03, z * 0.03);
        density = smoothMax(density, cubes, 0.5F);

        cubes = scatterCeiling.get(x * 0.03, y * 0.03, z * 0.03);
        density = smoothMax(density, cubes, 0.5F);

        return density;
    }

    @Override
    public void setSeed(int seed) {
        RANDOM.setSeed(seed);
        scatterFloor.setSeed(RANDOM.nextInt());
        scatterCeiling.setSeed(RANDOM.nextInt());
        noise.setSeed(RANDOM.nextInt());
    }

    private float getCubesFloor(int seed, Vector3d relativePos, Vector3d worldPos) {
        int px = BNVMath.floor_double(worldPos.x / 0.03);
        int pz = BNVMath.floor_double(worldPos.z / 0.03);
        if (map != null && map.getData(px, pz) != FEATURE_ID) {
            return 0;
        }

        random.setSeed(seed);

        float size = BNVMath.lerp(random.nextFloat(), 0.75F, 1.0F);
        float angle = random.nextFloat() * 6.283F;

        relativePos.y -= (random.nextFloat() * 20 + 110) * 0.03;

        if (seed != lastSeedFloor) {
            lastSeedFloor = seed;
            set(axis, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            normalize(axis);
            matrixFloor.rotation(angle, axis);
        }

        matrixFloor.transform(relativePos);
        float dx = Math.abs((float) relativePos.x);
        float dy = Math.abs((float) relativePos.y);
        float dz = Math.abs((float) relativePos.z);

        float d = Math.max(Math.max(dx, dy), dz);
        return size - d;
    }

    private float getCubesCeiling(int seed, Vector3d relativePos, Vector3d worldPos) {
        int px = BNVMath.floor_double(worldPos.x / 0.03);
        int pz = BNVMath.floor_double(worldPos.z / 0.03);
        if (map != null && map.getData(px, pz) != FEATURE_ID) {
            return 0;
        }

        random.setSeed(seed);

        float size = BNVMath.lerp(random.nextFloat(), 0.75F, 1.0F);
        float angle = random.nextFloat() * 6.283F;

        relativePos.y -= (250 - random.nextFloat() * 15) * 0.03;

        if (seed != lastSeedCeiling) {
            lastSeedCeiling = seed;
            set(axis, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            normalize(axis);
            matrixCeiling.rotation(angle, axis);
        }

        matrixCeiling.transform(relativePos);
        float dx = Math.abs((float) relativePos.x);
        float dy = Math.abs((float) relativePos.y);
        float dz = Math.abs((float) relativePos.z);

        float d = Math.max(Math.max(dx, dy), dz);
        return size - d;
    }

    private static void set(Vector3d v, float x, float y, float z) {
        v.x = x;
        v.y = y;
        v.z = z;
    }

    private static void normalize(Vector3d v) {
        double l = v.x * v.x + v.y * v.y + v.z * v.z;
        if (l < 1E-6) return;
        l = Math.sqrt(l);
        v.x /= l;
        v.y /= l;
        v.z /= l;
    }
}
