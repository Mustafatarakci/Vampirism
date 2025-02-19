package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.tileentity.AltarInfusionTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders the beams for the altar of infusion
 */
@OnlyIn(Dist.CLIENT)
public class AltarInfusionTESR extends VampirismTESR<AltarInfusionTileEntity> {


    private final ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation(REFERENCE.MODID, "textures/entity/infusion_beam.png");
    private final ResourceLocation beaconBeamTexture = new ResourceLocation("textures/entity/beacon_beam.png");

    public AltarInfusionTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(AltarInfusionTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLight, int combinedOverlay) {
        // Render the beams if the ritual is running
        AltarInfusionTileEntity.PHASE phase = te.getCurrentPhase();
        if (phase == AltarInfusionTileEntity.PHASE.BEAM1 || phase == AltarInfusionTileEntity.PHASE.BEAM2) {
            // Calculate center coordinates
            float cX = te.getBlockPos().getX() + 0.5f;
            float cY = te.getBlockPos().getY() + 3f;
            float cZ = te.getBlockPos().getZ() + 0.5f;
            matrixStack.pushPose();
            matrixStack.translate(0.5, 3, 0.5);
            BlockPos[] tips = te.getTips();
            for (BlockPos tip : tips) {
                this.renderBeam(matrixStack, iRenderTypeBuffer, -(te.getRunningTick() + partialTicks), tip.getX() + 0.5f - cX, tip.getY() + 0.5f - cY, tip.getZ() + 0.5f - cZ, combinedLight, true);
            }

            if (phase == AltarInfusionTileEntity.PHASE.BEAM2) {
                PlayerEntity p = te.getPlayer();
                if (p != null) {
                    this.renderBeam(matrixStack, iRenderTypeBuffer, -(te.getRunningTick() + partialTicks), (float) p.getX() - cX, (float) p.getY() + 1.2f - cY, (float) p.getZ() - cZ, combinedLight, false);
                }
            }
            matrixStack.popPose();


        }
    }

    /**
     * Renders a beam in the world, similar to the dragon healing beam
     */
    private void renderBeam(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, float dx, float dy, float dz, int packedLight, boolean beacon) {

        float distFlat = MathHelper.sqrt(dx * dx + dz * dz);
        float dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.YP.rotation((float) (-Math.atan2(dz, dx)) - ((float) Math.PI / 2F)));
        matrixStack.mulPose(Vector3f.XP.rotation((float) (-Math.atan2(distFlat, dy)) - ((float) Math.PI / 2F)));
        IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.entitySmoothCutout(beacon ? beaconBeamTexture : enderDragonCrystalBeamTextures));
        float f2 = partialTicks * 0.05f;
        float f3 = dist / 32.0F + partialTicks * 0.05f;
        float f4 = 0.0F;
        float f5 = 0.2F;
        float f6 = 0.0F;
        MatrixStack.Entry matrixstack$entry = matrixStack.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();

        for (int j = 1; j <= 8; ++j) {
            float f7 = MathHelper.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
            float f8 = MathHelper.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.2F;
            float f9 = (float) j / 8.0F;
            ivertexbuilder.vertex(matrix4f, f4, f5, 0.0F).color(75, 0, 0, 255).uv(f6, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.vertex(matrix4f, f4 * 0.5f, f5 * 0.5f, dist).color(255, 0, 0, 255).uv(f6, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.vertex(matrix4f, f7 * 0.5f, f8 * 0.5f, dist).color(255, 0, 0, 255).uv(f9, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.vertex(matrix4f, f7, f8, 0.0F).color(75, 0, 0, 255).uv(f9, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }

        matrixStack.popPose();

    }


}
