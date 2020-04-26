package com.gotofinal.diggler.chests;

import com.gotofinal.diggler.chests.cfg.Cfg;
//import com.sk89q.worldedit.bukkit.BukkitAdapter;
//import com.sk89q.worldedit.math.BlockVector3;
//import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
//import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import me.travja.darkrise.core.legacy.util.item.*;
//import me.travja.darkrise.core.util.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Chests extends JavaPlugin {
    private static Chests instance;
    private static final Map<String, WorldChests> worlds = new HashMap<>(3);

    public static Chests getInstance() {
        return instance;
    }

    public static void addWorld(final WorldChests world) {
        worlds.put(world.getWorld().getName(), world);
    }

    public static WorldChests getWorld(final String world) {
        return worlds.get(world);
    }

    public static Map<String, WorldChests> getWorlds() {
        return worlds;
    }

    {
        Chests.instance = this;
        ConfigurationSerialization.registerClass(EnchantmentStorageBuilder.class, "RC_EnchantmentStorageMeta");
        ConfigurationSerialization.registerClass(FireworkEffectBuilder.class, "RC_FireworkEffectMeta");
        ConfigurationSerialization.registerClass(LeatherArmorBuilder.class, "RC_LeatherArmorMeta");
        ConfigurationSerialization.registerClass(PotionDataBuilder.class, "RC_PotionMeta");
        ConfigurationSerialization.registerClass(FireworkBuilder.class, "RC_FireworkMeta");
        ConfigurationSerialization.registerClass(BookDataBuilder.class, "RC_BookMeta");
        ConfigurationSerialization.registerClass(SkullBuilder.class, "RC_SkullMeta");
        ConfigurationSerialization.registerClass(MapBuilder.class, "RC_MapMeta");
        ConfigurationSerialization.registerClass(ItemBuilder.class, "RC_Item");

        ConfigurationSerialization.registerClass(WorldChests.class, "RC_WorldChests");
        ConfigurationSerialization.registerClass(MapLocation.class, "RC_MapLocation");

        ConfigurationSerialization.registerClass(ItemCommand.class, "RC_ItemCommand");
    }

    @Override
    public void onEnable() {
        Cfg.init();
        WorldListener.init();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, Cfg::save, 1, Cfg.getAutoSaveTime());
        runTask(() -> worlds.values().forEach(WorldChests::addMissingChests));
    }

    public static boolean isInRegion(final Location location) {
//        WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform(); // Commented are 1.13+ api things
        RegionManager manager = /*platform*/ WorldGuardPlugin.inst().getRegionContainer()
                .get(/*BukkitAdapter.adapt(*/location.getWorld())/*)*/;

        return manager != null && manager.getApplicableRegions(location).size() > 0;
//                .getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ())).size() > 0;
    }

    @Override
    public void onDisable() {
        Cfg.save();
    }

    public static void runTaskLater(final Runnable runnable, final int delay) {
        Bukkit.getScheduler().runTaskLater(instance, runnable, delay);
    }

    public static void runTask(final Runnable runnable) {
        Bukkit.getScheduler().runTask(instance, runnable);
    }

    public static void async(final Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, runnable);
    }
}
