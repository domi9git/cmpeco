package com.qeaml.plugins.cmpeco.commands;

import com.qeaml.plugins.cmpeco.MainPlug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class WithdrawCommand implements CommandExecutor {
    private MainPlug plug;

    public WithdrawCommand(MainPlug plug) {
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
            amt = plug.balance.getBankBal(player);
        else
            amt = Double.parseDouble(args[0]);

        String msg;
        if (amt < 0.01) {
            msg = plug.strings.getPlayerString("with-smol", player);
            sender.sendMessage(ChatColor.RED + msg);
            return true;
        }

        var sym = plug.config.getString("currency");
        var res = plug.balance.withdraw(player, amt);
        switch (res) {
            case OK:
                msg = plug.strings.getPlayerString("with-okok", player);
                msg = String.format(msg, "" + amt + sym);
                sender.sendMessage(ChatColor.GREEN + msg);
                break;
            case NOT_ENOUGH_BANK:
                msg = plug.strings.getPlayerString("poor-bank", player);
                msg = String.format(msg, "" + amt + sym);
                sender.sendMessage(ChatColor.RED + msg);
                break;
            default:
                break;
        }

        return true;
    }
}
