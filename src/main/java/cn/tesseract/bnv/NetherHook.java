package cn.tesseract.bnv;

import DelirusCrux.Netherlicious.Dimension.NetherChunkProvider;
import cn.tesseract.bnv.world.generator.BNBWorldGenerator;
import cn.tesseract.mycelium.asm.Hook;
import cn.tesseract.mycelium.asm.ReturnCondition;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderHell;

import java.lang.reflect.Field;

public class NetherHook {
    @Hook(targetMethod = "<init>", injectOnExit = true)
    public static void init(ChunkProviderHell c, World world, long seed) {
        BNBWorldGenerator.updateData(world.saveHandler, seed);
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static Chunk provideChunk(ChunkProviderHell c, int cx, int cz) {
        return BNBWorldGenerator.makeChunk(c.worldObj, cx, cz);
    }

    @Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
    public static int getActualHeight(WorldProviderHell c) {
        return 256;
    }

    @Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
    public static void generateLightBrightnessTable(WorldProviderHell c) {
        for (byte i = 0; i < 16; i++) {
            float delta = i / 15F;
            c.lightBrightnessTable[i] = BNVMath.lerp(delta, 0.3F, 1.0F);
        }
    }
}
