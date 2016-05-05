package code.elix_x.coremods.colourfulblocks.client.gui;

import org.lwjgl.opengl.GL11;

import code.elix_x.excore.utils.color.RGBA;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class CustomItemRenderer {

	private static CustomRenderBlocks renderBlocksRi = new CustomRenderBlocks();

	public static void renderItemIntoGUI(FontRenderer p_77015_1_, TextureManager p_77015_2_, ItemStack p_77015_3_, int p_77015_4_, int p_77015_5_, RenderItem renderItem, RGBA rgba)
	{
		renderItemIntoGUI(p_77015_1_, p_77015_2_, p_77015_3_, p_77015_4_, p_77015_5_, renderItem, rgba, false);
	}

	public static void renderItemIntoGUI(FontRenderer p_77015_1_, TextureManager p_77015_2_, ItemStack p_77015_3_, int p_77015_4_, int p_77015_5_, RenderItem renderItem, RGBA rgba, boolean renderEffect)
	{
		int k = p_77015_3_.getItemDamage();
		Object object = p_77015_3_.getIconIndex();
		int l;
		float f;
		float f3;
		float f4;

		if (p_77015_3_.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(p_77015_3_.getItem()).getRenderType()))
		{
			p_77015_2_.bindTexture(TextureMap.locationBlocksTexture);
			Block block = Block.getBlockFromItem(p_77015_3_.getItem());
			GL11.glEnable(GL11.GL_ALPHA_TEST);

			if (block.getRenderBlockPass() != 0)
			{
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			}
			else
			{
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
				GL11.glDisable(GL11.GL_BLEND);
			}

			GL11.glPushMatrix();
			GL11.glTranslatef((float)(p_77015_4_ - 2), (float)(p_77015_5_ + 3), -3.0F + renderItem.zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F);
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			l = p_77015_3_.getItem().getColorFromItemStack(p_77015_3_, 0);
			f3 = (float)(l >> 16 & 255) / 255.0F;
			f4 = (float)(l >> 8 & 255) / 255.0F;
			f = (float)(l & 255) / 255.0F;

			if (renderItem.renderWithColor)
			{
				GL11.glColor4f(rgba.getRF(), rgba.getGF(), rgba.getBF(), rgba.getAF());
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			renderBlocksRi.useInventoryTint = renderItem.renderWithColor;
			renderBlocksRi.renderBlockAsItem(block, k, 1.0F);
			renderBlocksRi.useInventoryTint = true;

			if (block.getRenderBlockPass() == 0)
			{
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			}

			GL11.glPopMatrix();
		}
		else if (p_77015_3_.getItem().requiresMultipleRenderPasses())
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			p_77015_2_.bindTexture(TextureMap.locationItemsTexture);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(0, 0, 0, 0);
			GL11.glColorMask(false, false, false, true);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(-1);
			tessellator.addVertex((double)(p_77015_4_ - 2), (double)(p_77015_5_ + 18), (double)renderItem.zLevel);
			tessellator.addVertex((double)(p_77015_4_ + 18), (double)(p_77015_5_ + 18), (double)renderItem.zLevel);
			tessellator.addVertex((double)(p_77015_4_ + 18), (double)(p_77015_5_ - 2), (double)renderItem.zLevel);
			tessellator.addVertex((double)(p_77015_4_ - 2), (double)(p_77015_5_ - 2), (double)renderItem.zLevel);
			tessellator.draw();
			GL11.glColorMask(true, true, true, true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);

			Item item = p_77015_3_.getItem();
			for (l = 0; l < item.getRenderPasses(k); ++l)
			{
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				p_77015_2_.bindTexture(item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
				IIcon iicon = item.getIcon(p_77015_3_, l);
				int i1 = p_77015_3_.getItem().getColorFromItemStack(p_77015_3_, l);
				f = (float)(i1 >> 16 & 255) / 255.0F;
				float f1 = (float)(i1 >> 8 & 255) / 255.0F;
				float f2 = (float)(i1 & 255) / 255.0F;

				if (renderItem.renderWithColor)
				{
					GL11.glColor4f(f, f1, f2, 1.0F);
					GL11.glColor4f(rgba.getRF(), rgba.getGF(), rgba.getBF(), rgba.getAF());
				}

				GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, ad renderEffect can derp them up.
				GL11.glEnable(GL11.GL_ALPHA_TEST);

				renderItem.renderIcon(p_77015_4_, p_77015_5_, iicon, 16, 16);

				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_LIGHTING);
			}

			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			ResourceLocation resourcelocation = p_77015_2_.getResourceLocation(p_77015_3_.getItemSpriteNumber());
			p_77015_2_.bindTexture(resourcelocation);

			if (object == null)
			{
				object = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
			}

			l = p_77015_3_.getItem().getColorFromItemStack(p_77015_3_, 0);
			f3 = (float)(l >> 16 & 255) / 255.0F;
			f4 = (float)(l >> 8 & 255) / 255.0F;
			f = (float)(l & 255) / 255.0F;

			if (renderItem.renderWithColor)
			{
				GL11.glColor4f(rgba.getRF(), rgba.getGF(), rgba.getBF(), rgba.getAF());
			}

			GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);

			renderItem.renderIcon(p_77015_4_, p_77015_5_, (IIcon)object, 16, 16);

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
	}



}
