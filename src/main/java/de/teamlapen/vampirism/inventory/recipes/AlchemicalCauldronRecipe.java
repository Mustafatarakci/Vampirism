package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlchemicalCauldronRecipe extends AbstractCookingRecipe {
    private static final ISkill[] EMPTY_SKILLS = {};
    private final Either<Ingredient, FluidStack> fluid;
    @Nonnull
    private final ISkill[] skills;
    private final int reqLevel;

    public AlchemicalCauldronRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, Either<Ingredient, FluidStack> fluidIn, ItemStack resultIn, @Nonnull ISkill[] skillsIn, int reqLevelIn, int cookTimeIn, float exp) {
        super(ModRecipes.ALCHEMICAL_CAULDRON_TYPE, idIn, groupIn, ingredientIn, resultIn, exp, cookTimeIn);
        this.fluid = fluidIn;
        this.skills = skillsIn;
        this.reqLevel = reqLevelIn;
    }

    public boolean canBeCooked(int level, ISkillHandler<IHunterPlayer> skillHandler) {
        if (level < reqLevel) return false;
        for (ISkill s : skills) {
            if (!skillHandler.isSkillEnabled(s)) return false;
        }
        return true;
    }

    public Either<Ingredient, FluidStack> getFluid() {
        return fluid;
    }

    public int getRequiredLevel() {
        return reqLevel;
    }

    @Nonnull
    public ISkill[] getRequiredSkills() {
        return skills;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.ALCHEMICAL_CAULDRON.get();
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        boolean match = this.ingredient.test(inv.getItem(1));
        AtomicBoolean fluidMatch = new AtomicBoolean(true);
        fluid.ifLeft((ingredient1 -> fluidMatch.set(ingredient1.test(inv.getItem(0)))));
        fluid.ifRight((ingredient1 -> {
            fluidMatch.set(false);
            Optional<FluidStack> stack = FluidUtil.getFluidContained(inv.getItem(0));
            stack.ifPresent((handlerItem) -> fluidMatch.set(ingredient1.isFluidEqual(handlerItem) && ingredient1.getAmount() <= handlerItem.getAmount()));
        }));
        return match && fluidMatch.get();
    }

    @Override
    public String toString() {
        return "AlchemicalCauldronRecipe{" +
                "cookingTime=" + cookingTime +
                ", skills=" + Arrays.toString(skills) +
                ", output=" + result +
                ", ingredient=" + ingredient +
                ", reqLevel=" + reqLevel +
                ", experience=" + experience +
                ", fluid=" + fluid +
                '}';
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlchemicalCauldronRecipe> {
        @Override
        public AlchemicalCauldronRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getAsString(json, "group", "");
            Ingredient ingredients = Ingredient.fromJson(JSONUtils.isArrayNode(json, "ingredient") ? JSONUtils.getAsJsonArray(json, "ingredient") : JSONUtils.getAsJsonObject(json, "ingredient"));
            int level = JSONUtils.getAsInt(json, "level", 1);
            ISkill[] skills = VampirismRecipeHelper.deserializeSkills(JSONUtils.getAsJsonArray(json, "skill", null));
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "result"), true);
            Either<Ingredient, FluidStack> fluid = VampirismRecipeHelper.getFluidOrItem(json);
            int cookTime = JSONUtils.getAsInt(json, "cookTime", 200);
            float exp = JSONUtils.getAsFloat(json, "experience", 0.2F);
            return new AlchemicalCauldronRecipe(recipeId, group, ingredients, fluid, result, skills, level, cookTime, exp);
        }

        @Override
        public AlchemicalCauldronRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readUtf(32767);
            ItemStack result = buffer.readItem();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Either<Ingredient, FluidStack> fluid;
            if (buffer.readBoolean()) {
                fluid = Either.left(Ingredient.fromNetwork(buffer));
            } else {
                fluid = Either.right(FluidStack.readFromPacket(buffer));
            }
            float exp = buffer.readFloat();
            int cookingtime = buffer.readVarInt();
            int level = buffer.readVarInt();
            ISkill[] skills = new ISkill[buffer.readVarInt()];
            for (int i = 0; i < skills.length; i++) {
                skills[i] = ModRegistries.SKILLS.getValue(new ResourceLocation(buffer.readUtf(32767)));
            }
            return new AlchemicalCauldronRecipe(recipeId, group, ingredient, fluid, result, skills, level, cookingtime, exp);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, AlchemicalCauldronRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeItem(recipe.result);
            recipe.ingredient.toNetwork(buffer);
            if (recipe.fluid.left().isPresent()) {
                buffer.writeBoolean(true);
                recipe.fluid.left().get().toNetwork(buffer);
            } else {
                assert recipe.fluid.right().isPresent();
                buffer.writeBoolean(false);
                recipe.fluid.right().get().writeToPacket(buffer);
            }
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookingTime);
            buffer.writeVarInt(recipe.reqLevel);
            buffer.writeVarInt(recipe.skills.length);
            for (ISkill skill : recipe.skills) {
                buffer.writeUtf(skill.getRegistryName().toString());
            }
        }

    }
}
