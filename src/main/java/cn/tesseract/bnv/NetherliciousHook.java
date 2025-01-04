package cn.tesseract.bnv;

import DelirusCrux.Netherlicious.Dimension.NetherWorldProvider;
import DelirusCrux.Netherlicious.Utility.Configuration.WorldgenConfiguration;
import cn.tesseract.bnv.world.NetherliciousChunkProvider;
import cn.tesseract.mycelium.asm.Hook;
import cn.tesseract.mycelium.asm.ReturnCondition;
import net.minecraft.world.chunk.IChunkProvider;

import java.io.File;

public class NetherliciousHook {
    @Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
    public static void generateLightBrightnessTable(NetherWorldProvider c) {
        for (byte i = 0; i < 16; i++) {
            float delta = i / 15F;
            c.lightBrightnessTable[i] = BNVMath.lerp(delta, 0.3F, 1.0F);
        }
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static IChunkProvider createChunkGenerator(NetherWorldProvider c) {
        return new NetherliciousChunkProvider(c.worldObj, c.worldObj.getSeed());
    }

    @Hook(injectOnExit = true)
    public static void init(WorldgenConfiguration c, File targ) {
        WorldgenConfiguration.BigNether = true;
        WorldgenConfiguration.FoxfireLavaOcean = true;
    }
}
