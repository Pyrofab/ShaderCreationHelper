package ladysnake.shadercreator.entity;

import ladysnake.shadercreator.ShaderCreator;
import ladysnake.shadercreator.ShaderUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderTestEntity extends Render<TestEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ShaderCreator.MOD_ID, "textures/entity/test.png");

    public RenderTestEntity(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(@Nonnull TestEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translate((float) x, (float) y + 0.05f, (float) z);

        GlStateManager.enableAlpha();

        this.bindEntityTexture(entity);

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        ShaderUtil.useShader(ShaderUtil.test);

        ShaderUtil.setUniform("iTime", (int) System.currentTimeMillis());
        ShaderUtil.setUniform("lightmap", 1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        float minU = 0;
        float minV = 0;
        float maxU = 1;
        float maxV = 1;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-2D, 0.0D, -2D).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(2D, 0.0D, -2D).tex(minU, maxV).endVertex();
        bufferbuilder.pos(2D, 0.0D, 2D).tex(minU, minV).endVertex();
        bufferbuilder.pos(-2, 0.0D, 2D).tex(maxU, minV).endVertex();

        bufferbuilder.pos(-2D, 0.0D, 2D).tex(maxU, minV).endVertex();
        bufferbuilder.pos(2D, 0.0D, 2D).tex(minU, minV).endVertex();
        bufferbuilder.pos(2D, 0.0D, -2D).tex(minU, maxV).endVertex();
        bufferbuilder.pos(-2D, 0.0D, -2D).tex(maxU, maxV).endVertex();
        tessellator.draw();

        ShaderUtil.revert();

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull TestEntity entity) {
        return TEXTURE;
    }
}
