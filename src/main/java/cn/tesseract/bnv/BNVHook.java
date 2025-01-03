package cn.tesseract.bnv;

import cn.tesseract.bnv.world.generator.BNBWorldGenerator;
import cn.tesseract.mycelium.asm.Hook;
import cn.tesseract.mycelium.asm.ReturnCondition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderHell;

public class BNVHook {
    @Hook(targetMethod = "<init>", injectOnExit = true)
    public static void init(ChunkProviderHell c, World world, long seed) {
        BNBWorldGenerator.updateData(world.saveHandler, seed);
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static Chunk provideChunk(ChunkProviderHell c, int cx, int cz) {
       return BNBWorldGenerator.makeChunk(c.worldObj, cx, cz);
    }
}
