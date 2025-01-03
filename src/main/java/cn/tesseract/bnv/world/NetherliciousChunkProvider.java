package cn.tesseract.bnv.world;

import DelirusCrux.Netherlicious.Dimension.MaxHeightNetherChunkProvider;
import DelirusCrux.Netherlicious.Utility.Configuration.WorldgenConfiguration;
import DelirusCrux.Netherlicious.World.Features.Terrain.Dimension.MapGenNetherCavesMaxHeight;
import DelirusCrux.Netherlicious.World.Features.Terrain.Dimension.MapGenNetherRavineMaxHeight;
import cn.tesseract.bnv.world.generator.BNBWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.MapGenBase;

public class NetherliciousChunkProvider extends MaxHeightNetherChunkProvider {
    private World worldObj;

    private MapGenBase netherCaveGenerator = new MapGenNetherCavesMaxHeight();
    private MapGenBase netherRavineGenerator = new MapGenNetherRavineMaxHeight();

    public NetherliciousChunkProvider(World world, long seed) {
        super(world, seed);
        worldObj = world;
        BNBWorldGenerator.updateData(world.saveHandler, seed);
    }

    @Override
    public Chunk provideChunk(int cx, int cz) {
        Block[] blocks = BNBWorldGenerator.makeBlocks(worldObj, cx, cz);
        byte[] meta = new byte[blocks.length];
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, cx * 16, cz * 16, 16, 16);
        replaceBiomeBlocks(cx, cz, blocks, meta, abiomegenbase);
        this.netherCaveGenerator.func_151539_a(this, this.worldObj, cx, cz, blocks);
        if (WorldgenConfiguration.Ravines) {
            this.netherRavineGenerator.func_151539_a(this, this.worldObj, cx, cz, blocks);
        }
        this.genNetherBridge.func_151539_a(this, this.worldObj, cx, cz, blocks);
        Chunk chunk = new Chunk(this.worldObj, blocks, meta, cx, cz);
        byte[] abyte = chunk.getBiomeArray();
        for (int k = 0; k < abyte.length; k++) {
            abyte[k] = (byte) abiomegenbase[k].biomeID;
        }
        chunk.resetRelightChecks();
        return chunk;
    }
}
