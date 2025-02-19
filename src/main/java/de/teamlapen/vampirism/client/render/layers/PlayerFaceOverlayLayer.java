package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Renders an overlay over the entities face
 */
@OnlyIn(Dist.CLIENT)
public class PlayerFaceOverlayLayer<T extends MobEntity & IPlayerOverlay, M extends BipedModel<T>> extends LayerRenderer<T, M> {


    public PlayerFaceOverlayLayer(BipedRenderer<T, M> renderBiped) {
        super(renderBiped);
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation loc = entity.getOverlayPlayerProperties().map(Pair::getLeft).orElseGet(DefaultPlayerSkin::getDefaultSkin);
        IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(loc));
        this.getParentModel().head.visible = true;
        this.getParentModel().hat.visible = true;
        this.getParentModel().head.render(stack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        this.getParentModel().hat.render(stack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        this.getParentModel().head.visible = false;
        this.getParentModel().hat.visible = false;

    }


}
