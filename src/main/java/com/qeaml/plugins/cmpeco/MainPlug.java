package com.qeaml.plugins.cmpeco;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;

import com.qeaml.plugins.cmpeco.commands.*;

public class MainPlug extends JavaPlugin {
    public StringProvider strings;
    public BalanceManager balance;
    public YamlConfiguration config;
    public Permission perms;

    @Override
    public void onEnable() {
        getLogger().info("Hello");

        ensureData();
        strings = new StringProvider(this);
        balance = new BalanceManager(this);

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        if (perms == null) {
            getLogger().severe("Could not hook into permissions.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("transfer").setExecutor(new TransferCommand(this));
        getCommand("deposit").setExecutor(new DepositCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye");
        balance.save();
    }

    public void ensureData() {
        getLogger().info("Loading plugin data");

        var baseDir = getDataFolder().getAbsolutePath();
        Utils.utils.ensureFolder(baseDir);

        getLogger().info("Loading config");
        var f = new File(Utils.utils.combinePath(baseDir, "config.yml"));
        if (!f.exists()) {
            getLogger().info("Config does not exist, so the default was created in it's place.");
            try {
                f.createNewFile();
                var defCfg = getResource("default-config.yml");
                var fw = new FileOutputStream(f);
                defCfg.transferTo(fw);
            } catch (IOException e) {
                getLogger().severe("Could not load config: " + e.getLocalizedMessage());
                getPluginLoader().disablePlugin(this);
                return;
            }
        }
        config = YamlConfiguration.loadConfiguration(f);

        getLogger().info("Config loaded");
    }
}
