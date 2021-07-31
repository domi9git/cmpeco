package com.qeaml.plugins.cmpeco.commands;

import com.qeaml.plugins.cmpeco.MainPlug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class PayCommand implements CommandExecutor {
    private MainPlug plug;

    public PayCommand(MainPlug plug) {
        this.plug = plug;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length != 2)
            return false;

        String msg;
        var from = (Player) sender;
        var to = sender.getServer().getPlayer(args[0]);
        if (to == null) {
            msg = plug.strings.getPlayerString("player-not-found", from);
            from.sendMessage(ChatColor.RED + msg);
            return true;
        }

        var sym = plug.config.getString("currency");
        var amt = Double.parseDouble(args[1]);
        var res = plug.balance.pay(from, to, amt);
        switch (res) {
            case OK:
                msg = plug.strings.getPlayerString("pay-send", from);
                msg = String.format(msg, "" + amt + sym, to.getDisplayName());
                from.sendMessage(ChatColor.GREEN + msg);

                msg = plug.strings.getPlayerString("pay-recv", to);
                msg = String.format(msg, "" + amt + sym, from.getDisplayName());
                to.sendMessage(ChatColor.GOLD + msg);
                break;

            case NOT_ENOUGH_WALLET:
                msg = plug.strings.getPlayerString("pay-poor", from);
                from.sendMessage(ChatColor.RED + msg);
                break;

            case TOO_FAR:
                msg = plug.strings.getPlayerString("pay-2far", from);
                from.sendMessage(ChatColor.RED + msg);
                break;

            case UNSAFE:
                

            default:
                break;
        }

        return true;
    }
}
