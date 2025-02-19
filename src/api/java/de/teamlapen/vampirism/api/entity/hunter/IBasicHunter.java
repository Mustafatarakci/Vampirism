package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Interface for basic hunter mob
 * Do not implement
 */
public interface IBasicHunter extends IHunterMob, IAdjustableLevel, IVillageCaptureEntity {
    int TYPES = 126;

    /**
     * @return A randomly selected but permanent integer between 0 and {@link IBasicHunter#TYPES} or -1 if not selected yet.
     */
    int getEntityTextureType();

    boolean isLookingForHome();

    void makeNormalHunter();

    void makeVillageHunter(AxisAlignedBB box);

}
