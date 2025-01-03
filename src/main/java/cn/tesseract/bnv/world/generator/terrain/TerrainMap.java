package cn.tesseract.bnv.world.generator.terrain;

import cn.tesseract.bnv.BNV;
import cn.tesseract.bnv.noise.FractalNoise;
import cn.tesseract.bnv.noise.PerlinNoise;
import cn.tesseract.bnv.noise.VoronoiNoise;
import cn.tesseract.bnv.world.generator.map.DataMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import net.minecraft.world.storage.ISaveHandler;
import org.joml.Vector2i;

import java.util.*;

public class TerrainMap extends DataMap<String> {
    private static final String DEFAULT_TERRAIN = BNV.id("plains");
    private static final Vector2i[] OFFSETS;
    private static final float MULTIPLIER;

    private final EnumMap<TerrainRegion, List<String>> regionTerrain = new EnumMap<>(TerrainRegion.class);
    private final FractalNoise oceanNoise = new FractalNoise(PerlinNoise::new);
    private final FractalNoise mountainNoise = new FractalNoise(PerlinNoise::new);
    private final VoronoiNoise bridgesNoise = new VoronoiNoise();
    private final VoronoiNoise cellNoise = new VoronoiNoise();
    private final Random random = new Random(0);

    public TerrainMap() {
        super("bnb_terrain");
        oceanNoise.setOctaves(3);
        mountainNoise.setOctaves(2);
        Arrays.stream(TerrainRegion.values()).forEach(
            region -> regionTerrain.put(region, new ArrayList<>()
            ));
    }

    @Override
    protected String serialize(String value) {
        return value;
    }

    @Override
    protected String deserialize(String name) {
        return name;
    }

    @Override
    public String generateData(int x, int z) {
        TerrainRegion region = getRegionInternal(x, z);
        List<String> list = regionTerrain.get(region);
        if (list.isEmpty()) return DEFAULT_TERRAIN;
        int index = (int) Math.floor(cellNoise.getID(x * 0.1, z * 0.1) * list.size());
        return list.get(index);
    }

    @Override
    public void setData(ISaveHandler data, int seed) {
        super.setData(data, seed);
        oceanNoise.setSeed(random.nextInt());
        mountainNoise.setSeed(random.nextInt());
        bridgesNoise.setSeed(random.nextInt());
        cellNoise.setSeed(random.nextInt());
    }

    public void addTerrain(String terrainID, TerrainRegion region) {
        regionTerrain.get(region).add(terrainID);
    }

    public void getDensity(int x, int z, Reference2FloatMap<String> data) {
        data.clear();
        for (Vector2i offset : OFFSETS) {
            String sdf = getData(x + offset.x, z + offset.y);
            float value = data.getOrDefault(sdf, 0.0F) + MULTIPLIER;
            data.put(sdf, value);
        }
    }

    public TerrainRegion getRegion(int x, int z) {
        double preX = (COS * x - SIN * z) / 16.0 + distortionX.get(x * 0.03, z * 0.03) * 1.5F;
        double preZ = (SIN * x + COS * z) / 16.0 + distortionX.get(x * 0.03, z * 0.03) * 1.5F;

        int px = (int) Math.floor(preX);
        int pz = (int) Math.floor(preZ);

        float dx = (float) (preX - px);
        float dz = (float) (preZ - pz);

        TerrainRegion a = getRegionInternal(px, pz);

        if (dx < 0.333F && dz < 0.333F) {
            TerrainRegion b = getRegionInternal(px - 1, pz - 1);
            TerrainRegion c = getRegionInternal(px - 1, pz);
            TerrainRegion d = getRegionInternal(px, pz - 1);
            if (b == c && c == d) {
                float v = dx + dz;
                return v < 0.333F ? c : a;
            }
        }

        if (dx > 0.666F && dz < 0.333F) {
            TerrainRegion b = getRegionInternal(px + 1, pz - 1);
            TerrainRegion c = getRegionInternal(px + 1, pz);
            TerrainRegion d = getRegionInternal(px, pz - 1);
            if (b == c && c == d) {
                float v = (1.0F - dx) + dz;
                return v < 0.333F ? c : a;
            }
        }

        if (dx < 0.333F && dz > 0.666F) {
            TerrainRegion b = getRegionInternal(px - 1, pz + 1);
            TerrainRegion c = getRegionInternal(px - 1, pz);
            TerrainRegion d = getRegionInternal(px, pz + 1);
            if (b == c && c == d) {
                float v = dx + (1.0F - dz);
                return v < 0.333F ? c : a;
            }
        }

        if (dx > 0.666F && dz > 0.666F) {
            TerrainRegion b = getRegionInternal(px + 1, pz + 1);
            TerrainRegion c = getRegionInternal(px + 1, pz);
            TerrainRegion d = getRegionInternal(px, pz + 1);
            if (b == c && c == d) {
                float v = (1.0F - dx) + (1.0F - dz);
                return v < 0.333F ? c : a;
            }
        }

        return a;
    }

    public TerrainRegion getRegionInternal(int x, int z) {
        float ocean = oceanNoise.get(x * 0.0375, z * 0.0375);
        float mountains = mountainNoise.get(x * 0.075, z * 0.075);
        if (ocean > 0.5F) {
            double px = x * 0.03 * 0.75 + distortionX.get(x * 0.015, z * 0.015);
            double pz = z * 0.03 * 0.75 + distortionZ.get(x * 0.015, z * 0.015);
            float bridges = bridgesNoise.getF1F2(px, pz);
            if (bridges > 0.9) return TerrainRegion.BRIDGES;
            if (
                oceanNoise.get((x + 1) * 0.0375, z * 0.0375) < 0.5F ||
                    oceanNoise.get((x - 1) * 0.0375, z * 0.0375) < 0.5F ||
                    oceanNoise.get(x * 0.0375, (z + 1) * 0.0375) < 0.5F ||
                    oceanNoise.get(x * 0.0375, (z - 1) * 0.0375) < 0.5F
            ) return mountains > 0.6F ? TerrainRegion.SHORE_MOUNTAINS : TerrainRegion.SHORE_NORMAL;
            if (ocean < 0.63F) return TerrainRegion.OCEAN_NORMAL;
            return mountains > 0.4F ? TerrainRegion.OCEAN_MOUNTAINS : TerrainRegion.OCEAN_NORMAL;
        }
        return mountains > 0.6F ? TerrainRegion.MOUNTAINS : mountains > 0.53F ? TerrainRegion.HILLS : TerrainRegion.PLAINS;
    }

    static {
        int radius = 5;
        List<Vector2i> offsets = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius) {
                    offsets.add(new Vector2i(x << 2, z << 2));
                }
            }
        }
        OFFSETS = offsets.toArray(new Vector2i[0]);
        MULTIPLIER = 1F / OFFSETS.length;
    }
}
