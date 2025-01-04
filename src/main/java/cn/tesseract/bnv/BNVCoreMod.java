package cn.tesseract.bnv;

import cn.tesseract.mycelium.asm.minecraft.HookLibPlugin;
import cn.tesseract.mycelium.asm.minecraft.HookLoader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class BNVCoreMod extends HookLoader {

    @Override
    protected void registerHooks() {
        registerHookContainer(NetherHook.class.getName());
        registerHookContainer(NetherliciousHook.class.getName());
        registerHookContainer(BOPHook.class.getName());
        registerNodeTransformer("biomesoplenty.common.biome.decoration.BOPNetherBiomeDecorator", node -> {
            for (MethodNode method : node.methods) {
                if (HookLibPlugin.getMethodMcpName(method.name).equals("genDecorations")) {
                    Iterator<AbstractInsnNode> it = method.instructions.iterator();
                    while (it.hasNext()) {
                        if (it.next() instanceof IntInsnNode insn)
                            if (insn.operand == 128)
                                insn.operand = 256;
                    }
                }
            }
        });
        registerNodeTransformer("mods.natura.worldgen.BaseTreeWorldgen", node -> {
            for (MethodNode method : node.methods) {
                if (HookLibPlugin.getMethodMcpName(method.name).equals("generate")) {
                    Iterator<AbstractInsnNode> it = method.instructions.iterator();
                    while (it.hasNext()) {
                        if (it.next() instanceof IntInsnNode insn)
                            if (insn.operand == 108)
                                insn.operand = 255;
                    }
                }
            }
        });
    }
}
