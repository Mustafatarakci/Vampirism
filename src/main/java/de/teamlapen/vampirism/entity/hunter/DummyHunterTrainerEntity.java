package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.ICaptureIgnore;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class DummyHunterTrainerEntity extends VampirismEntity implements ICaptureIgnore {
    private final int MOVE_TO_RESTRICT_PRIO = 3;

    public DummyHunterTrainerEntity(EntityType<? extends DummyHunterTrainerEntity> type, World world) {
        super(type, world);
        saveHome = true;
        hasArms = true;
        ((GroundPathNavigator) this.getNavigation()).setCanOpenDoors(true);

        this.setDontDropEquipment();
        this.peaceful = true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(distanceToClosestPlayer) && getHome() != null;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public void setHome(AxisAlignedBB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof SpawnEggItem;

        if (!flag && this.isAlive() && !player.isShiftKeyDown()) {
            if (!this.level.isClientSide) {
                if (Helper.isHunter(player)) {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.trainer_disabled_hunter"), Util.NIL_UUID);
                } else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.trainer_disabled"), Util.NIL_UUID);
                }
            }
            return ActionResultType.SUCCESS;
        }


        return super.mobInteract(player, hand);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 13F));
        this.goalSelector.addGoal(9, new LookAtGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }


}
