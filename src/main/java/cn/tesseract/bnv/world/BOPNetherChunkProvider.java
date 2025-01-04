package cn.tesseract.bnv.world;

import cn.tesseract.bnv.world.generator.BNBWorldGenerator;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SHROOM;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.QUARTZ;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.*;

public class BOPNetherChunkProvider extends ChunkProviderHell {
    public BOPNetherChunkProvider(World world, long seed) {
        super(world, seed);
        BNBWorldGenerator.updateData(world.saveHandler, seed);
    }

    @Override
    public Chunk provideChunk(int cx, int cz) {
        Block[] blocks = BNBWorldGenerator.makeBlocks(worldObj, cx, cz);
        byte[] meta = new byte[blocks.length];
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, cx * 16, cz * 16, 16, 16);

        replaceBiomeBlocks(cx, cz, blocks, meta, abiomegenbase);
        Chunk chunk = new Chunk(this.worldObj, blocks, meta, cx, cz);
        byte[] abyte = chunk.getBiomeArray();
        for (int k = 0; k < abyte.length; k++) {
            abyte[k] = (byte) abiomegenbase[k].biomeID;
        }
        chunk.resetRelightChecks();
        return chunk;
    }

    public void replaceBiomeBlocks(int cx, int cz, Block[] blocks, byte[] meta, BiomeGenBase[] biomes) {
        ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, cx, cz, blocks, meta, biomes, this.worldObj);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.DENY) return;

        byte b0 = 64;
        double d0 = 0.03125D;
        this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, cx * 16, cz * 16, 0, 16, 16, 1, d0, d0, 1.0D);
        this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, cx * 16, 109, cz * 16, 16, 1, 16, d0, 1.0D, d0);
        this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen.generateNoiseOctaves(this.netherrackExclusivityNoise, cx * 16, cz * 16, 0, 16, 16, 1, d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                boolean flag = this.slowsandNoise[k + l * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
                boolean flag1 = this.gravelNoise[k + l * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
                int i1 = (int) (this.netherrackExclusivityNoise[k + l * 16] / 3.0D + 3.0D + this.hellRNG.nextDouble() * 0.25D);
                int j1 = -1;

                BiomeGenBase biomegenbase = biomes[l + k * 16];
                Block block = biomegenbase.topBlock;
                Block block1 = biomegenbase.fillerBlock;

                for (int k1 = 255; k1 >= 0; --k1) {
                    int l1 = (l * 16 + k) * 256 + k1;

                    if (k1 < 255 - this.hellRNG.nextInt(5) && k1 > this.hellRNG.nextInt(5)) {
                        Block block2 = blocks[l1];

                        if (block2 != null && block2.getMaterial() != Material.air) {
                            if (block2 == Blocks.netherrack) {
                                if (j1 == -1) {
                                    if (i1 <= 0) {
                                        block = null;
                                        block1 = Blocks.netherrack;
                                    } else if (k1 >= b0 - 4 && k1 <= b0 + 1) {
                                        block = Blocks.netherrack;
                                        block1 = Blocks.netherrack;

                                        if (flag1) {
                                            block = Blocks.gravel;
                                            block1 = Blocks.netherrack;
                                        }

                                        if (flag) {
                                            block = Blocks.soul_sand;
                                            block1 = Blocks.soul_sand;
                                        }
                                    }

                                    if (k1 < b0 && (block == null || block.getMaterial() == Material.air)) {
                                        block = Blocks.lava;
                                    }

                                    j1 = i1;

                                    if (k1 >= b0 - 1) {
                                        blocks[l1] = block;
                                    } else {
                                        blocks[l1] = block1;
                                    }
                                } else if (j1 > 0) {
                                    --j1;
                                    blocks[l1] = block1;
                                }
                            }
                        } else {
                            j1 = -1;
                        }
                    } else {
                        blocks[l1] = Blocks.bedrock;
                    }
                }
            }
        }
    }

    public void populate(IChunkProvider provider, int cx, int cz) {
        BlockFalling.fallInstantly = true;

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(provider, worldObj, hellRNG, cx, cz, false));

        int k = cx * 16;
        int l = cz * 16;
        BiomeGenBase var6 = worldObj.getBiomeGenForCoords(k + 16, l + 16);
        this.hellRNG.setSeed(this.worldObj.getSeed());
        long rand1 = this.hellRNG.nextLong() / 2L * 2L + 1L;
        long rand2 = this.hellRNG.nextLong() / 2L * 2L + 1L;
        this.hellRNG.setSeed((long) cx * rand1 + (long) cz * rand2 ^ this.worldObj.getSeed());

        genNetherBridge.generateStructuresInChunk(worldObj, hellRNG, cx, cz);
        int i1;
        int j1;
        int k1;
        int l1;

        boolean doGen = TerrainGen.populate(provider, worldObj, hellRNG, cx, cz, false, NETHER_LAVA);
        for (i1 = 0; doGen && i1 < 8; ++i1) {
            j1 = k + hellRNG.nextInt(16) + 8;
            k1 = hellRNG.nextInt(120) + 4;
            l1 = l + hellRNG.nextInt(16) + 8;
            (new WorldGenHellLava(Blocks.flowing_lava, false)).generate(worldObj, hellRNG, j1, k1, l1);
        }

        i1 = hellRNG.nextInt(hellRNG.nextInt(10) + 1) + 1;
        int i2;

        doGen = TerrainGen.populate(provider, worldObj, hellRNG, cx, cz, false, FIRE);
        for (j1 = 0; doGen && j1 < i1; ++j1) {
            k1 = k + hellRNG.nextInt(16) + 8;
            l1 = hellRNG.nextInt(120) + 4;
            i2 = l + hellRNG.nextInt(16) + 8;
            (new WorldGenFire()).generate(worldObj, hellRNG, k1, l1, i2);
        }

        i1 = hellRNG.nextInt(hellRNG.nextInt(10) + 1);

        doGen = TerrainGen.populate(provider, worldObj, hellRNG, cx, cz, false, GLOWSTONE);
        for (j1 = 0; doGen && j1 < i1; ++j1) {
            k1 = k + hellRNG.nextInt(16) + 8;
            l1 = hellRNG.nextInt(120) + 4;
            i2 = l + hellRNG.nextInt(16) + 8;
            (new WorldGenGlowStone1()).generate(worldObj, hellRNG, k1, l1, i2);
        }

        for (j1 = 0; doGen && j1 < 10; ++j1) {
            k1 = k + hellRNG.nextInt(16) + 8;
            l1 = hellRNG.nextInt(128);
            i2 = l + hellRNG.nextInt(16) + 8;
            (new WorldGenGlowStone2()).generate(worldObj, hellRNG, k1, l1, i2);
        }

        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, hellRNG, k, l));

        doGen = TerrainGen.decorate(worldObj, hellRNG, k, l, SHROOM);
        if (doGen && hellRNG.nextBoolean()) {
            j1 = k + hellRNG.nextInt(16) + 8;
            k1 = hellRNG.nextInt(128);
            l1 = l + hellRNG.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.brown_mushroom)).generate(worldObj, hellRNG, j1, k1, l1);
        }

        if (doGen && hellRNG.nextBoolean()) {
            j1 = k + hellRNG.nextInt(16) + 8;
            k1 = hellRNG.nextInt(128);
            l1 = l + hellRNG.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.red_mushroom)).generate(worldObj, hellRNG, j1, k1, l1);
        }

        WorldGenMinable worldgenminable = new WorldGenMinable(Blocks.quartz_ore, 13, Blocks.netherrack);
        int j2;

        doGen = TerrainGen.generateOre(worldObj, hellRNG, worldgenminable, k, l, QUARTZ);
        for (k1 = 0; k1 < 16 && doGen; ++k1) {
            l1 = k + hellRNG.nextInt(16);
            i2 = hellRNG.nextInt(108) + 10;
            j2 = l + hellRNG.nextInt(16);
            worldgenminable.generate(worldObj, hellRNG, l1, i2, j2);
        }

        for (k1 = 0; k1 < 16; ++k1) {
            l1 = k + hellRNG.nextInt(16);
            i2 = hellRNG.nextInt(108) + 10;
            j2 = l + hellRNG.nextInt(16);
            (new WorldGenHellLava(Blocks.flowing_lava, true)).generate(worldObj, hellRNG, l1, i2, j2);
        }

        var6.decorate(worldObj, hellRNG, k, l);

        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(worldObj, hellRNG, k, l));
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(provider, worldObj, hellRNG, cx, cz, false));

        BlockFalling.fallInstantly = false;
    }
}
