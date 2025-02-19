package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.command.arguments.ActionArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class BindActionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("bind-action")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .then(Commands.argument("shortcutnumber", IntegerArgumentType.integer(1, 3))
                        .then(Commands.argument("action", ActionArgument.actions())
                                .executes(context -> bindAction(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "shortcutnumber"), ActionArgument.getAction(context, "action")))))
                .then(Commands.literal("help")
                        .executes(BindActionCommand::help));
    }

    private static int bindAction(CommandContext<CommandSource> context, ServerPlayerEntity asPlayer, int number, IAction action) {
        FactionPlayerHandler.get(asPlayer).setBoundAction(number, action, true, true);
        context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.base.bind_action.success", action.getName(), number), false);
        return 0;
    }

    private static int help(CommandContext<CommandSource> context) {
        context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.base.bind_action.help"), false);
        return 0;
    }

}
