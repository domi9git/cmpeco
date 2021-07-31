package com.qeaml.plugins.cmpeco.commands;

import com.qeaml.plugins.cmpeco.MainPlug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class DepositCommand implements CommandExecutor {
    private MainPlug plug;

    public DepositCommand(MainPlug plug) {
        this.plug = plug;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length != 1)
            return false;

        var player = (Player) sender;
        double amt;
        if (args[0].equalsIgnoreCase("all") || args[0] == "*")
            amt = plug.balance.getWalletBal(player);
        else
            amt = Double.parseDouble(args[0]);

        String msg;
        if (amt < 0.01) {
            msg = plug.strings.getPlayerString("invalid-amt", player);
            sender.sendMessage(ChatColor.RED + msg);
            return true;
        }

        var sym = plug.config.getString("currency");
        var res = plug.balance.deposit(player, amt);
        switch (res) {
            case OK:
                msg = plug.strings.getPlayerString("dep-okok", player);
                msg = String.format(msg, "" + amt + sym);
                sender.sendMessage(ChatColor.GREEN + msg);
                break;
            case NOT_ENOUGH_WALLET:
                msg = plug.strings.getPlayerString("poor-wllt", player);
                msg = String.format(msg, "" + amt + sym);
                sender.sendMessage(ChatColor.RED + msg);
                break;
            default:
                break;
        }

        return true;
    }
}
