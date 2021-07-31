package com.qeaml.plugins.cmpeco.commands;

import com.qeaml.plugins.cmpeco.MainPlug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class TransferCommand implements CommandExecutor {
    private MainPlug plug;

    public TransferCommand(MainPlug plug) {
        this.plug = plug;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!plug.perms.has(sender, "cmp.eco.bank")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 3)
            return false;

        var from = sender.getServer().getPlayer(args[0]);
        if (from == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player: " + args[0]);
            return true;
        }
        var to = sender.getServer().getPlayer(args[1]);
        if (to == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player: " + args[1]);
            return true;
        }
        var amt = Double.parseDouble(args[2]);

        var res = plug.balance.transfer(from, to, amt);
        switch (res) {
            case OK:
                sender.sendMessage(ChatColor.GREEN + "Success!");
                break;
            case NOT_ENOUGH_BANK:
                sender.sendMessage(ChatColor.RED + "Not enough money.");
                break;
            default:
                break;
        }

        return true;
    }

}
