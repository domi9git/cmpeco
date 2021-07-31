package com.qeaml.plugins.cmpeco.commands;

import com.qeaml.plugins.cmpeco.MainPlug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class BalanceCommand implements CommandExecutor {
    private MainPlug plug;

    public BalanceCommand(MainPlug plug) {
        this.plug = plug;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        var sym = plug.config.getString("currency");
        var player = (Player) sender;
        var bank = plug.balance.getBankBal(player);
        var wallet = plug.balance.getWalletBal(player);

        String msg;
        msg = plug.strings.getPlayerString("bal-bank", player);
        msg = String.format(msg, "" + bank + sym);
        player.sendMessage(msg);

        msg = plug.strings.getPlayerString("bal-wllt", player);
        msg = String.format(msg, "" + wallet + sym);
        player.sendMessage(msg);

        return true;
    }

}
