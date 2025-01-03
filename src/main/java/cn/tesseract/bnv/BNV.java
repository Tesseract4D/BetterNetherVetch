package cn.tesseract.bnv;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "bnv", acceptedMinecraftVersions = "[1.7.10]", version = Tags.VERSION)
public class BNV {
    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        LOGGER = e.getModLog();

    }

    public static String id(String name) {
        return "bnv:" + name;
    }
}
