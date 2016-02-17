package code.elix_x.coremods.colourfulblocks.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
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
import net.minecraft.world.IBlockAccess;

public class ColourfulBlocksTransformer implements IClassTransformer {

	public static final Logger logger = LogManager.getLogger("CoBl Core");

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes){
		if(name.equals("net.minecraft.block.Block")){
			logger.info("##################################################");
			logger.info("Patching Block");
			byte[] b = patchBlock(name, bytes);
			logger.info("Patching Block Completed");
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
		if(name.equals("net.minecraft.client.renderer.RenderBlocks")){
			logger.info("##################################################");
			logger.info("Patching RenderBlocks");
			byte[] b = patchRenderBlocks(name, bytes);
			logger.info("Patching RenderBlocks Completed");
			logger.info("##################################################");
			return b;
		}
		return bytes;
	}

	private byte[] patchRenderBlocks(String name, byte[] bytes){		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		for(MethodNode method : classNode.methods){
			if((method.name.equals("renderBlockByRenderType") || method.name.equals("func_147805_b")) && method.desc.equals("(Lnet/minecraft/block/Block;III)Z")){
				try{
					logger.info("**************************************************");
					logger.info("Patching renderBlockByRenderType");


					AbstractInsnNode targetNode = null;

					for(AbstractInsnNode node : method.instructions.toArray()){
						if(node instanceof MethodInsnNode){
							MethodInsnNode mnode = (MethodInsnNode) node;
							if(mnode.owner.equals("net.minecraft.src.FMLRenderAccessLibrary".replace(".", "/"))){
								targetNode = node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious();
								break;
							}
						}
					}

					InsnList list = new InsnList();
					list.add(new LabelNode());
					list.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list.add(new VarInsnNode(Opcodes.ILOAD, 2));
					list.add(new VarInsnNode(Opcodes.ILOAD, 3));
					list.add(new VarInsnNode(Opcodes.ILOAD, 4));
					list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ColourfulBlocksHooks.class.getName().replace(".", "/"), "recolorBlock", "(L" + "net.minecraft.renderer.RenderBlock".replace(".", "/") + ";III)V", false));
					list.add(new LabelNode());

					method.instructions.insert(targetNode.getPrevious().getPrevious().getPrevious(), list);


					logger.info("Patching renderBlockByRenderType Completed");
					logger.info("**************************************************");
				}catch(Exception e){
					logger.info("Patching renderBlockByRenderType Failed With Exception:");
					e.printStackTrace();
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
			if((method.name.equals("renderTileEntity") || method.name.equals("func_147544_a")) && method.desc.equals("(Lnet/minecraft/tileentity/TileEntity;F)V")){
				try{
					logger.info("**************************************************");
					logger.info("Patching renderTileEntity");


					AbstractInsnNode targetNode = null;

					for(AbstractInsnNode node : method.instructions.toArray()){
						if(node instanceof MethodInsnNode){
							MethodInsnNode mnode = (MethodInsnNode) node;
							if(mnode.owner.equals(GL11.class.getName().replace(".", "/"))){
								targetNode = node;
								break;
							}
						}
					}

					InsnList list = new InsnList();
					list.add(new LabelNode());
					list.add(new VarInsnNode(Opcodes.ALOAD, 1));
					list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ColourfulBlocksHooks.class.getName().replace(".", "/"), "recolorTileEntity", "(L" + "net.minecraft.tileentity.TileEntity".replace(".", "/") + ";)V", false));
					list.add(new LabelNode());

					method.instructions.insert(targetNode, list);


					logger.info("Patching renderTileEntity Completed");
					logger.info("**************************************************");
				}catch(Exception e){
					logger.info("Patching renderTileEntity Failed With Exception:");
					e.printStackTrace();
					logger.info("**************************************************");
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private byte[] patchBlock(String name, byte[] bytes){		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		for(MethodNode method : classNode.methods){
			if((method.name.equals("colorMultiplier") || method.name.equals("func_149720_d")) && method.desc.equals("(Lnet/minecraft/world/IBlockAccess;III)I")){
				try{
					logger.info("**************************************************");
					logger.info("Patching colorMultiplier");


					InsnList list = new InsnList();
					list.add(new LabelNode());
					list.add(new VarInsnNode(Opcodes.ALOAD, 1));
					list.add(new VarInsnNode(Opcodes.ILOAD, 2));
					list.add(new VarInsnNode(Opcodes.ILOAD, 3));
					list.add(new VarInsnNode(Opcodes.ILOAD, 4));
					list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ColourfulBlocksHooks.class.getName().replace(".", "/"), "getBlockColor", "(L" + IBlockAccess.class.getName().replace(".", "/") + ";III)I", false));
					list.add(new InsnNode(Opcodes.IRETURN));
					list.add(new LabelNode());

					method.instructions.insert(list);


					logger.info("Patching colorMultiplier Completed");
					logger.info("**************************************************");
				}catch(Exception e){
					logger.info("Patching colorMultiplier Failed With Exception:");
					e.printStackTrace();
					logger.info("**************************************************");
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
