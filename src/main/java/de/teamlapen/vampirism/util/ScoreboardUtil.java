package de.teamlapen.vampirism.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreCriteria;

public class ScoreboardUtil {
    public final static ScoreCriteria FACTION_CRITERIA = new ScoreCriteria("vampirism:faction");
    public final static ScoreCriteria VAMPIRE_LEVEL_CRITERIA = new ScoreCriteria("vampirism:vampire");
    public final static ScoreCriteria HUNTER_LEVEL_CRITERIA = new ScoreCriteria("vampirism:hunter");


    public static void updateScoreboard(PlayerEntity player, ScoreCriteria crit, int value) {
        if (!player.level.isClientSide) {
            player.getScoreboard().forAllObjectives(crit, player.getScoreboardName(), (obj) -> {
                obj.setScore(value);
            });
        }
    }

}
