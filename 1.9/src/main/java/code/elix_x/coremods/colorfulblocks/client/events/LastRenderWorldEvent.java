package code.elix_x.coremods.colorfulblocks.client.events;

import org.lwjgl.opengl.GL11;

import code.elix_x.coremods.colorfulblocks.color.tool.IColoringTool;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
			if(color != null){
				for(BlockPos block : ((IColoringTool) itemstack.getItem()).getTargettedBlocks(player, itemstack)){
					if(block.getBlock(player.worldObj) != Blocks.AIR) renderTint(block, color);
				}
			}
		}
	}

	private void renderTint(BlockPos block, RGBA color){
		GL11.glPushMatrix();
		GL11.glTranslated(block.x - TileEntityRendererDispatcher.staticPlayerX, block.y - TileEntityRendererDispatcher.staticPlayerY, block.z -TileEntityRendererDispatcher.staticPlayerZ);
		GL11.glEnable(GL11.GL_BLEND);

		Tessellator tess = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tess.getBuffer();
		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		//bottom
		vertexBuffer.pos(-0.001, -0.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, -0.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, -0.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(-0.001, -0.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();

		//front
		vertexBuffer.pos(-0.001, -0.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(-0.001, 1.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, 1.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, -0.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();

		//left
		vertexBuffer.pos(-0.001, -0.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(-0.001, -0.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(-0.001, 1.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(-0.001, 1.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();

		//back
		vertexBuffer.pos(-0.001, -0.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, -0.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, 1.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(-0.001, 1.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();

		//right
		vertexBuffer.pos(1.001, -0.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, 1.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, 1.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, -0.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();

		//top
		vertexBuffer.pos(-0.001, 1.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(-0.001, 1.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, 1.001, 1.001).color(color.r, color.g, color.b, 128).endVertex();
		vertexBuffer.pos(1.001, 1.001, -0.001).color(color.r, color.g, color.b, 128).endVertex();

		tess.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
