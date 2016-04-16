package code.elix_x.coremods.colourfulblocks.client.events;

import org.lwjgl.opengl.GL11;

import code.elix_x.coremods.colourfulblocks.color.tool.IColoringTool;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.BlockPos;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class LastRenderWorldEvent {

	@SubscribeEvent
	public void render(RenderWorldLastEvent event){
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IColoringTool){
			ItemStack itemstack = player.getCurrentEquippedItem();
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

		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorRGBA(color.r, color.g, color.b, 128);

		//bottom
		tess.addVertex(-0.001, -0.001, -0.001);
		tess.addVertex(1.001, -0.001, -0.001);
		tess.addVertex(1.001, -0.001, 1.001);
		tess.addVertex(-0.001, -0.001, 1.001);

		//front
		tess.addVertex(-0.001, -0.001, -0.001);
		tess.addVertex(-0.001, 1.001, -0.001);
		tess.addVertex(1.001, 1.001, -0.001);
		tess.addVertex(1.001, -0.001, -0.001);

		//left
		tess.addVertex(-0.001, -0.001, -0.001);
		tess.addVertex(-0.001, -0.001, 1.001);
		tess.addVertex(-0.001, 1.001, 1.001);
		tess.addVertex(-0.001, 1.001, -0.001);

		//back
		tess.addVertex(-0.001, -0.001, 1.001);
		tess.addVertex(1.001, -0.001, 1.001);
		tess.addVertex(1.001, 1.001, 1.001);
		tess.addVertex(-0.001, 1.001, 1.001);

		//right
		tess.addVertex(1.001, -0.001, -0.001);
		tess.addVertex(1.001, 1.001, -0.001);
		tess.addVertex(1.001, 1.001, 1.001);
		tess.addVertex(1.001, -0.001, 1.001);

		//top
		tess.addVertex(-0.001, 1.001, -0.001);
		tess.addVertex(-0.001, 1.001, 1.001);
		tess.addVertex(1.001, 1.001, 1.001);
		tess.addVertex(1.001, 1.001, -0.001);

		tess.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
