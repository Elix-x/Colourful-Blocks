package code.elix_x.coremods.colorfulblocks.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ColorfulBlocksTransformer implements IClassTransformer {

	public static final Logger logger = LogManager.getLogger("CoBl Core");

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes){
		if(name.equals("net.minecraft.client.renderer.block.model.BakedQuad")){
			logger.info("##################################################");
			logger.info("Patching BakedQuad");
			byte[] b = patchBakedQuad(name, bytes);
			logger.info("Patching BakedQuad Completed");
			logger.info("##################################################");
			return b;
		}
		if(name.equals("net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher")){
			logger.info("##################################################");
			logger.info("Patching TileEntityRendererDispatcher");
			byte[] b = patchTileEntityRendererDispatcher(name, bytes);
			logger.info("Patching TileEntityRendererDispatcher Completed");
			logger.info("##################################################");
			return b;
		}
		return bytes;
	}

	private byte[] patchBakedQuad(String name, byte[] bytes){
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		for(MethodNode method : classNode.methods){
			if(method.name.equals("hasTintIndex") || method.name.equals("func_178212_b")){
				try {
					logger.info("**************************************************");
					logger.info("Patching hasTintIndex");

					InsnList list = new InsnList();
					list.add(new InsnNode(Opcodes.ICONST_1));
					list.add(new InsnNode(Opcodes.IRETURN));
					method.instructions.insert(list);

					logger.info("Patching hasTintIndex Completed");
					logger.info("**************************************************");
				} catch(Exception e){
					logger.error("Patching hasTintIndex Failed With Exception:", e);
					logger.info("**************************************************");
				}
			}
			if(method.name.equals("getTintIndex") || method.name.equals("func_178211_c")){
				try {
					logger.info("**************************************************");
					logger.info("Patching getTintIndex");

					AbstractInsnNode targetNode = null;
					for(AbstractInsnNode node : method.instructions.toArray()){
						if(node.getOpcode() == Opcodes.IRETURN){
							targetNode = node;
							break;
						}
					}

					method.instructions.insertBefore(targetNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "code.elix_x.coremods.colorfulblocks.core.ColorfulBlocksHooks".replace(".", "/"), "getTintIndex", "(I)I", false));

					logger.info("Patching getTintIndex Completed");
					logger.info("**************************************************");
				} catch(Exception e){
					logger.error("Patching getTintIndex Failed With Exception:", e);
					logger.info("**************************************************");
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private byte[] patchTileEntityRendererDispatcher(String name, byte[] bytes){		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		for(MethodNode method : classNode.methods){
			if((method.name.equals("renderTileEntityAt") || method.name.equals("func_178469_a")) && method.desc.equals("(Lnet/minecraft/tileentity/TileEntity;DDDFI)V")){
				try {
					logger.info("**************************************************");
					logger.info("Patching renderTileEntity");

					AbstractInsnNode targetNode = null;

					for(AbstractInsnNode node : method.instructions.toArray()){
						if(node.getOpcode() == Opcodes.IFNULL){
							targetNode = node;
							break;
						}
					}

					InsnList list = new InsnList();
					list.add(new LabelNode());
					list.add(new VarInsnNode(Opcodes.ALOAD, 1));
					list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "code.elix_x.coremods.colorfulblocks.core.ColorfulBlocksHooks".replace(".", "/"), "recolorTileEntity", "(Lnet/minecraft/tileentity/TileEntity;)V", false));
					list.add(new LabelNode());

					method.instructions.insert(targetNode, list);

					logger.info("Patching renderTileEntity Completed");
					logger.info("**************************************************");
				} catch(Exception e){
					logger.error("Patching renderTileEntity Failed With Exception:", e);
					logger.info("**************************************************");
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
