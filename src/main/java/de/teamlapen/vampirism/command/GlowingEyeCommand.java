package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class GlowingEyeCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("glowingEye")
                .then(Commands.argument("on", BoolArgumentType.bool())
                        .executes(context -> {
                            return setGlowingEye(context, context.getSource().getPlayerOrException(), BoolArgumentType.getBool(context, "on"));
                        }));
    }

    private static int setGlowingEye(CommandContext<CommandSource> context, PlayerEntity player, boolean on) {
        VampirePlayer.getOpt(player).ifPresent(vampire -> vampire.setGlowingEyes(on));
        if (on) {
            context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.base.glowing_eyes.enabled", on), false);
        } else {
            context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.base.glowing_eyes.disabled", on), false);
        }
        return 0;
    }
}
