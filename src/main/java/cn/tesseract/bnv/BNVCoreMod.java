package cn.tesseract.bnv;

import cn.tesseract.mycelium.asm.minecraft.HookLoader;

public class BNVCoreMod extends HookLoader {

    @Override
    protected void registerHooks() {
        registerHookContainer(NetherHook.class.getName());
        registerHookContainer(NetherliciousHook.class.getName());
    }
}
