package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetDefaultClaimFlagCmd extends BaseCmd {

    SetDefaultClaimFlagCmd(GPFlags plugin) {
        super(plugin);
        command = "SetDefaultClaimFlag";
        usage = "<flag> [<parameters>]";
        requirePlayer = true;
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) return false;

        Player player = ((Player) sender);

        String flagName = args[0];

        FlagDefinition def = PLUGIN.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
            return true;
        }

        if (!playerHasPermissionForFlag(def, player)) {
            GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
            return true;
        }

        if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
            GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
            return true;
        }

        String[] params = new String[args.length - 1];
        System.arraycopy(args, 1, params, 0, args.length - 1);

        SetFlagResult result = PLUGIN.getFlagManager().setFlag(FlagManager.DEFAULT_FLAG_ID, def, true, params);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        if (result.isSuccess()) {
            GPFlags.sendMessage(player, color, Messages.DefaultFlagSet);
            PLUGIN.getFlagManager().save();
        } else {
            GPFlags.sendMessage(player, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        }

        return true;
    }

    @Override
    List<String> tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return flagTab(sender, args[0]);
        } else if (args.length == 2) {
            return paramTab(sender, args);
        }
        return Collections.emptyList();
    }

}
