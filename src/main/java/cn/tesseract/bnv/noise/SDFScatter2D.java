package cn.tesseract.bnv.noise;

import cn.tesseract.bnv.BNVMath;
import org.joml.Vector3d;

public class SDFScatter2D extends FloatNoise {
    private final Vector3d pos = new Vector3d(0, 0, 0);
    private final Vector3d worldPos = new Vector3d(0, 0, 0);
    private final ScatterSDF sdf;
    private int seed;

    public SDFScatter2D(ScatterSDF sdf) {
        this.sdf = sdf;
    }

    @Override
    public float get(double x, double y) {
        return 0;
    }

    @Override
    public float get(double x, double y, double z) {
        int x1 = BNVMath.floor_double(x);
        int z1 = BNVMath.floor_double(z);

        float sdx = (float) (x - x1);
        float sdz = (float) (z - z1);

        float distance = -1000;

        for (byte i = -1; i < 2; i++) {
            for (byte k = -1; k < 2; k++) {
                float dx = wrap(hash(x1 + i, z1 + k, seed), 3607) / 3607.0F * 0.7F;
                float dz = wrap(hash(x1 + i, z1 + k, seed + 23), 3607) / 3607.0F * 0.7F;
                worldPos.x = x + dx + i - sdx;
                worldPos.z = z + dz + k - sdz;
                worldPos.y = y;
                pos.x = dx + i - sdx;
                pos.z = dz + k - sdz;
                pos.y = y;
                int featureSeed = wrap(hash(x1 + i, z1 + k, seed + 157), 378632);
                float d = sdf.getDensity(featureSeed, pos, worldPos);
                if (d > distance) distance = d;
            }
        }

        return distance;
    }

    @Override
    public void setSeed(int seed) {
        this.seed = seed;
    }
}
