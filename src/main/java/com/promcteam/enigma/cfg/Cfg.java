package com.promcteam.enigma.cfg;

import com.promcteam.codex.legacy.item.FireworkBuilder;
import com.promcteam.enigma.*;
import com.promcteam.enigma.util.BlockType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public final class Cfg {
    @Getter
    private static Collection<ItemCommand>     commands        =
            Collections.singletonList(new ItemCommand(CommandType.CONSOLE, "say {player} found chest!", 0));
    @Getter
    private static Collection<FireworkBuilder> fireworks       = Collections.singletonList(FireworkBuilder.start()
            .effect(FireworkEffect.builder().withFlicker().withColor(Color.AQUA))
            .power(2));
    @Getter
    private static int                         autoSaveTime    =
            (int) (ItemCommand.TPS * TimeUnit.MINUTES.toSeconds(15));
    @Getter
    private static boolean                     asyncSaveOnFind = true;

    public static synchronized void save() {
        final File              file = new File(Enigma.getInstance().getDataFolder(), "config.yml");
        final FileConfiguration cfg;
        if (!file.exists()) {
            cfg = new YamlConfiguration();
            file.getAbsoluteFile().getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (final IOException e) {
                Enigma.getInstance().getLogger().warning("Can't create config file: " + file);
                e.printStackTrace();
            }
        } else {
            cfg = YamlConfiguration.loadConfiguration(file);
        }
        final Map<String, WorldChests> worlds = Enigma.getWorlds();

        cfg.set("autoSaveTime", autoSaveTime);
        cfg.set("asyncSaveOnFind", asyncSaveOnFind);
        cfg.set("commands", commands);
        cfg.set("fireworks", new ArrayList<>(fireworks));
        cfg.set("enabledWorlds", new ArrayList<>(worlds.keySet()));
        cfg.set("worlds", worlds);

        try {
            cfg.save(file);
        } catch (final IOException e) {
            Enigma.getInstance().getLogger().warning("Can't save config file: " + file);
            e.printStackTrace();
        }
    }

    public static void init() {
        final File              file = new File(Enigma.getInstance().getDataFolder(), "config.yml");
        final FileConfiguration cfg;
        if (!file.exists()) {
            cfg = new YamlConfiguration();
            cfg.addDefault("autoSaveTime", autoSaveTime);
            cfg.addDefault("asyncSaveOnFind", asyncSaveOnFind);
            cfg.addDefault("commands", commands);
            cfg.addDefault("fireworks", fireworks);
            cfg.addDefault("enabledWorlds", Collections.singletonList(Bukkit.getWorlds().get(0).getName()));
            final WorldChests c = new WorldChests(Bukkit.getWorlds().get(0),
                    10,
                    new MapLocation(-10, -10),
                    new MapLocation(10, 10),
                    new BlockType(Material.ENDER_CHEST, (byte) 2));
            cfg.addDefault("worlds." + c.getWorld().getName(), c);
            Enigma.addWorld(c);
            file.getAbsoluteFile().getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (final IOException e) {
                Enigma.getInstance().getLogger().warning("Can't create config file: " + file);
                e.printStackTrace();
            }
            cfg.options().copyDefaults(true);
            try {
                cfg.save(file);
            } catch (final IOException e) {
                Enigma.getInstance().getLogger().warning("Can't save config file: " + file);
                e.printStackTrace();
            }
        } else {
            cfg = YamlConfiguration.loadConfiguration(file);
        }
        autoSaveTime = cfg.getInt("autoSaveTime");
        asyncSaveOnFind = cfg.getBoolean("asyncSaveOnFind");
        //noinspection unchecked
        commands = (Collection<ItemCommand>) cfg.getList("commands");
        //noinspection unchecked
        fireworks = (Collection<FireworkBuilder>) cfg.getList("fireworks");
        for (final String world : cfg.getStringList("enabledWorlds")) {
            final World w = Bukkit.getWorld(world);
            if (w == null) {
                Enigma.getInstance()
                        .getLogger()
                        .warning("World " + world + " is enabled, but there isn't any world with this name.");
                continue;
            }
            if (cfg.isSet("worlds." + world)) {
                Enigma.addWorld((WorldChests) cfg.get("worlds." + world));
            } else {
                Enigma.addWorld(new WorldChests(w,
                        10,
                        new MapLocation(-10, -10),
                        new MapLocation(10, 10),
                        new BlockType(Material.ENDER_CHEST, (byte) 2)));
            }
        }
    }
}
