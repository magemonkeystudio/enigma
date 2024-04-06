package studio.magemonkey.enigma;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import studio.magemonkey.codex.legacy.item.FireworkBuilder;
import studio.magemonkey.codex.util.SerializationBuilder;
import studio.magemonkey.enigma.cfg.Cfg;
import studio.magemonkey.enigma.util.BlockLocation;
import studio.magemonkey.enigma.util.BlockType;
import studio.magemonkey.enigma.util.IntRange;
import studio.magemonkey.enigma.util.IntsToLong;
import studio.magemonkey.risecore.legacy.util.DeserializationWorker;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;
import java.util.Map.Entry;

@SerializableAs("Enigma_WorldChests")
public class WorldChests implements ConfigurationSerializable {
    @Getter
    private final           World                       world;
    @Getter
    private final           int                         maxChests;
    @Getter
    private final           MapLocation                 min;
    @Getter
    private final           MapLocation                 max;
    private final           BlockType                   blockType;
    private final           Collection<BlockLocation>   chests;
    private final           Multimap<Long, MapLocation> chestsToSpawn;
    private final transient IntRange                    rx;
    private final transient IntRange                    rz;

    @SuppressWarnings("unchecked")
    public WorldChests(final Map<String, Object> map) {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.world = Bukkit.getWorld(w.getString("world"));
        this.maxChests = w.getInt("maxChests");
        this.min = /*w.deserialize("minPoint", MapLocation.class);*/w.getTypedObject("minPoint", MapLocation.class);
        this.max = /*w.deserialize("maxPoint", MapLocation.class);*/w.getTypedObject("maxPoint", MapLocation.class);
        this.blockType = BlockType.fromConfigString(w.getString("blockType"));
        this.chests = w.getHashSet("chests");
        this.chestsToSpawn = ArrayListMultimap.create(this.maxChests / 4, 5);
        boolean empty = false;
        try {
            if (((LinkedHashMap<Long, ArrayList<Object>>) map.get("chestsToSpawn")).isEmpty())
                empty = true;
        } catch (ClassCastException | NullPointerException e) {
            empty = true;
        }

        if (!empty) {
            final LinkedHashMap<Long, List<MapLocation>> chMap =
                    (LinkedHashMap<Long, List<MapLocation>>) map.get("chestsToSpawn");
            for (final Entry<Long, List<MapLocation>> entry : chMap.entrySet()) {
                final Long key;
                if (entry.getKey() instanceof Number) {
                    key = ((Number) entry.getKey()).longValue();
                } else {
                    key = Long.parseLong(entry.getKey().toString());
                }
                this.chestsToSpawn.putAll(key, entry.getValue());
            }
        }

        this.rx = new IntRange(this.min.getX(), this.max.getX());
        this.rz = new IntRange(this.min.getZ(), this.max.getZ());
    }

    public WorldChests(final World world,
                       final int maxChests,
                       final MapLocation min,
                       final MapLocation max,
                       final BlockType blockType) {
        this.world = world;
        this.maxChests = maxChests;
        this.min = min;
        this.max = max;
        this.blockType = blockType;
        this.chests = new HashSet<>(maxChests - (maxChests / 3));
        this.chestsToSpawn = ArrayListMultimap.create(this.maxChests / 4, 5);

        this.rx = new IntRange(this.min.getX(), this.max.getX());
        this.rz = new IntRange(this.min.getZ(), this.max.getZ());
    }

    public boolean onInteract(final CommandSender player, final Block block) {
        if ((player == null) || (block == null) || (this.blockType.getMat()
                != block.getType()) /*|| ((this.blockType.getType() != -1) && (this.blockType.getType() != block.getData()))*/
                || !this.chests.remove(new BlockLocation(block))) {
            return false;
        }
        block.setType(Material.AIR);
        ItemCommand.invoke(player, Cfg.getCommands());
        for (final FireworkBuilder fireData : Cfg.getFireworks()) {
            final Firework     firework = this.world.spawn(block.getLocation().add(0, 5, 0), Firework.class);
            final FireworkMeta meta     = firework.getFireworkMeta();
            fireData.apply(meta);
            firework.setFireworkMeta(meta);
        }
        this.addMissingChests();
        if (Cfg.isAsyncSaveOnFind()) {
            Enigma.async(Cfg::save);
        }
        return true;
    }

