package com.qeaml.plugins.cmpeco;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BalanceManager {
    private Logger log;
    private MainPlug plug;
    private YamlConfiguration source;

    public BalanceManager(MainPlug plug) {
        this.plug = plug;
        this.log = plug.getLogger();
        load();
    }

    public void load() {
        log.info("Loading balance information");

        var baseDir = plug.getDataFolder().getAbsolutePath();
        var balDir = Utils.utils.combinePath(baseDir, "balance.yml");
        var balFile = new File(balDir);
        if (!balFile.exists()) {
            try {
                balFile.createNewFile();
                var defBal = plug.getResource("default-balance.yml");
                var balOut = new FileOutputStream(balFile);
                defBal.transferTo(balOut);
            } catch (IOException e) {
                log.warning("Could not load balance: " + e.getLocalizedMessage());
                plug.getPluginLoader().disablePlugin(plug);
                return;
            }
        }
        source = YamlConfiguration.loadConfiguration(balFile);

        log.info("Balances loaded");
    }

    public void save() {
        log.info("Saving balances");

        var baseDir = plug.getDataFolder().getAbsolutePath();
        var balDir = Utils.utils.combinePath(baseDir, "balance.yml");
        var balFile = new File(balDir);
        try {
            source.save(balFile);
        } catch (IOException e) {
            log.warning("Could not save balances: " + e.getLocalizedMessage());
            return;
        }

        log.info("Balances saved");
    }

    public void setWalletBal(Player player, double amt) {
        var section = source.getConfigurationSection("wallet");
        section.set(player.getUniqueId().toString(), amt);
        source.set("wallet", section);
    }

    public void setBankBal(Player player, double amt) {
        var section = source.getConfigurationSection("bank");
        section.set(player.getUniqueId().toString(), amt);
        source.set("bank", section);
    }

    public double getBankBal(Player player) {
        var id = player.getUniqueId().toString();
        var bankSection = source.getConfigurationSection("bank");
        var res = bankSection.getDouble(id, Double.MAX_VALUE);
        if (res == Double.MAX_VALUE) {
            setBankBal(player, 0.0);
            return 0.0;
        }
        return res;
    }

    public double getWalletBal(Player player) {
        var id = player.getUniqueId().toString();
        var walletSection = source.getConfigurationSection("wallet");
        var res = walletSection.getDouble(id, Double.MAX_VALUE);
        if (res == Double.MAX_VALUE) {
            var start = plug.config.getDouble("starter-money");
            setWalletBal(player, start);
            return start;
        }
        return res;
    }

    public enum TransferResult {
        OK, NOT_ENOUGH_BANK, NOT_ENOUGH_WALLET, UNSAFE, TOO_FAR
    }

    public TransferResult deposit(Player player, double amt) {
        log.info("Deposit: " + player.getName() + "$" + amt);

        var bank = getBankBal(player);
        var wallet = getWalletBal(player);

        if (bank < amt)
            return TransferResult.NOT_ENOUGH_BANK;

        wallet += amt;
        bank -= amt;

        setBankBal(player, bank);
        setWalletBal(player, wallet);
        save();

        return TransferResult.OK;
    }

    public TransferResult withdraw(Player player, double amt) {
        log.info("Withdraw: " + player.getName() + "$" + amt);

        var bank = getBankBal(player);
        var wallet = getWalletBal(player);

        if (wallet < amt)
            return TransferResult.NOT_ENOUGH_BANK;

        wallet -= amt;
        bank += amt;

        setBankBal(player, bank);
        setWalletBal(player, wallet);
        save();

        return TransferResult.OK;
    }

    public TransferResult transfer(Player from, Player to, double amt) {
        log.info("Transfer: " + from.getName() + "->" + to.getName() + "$" + amt);

        var fromBank = getBankBal(from);
        var toBank = getBankBal(to);

        if (fromBank < amt)
            return TransferResult.NOT_ENOUGH_BANK;

        fromBank -= amt;
        toBank += amt;

        setBankBal(from, fromBank);
        setBankBal(to, toBank);
        save();

        return TransferResult.OK;
    }

    public TransferResult pay(Player from, Player to, double amt) {
        log.info("Pay: " + from.getName() + "->" + to.getName() + "$" + amt);

        var maximum = plug.config.getDouble("pay-threshold", Double.MAX_VALUE);
        if (amt > maximum)
            return TransferResult.UNSAFE;

        var maxDist = plug.config.getDouble("pay-distance", Double.MAX_VALUE);
        var fromLoc = from.getLocation();
        var toLoc = to.getLocation();
        // calculate the receiver's position relative to the sender
        var diffX = Math.abs(fromLoc.getX() - toLoc.getX());
        var diffY = Math.abs(fromLoc.getY() - toLoc.getY());
        var diffZ = Math.abs(fromLoc.getZ() - toLoc.getZ());
        // pythagorean theorem
        var distance = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (distance > maxDist) {
            log.info("Too far.");
            return TransferResult.TOO_FAR;
        }

        var fromWallet = getWalletBal(from);
        var toWallet = getWalletBal(to);

        if (fromWallet < amt)
            return TransferResult.NOT_ENOUGH_WALLET;

        fromWallet -= amt;
        toWallet += amt;

        setWalletBal(from, fromWallet);
        setWalletBal(to, toWallet);
        save();

        return TransferResult.OK;
    }
}
