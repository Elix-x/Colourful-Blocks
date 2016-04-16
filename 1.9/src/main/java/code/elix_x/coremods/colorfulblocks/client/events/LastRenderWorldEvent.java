package code.elix_x.coremods.colorfulblocks.client.events;

import org.lwjgl.opengl.GL11;

import code.elix_x.coremods.colorfulblocks.color.tool.IColoringTool;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LastRenderWorldEvent {

	@SubscribeEvent
	public void render(RenderWorldLastEvent event){
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null && player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof IColoringTool){
			ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
			RGBA color = ((IColoringTool) itemstack.getItem()).getCurrentColor(itemstack);
			for(BlockPos block : ((IColoringTool) itemstack.getItem()).getBlocksAboutToBeColored(player, itemstack)){
				if(block.getBlock(player.worldObj) != Blocks.air) renderTint(block, color);
			}
		}
	}

	private void renderTint(BlockPos block, RGBA color){
		GL11.glPushMatrix();
		GL11.glTranslated(block.x - TileEntityRendererDispatcher.staticPlayerX, block.y - TileEntityRendererDispatcher.staticPlayerY, block.z -TileEntityRendererDispatcher.staticPlayerZ);
		GL11.glEnable(GL11.GL_BLEND);

		VertexBuffer tess = Tessellator.getInstance().getBuffer();
		//		tess.begin(GL11.GL_QUADS, new VertexFormat().addElement(new ));
		//		tess.setColorRGBA(color.r, color.g, color.b, 128);

		//bottom
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, -0.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, -0.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, -0.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, -0.001, 1.001); tess.endVertex();

		//front
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, -0.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, 1.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, 1.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, -0.001, -0.001); tess.endVertex();

		//left
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, -0.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, -0.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, 1.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, 1.001, -0.001); tess.endVertex();

		//back
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, -0.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, -0.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, 1.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, 1.001, 1.001); tess.endVertex();

		//right
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, -0.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, 1.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, 1.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, -0.001, 1.001); tess.endVertex();

		//top
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, 1.001, -0.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(-0.001, 1.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, 1.001, 1.001); tess.endVertex();
		tess.color(color.r, color.g, color.b, 128).putPosition(1.001, 1.001, -0.001); tess.endVertex();

		tess.finishDrawing();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