    public void onLoadChunk(final Chunk chunk) {
        int i = 0;
        for (final Iterator<MapLocation> it =
             this.chestsToSpawn.get(IntsToLong.pack(chunk.getX(), chunk.getZ())).iterator(); it.hasNext(); ) {
            final MapLocation loc   = it.next();
            final Block       block = this.spawnChest(loc.getX(), loc.getZ());
            if (block == null) {
                i++;
            } else {
                this.chests.add(new BlockLocation(block));
            }
            it.remove();
        }

        int count = 0;
        while (i > 0 && count < 200) {
            if (this.addRandomChest()) {
                i--;
            } else {
                count++;
            }
        }

        if (count >= 200) {
            Enigma.getInstance().getLogger().warning("Couldn't add new chests upon chunk load after 200 attempts.");
        }
    }

    private Block spawnChest(final int x, final int z) {
        final int y = this.world.getHighestBlockYAt(x, z);
        if (y <= 3) {
            return null;
        }
        Material mat = this.world.getBlockAt(x, y, z).getType();
        if (!(mat == Material.GRASS_BLOCK || mat == Material.STONE)) return null;
//        if (mat == Material.LAVA || mat == Material.WATER) {
//            return null;
//        }
        mat = this.world.getBlockAt(x, y + 1, z).getType();
        final Material mat2 = this.world.getBlockAt(x, y + 2, z).getType();
        if (mat != Material.AIR || mat2 != Material.AIR) return null;

        mat = this.world.getBlockAt(x, y - 1, z).getType();
        final Block block;
        if (mat == Material.WATER) {
            mat = this.world.getBlockAt(x, y - 2, z).getType();
            if (mat == Material.WATER || mat == Material.LAVA) {
                return null;
            }
            block = this.world.getBlockAt(x, y - 1, z);
        } else if (mat == Material.LAVA) {
            return null;
        } else {
            block = this.world.getBlockAt(x, y + 1, z);
        }
        if (Enigma.isInRegion(block.getLocation())) {
            return null;
        }

        Enigma.getInstance().getLogger().info("New chest created!");
        block.setType(this.blockType.getMat());
        block.getState().update();
        return block;
    }

    private boolean addRandomChest() {
        final int x = this.rx.getRandom();
        final int z = this.rz.getRandom();
        if (!this.world.isChunkLoaded(x >> 4, z >> 4)) {
            return this.chestsToSpawn.put(IntsToLong.pack(x >> 4, z >> 4), new MapLocation(x, z));
        }
        final Block block = this.spawnChest(x, z);
        return (block != null) && this.chests.add(new BlockLocation(block));
    }

    public void addMissingChests() {
        if ((this.chests.size() + this.chestsToSpawn.size()) >= this.maxChests) {
            return;
        }
        Enigma.getInstance().getLogger().info("We don't have enough chests, attempting to add more.");
        int       i     = 0;
        final int s     = (this.maxChests - (this.chests.size() + this.chestsToSpawn.size()));
        int       count = 0;
        while (i < s && count < 200) {
            if (this.addRandomChest()) {
                i++;
            } else
                count++;
        }

        if (count >= 200) {
            Enigma.getInstance()
                    .getLogger()
                    .warning("Couldn't add new chests after 200 attempts. Maybe the chunk is not loaded?");
        }
    }

    @Override
    public Map<String, Object> serialize() {
//        Map<Long, List<MapLocation>> map = new HashMap<>();
//        for (final Entry<Long, Collection<MapLocation>> entry:this.chestsToSpawn.asMap().entrySet())
//        {
//            map.put(entry.getKey(), new ArrayList<>(entry.getValue()));
//        }
        return SerializationBuilder.start(7)
                .append("world", this.world.getName())
                .append("maxChests", this.maxChests)
                .append("minPoint", this.min)
                .append("maxPoint", this.max)
                .append("blockType", this.blockType.toConfigString())
                .append("chests", new ArrayList<>(this.chests))
                .append("chestsToSpawn", this.chestsToSpawn.asMap())
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("world", this.world)
                .append("maxChests", this.maxChests)
                .append("min", this.min)
                .append("max", this.max)
                .append("blockType", this.blockType)
                .append("chests", this.chests)
                .append("chestsToSpawn", this.chestsToSpawn)
                .toString();
    }
}
