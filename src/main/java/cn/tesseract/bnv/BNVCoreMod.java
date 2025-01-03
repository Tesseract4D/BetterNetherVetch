package cn.tesseract.bnv;

import cn.tesseract.mycelium.asm.minecraft.HookLoader;

public class BNVCoreMod extends HookLoader {

    @Override
    protected void registerHooks() {
        registerHookContainer(BNVHook.class.getName());
    }
}
