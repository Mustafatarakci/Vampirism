package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ConvertedVillagerRenderer extends VillagerRenderer {

    private final ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/villager_overlay.png");

    public ConvertedVillagerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, (IReloadableResourceManager) Minecraft.getInstance().getResourceManager());
        this.addLayer(new VampireEntityLayer<>(this, overlay, false));
    }
}
