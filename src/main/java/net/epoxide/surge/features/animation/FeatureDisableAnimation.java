package net.epoxide.surge.features.animation;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.features.Feature;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Allows for animations to be disabled. This will usually improve performance, especially when
 * in areas with lots of animation like an ocean or the nether.
 */
@SideOnly(Side.CLIENT)
public class FeatureDisableAnimation extends Feature {
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        
        final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
        this.transformLoadSpriteFrames(ASMUtils.getMethodFromClass(clazz, "loadSpriteFrames", "(Lnet/minecraft/client/resources/IResource;I)V"));
        return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
    
    private void transformLoadSpriteFrames (MethodNode method) {
        
        final InsnList needle = new InsnList();
        needle.add(new InsnNode(Opcodes.POP));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 4));
        
        final AbstractInsnNode pointer = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);
        method.instructions.remove(pointer.getNext());
        final InsnList newInstr = new InsnList();
        
        final LabelNode label6 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFNULL, label6));
        newInstr.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/epoxide/surge/features/FeatureManager", "featureDisableAnimation", "Lnet/epoxide/surge/features/Feature;"));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/epoxide/surge/features/Feature", "isEnabled", "()Z", false));
        
        needle.clear();
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/texture/TextureAtlasSprite", "framesTextureData", "Ljava/util/List;"));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 5));
        needle.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", false));
        needle.add(new InsnNode(Opcodes.POP));
        needle.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode()));
        needle.add(new LabelNode());
        
        final AbstractInsnNode pointer2 = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, (LabelNode) pointer2));
        newInstr.add(label6);
        
        method.instructions.insert(pointer, newInstr);
    }
    
    @Override
    public boolean isTransformer () {
        
        return true;
    }
    
    @Override
    public boolean shouldTransform (String name) {
        
        return name.equals("net.minecraft.client.renderer.texture.TextureAtlasSprite");
    }
    
    @Override
    public boolean enabledByDefault () {
        
        return false;
    }
}