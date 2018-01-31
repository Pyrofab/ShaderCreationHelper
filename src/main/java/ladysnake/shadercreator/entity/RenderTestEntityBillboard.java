package ladysnake.shadercreator.entity;

import ladysnake.shadercreator.ShaderCreator;
import ladysnake.shadercreator.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderTestEntityBillboard extends Render<TestEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ShaderCreator.MOD_ID, "textures/entity/noise.png");

    public RenderTestEntityBillboard(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(@Nonnull TestEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        this.bindEntityTexture(entity);
        RenderHelper.enableStandardItemLighting();
        int j = entity.getBrightnessForRender();
        int k = j % 65536;
        int l = j / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(0.0F, 0.1F, 0.0F);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(1F, 1F, 1F);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        ShaderUtil.useShader(ShaderUtil.test);
        EntityPlayer player = Minecraft.getMinecraft().player;
        ShaderUtil.setUniform("playerPosition", new float[]{(float) player.posX, (float) player.posY + player.eyeHeight, (float) player.posZ});
        ShaderUtil.setUniform("center", new float[]{(float) entity.posX, (float) entity.posY + entity.height / 2, (float) entity.posZ});
        ShaderUtil.setUniform("radius", 3);
        ShaderUtil.setUniform("gasColor", new float[]{1, 1, 1, 1});
        ShaderUtil.setUniform("iTime", (int) System.currentTimeMillis());
        ShaderUtil.setUniform("gbufferProjection", ShaderUtil.getProjectionMatrix());
        ShaderUtil.setUniform("gbufferProjectionInverse", ShaderUtil.getProjectionMatrixInverse());
        ShaderUtil.setUniform("gbufferModelView", ShaderUtil.getModelViewMatrix());
        ShaderUtil.setUniform("gbufferModelViewInverse", ShaderUtil.getModelViewMatrixInverse());
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-2D, 0D, 0.0D).tex(0D, 0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(2D, 0D, 0.0D).tex(0D, 1D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(2D, 4D, 0.0D).tex(1D, 1D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(-2D, 4D, 0.0D).tex(1D, 0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        ShaderUtil.revert();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull TestEntity entity) {
        return TEXTURE;
    }
}
