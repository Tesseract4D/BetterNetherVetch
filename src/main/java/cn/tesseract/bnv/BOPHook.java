package cn.tesseract.bnv;

import biomesoplenty.common.world.WorldProviderBOPHell;
import cn.tesseract.bnv.world.BOPNetherChunkProvider;
import cn.tesseract.mycelium.asm.Hook;
import cn.tesseract.mycelium.asm.ReturnCondition;
import net.minecraft.world.chunk.IChunkProvider;

public class BOPHook {
    @Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
    public static void generateLightBrightnessTable(WorldProviderBOPHell c) {
        for (byte i = 0; i < 16; i++) {
            float delta = i / 15F;
            c.lightBrightnessTable[i] = BNVMath.lerp(delta, 0.3F, 1.0F);
        }
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static IChunkProvider createChunkGenerator(WorldProviderBOPHell c) {
        return new BOPNetherChunkProvider(c.worldObj, c.worldObj.getSeed());
    }
}
