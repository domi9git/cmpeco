package com.qeaml.plugins.cmpeco;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StringProvider {
    private YamlConfiguration source;
    private ConfigurationSection langCodes;
    private MainPlug plug;
    private Logger log;
    public String defLang; // default language

    public StringProvider(MainPlug plug) {
        this.plug = plug;
        this.log = plug.getLogger();
        this.defLang = plug.config.getString("lang", "test");
        this.log.info("Default language is " + this.defLang);
        load();
    }

    public void load() {
        log.info("Loading strings");
        var res = this.plug.getResource("strings.yml");
        try {
            var src = new String(res.readAllBytes());
            var rdr = new StringReader(src);
            source = YamlConfiguration.loadConfiguration(rdr);
        } catch (IOException e) {
            log.severe("Could not load strings: " + e.getLocalizedMessage());
            return;
        }
        langCodes = source.getConfigurationSection("langCodes");
        log.info("Strings loaded");
    }

    public String getString(String key, String lang) {
        // log.info("Getting string `" + key + "` for language `" + lang + "`.");
        var langSection = source.getConfigurationSection(lang);
        var raw = langSection.getString(key, "");
        if (raw == "") {
            log.info("Unknown string `" + key + "` for language " + lang);
            return key; // return "FUCK";
        }
        return raw;
    }

    public String getPlayerString(String key, Player player) {
        return getString(key, localeToLang(player));
    }

    public String localeToLang(Player player) {
        var loc = player.getLocale();
        // log.info(player.getName() + " locale is " + loc);
        for (String code : langCodes.getKeys(false)) {
            if (loc.toLowerCase().startsWith(code.toLowerCase())) {
                return langCodes.getString(code);
            }
        }
        log.warning("Unrecognized player locale: " + loc);
        return defLang;
    }
}
