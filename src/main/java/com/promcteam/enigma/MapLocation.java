package com.promcteam.enigma;


import com.promcteam.codex.util.SerializationBuilder;
import com.promcteam.risecore.legacy.util.DeserializationWorker;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Enigma_MapLocation")
public class MapLocation implements ConfigurationSerializable {
    public static final MapLocation ZERO = new MapLocation(0, 0);
    protected final     int         x;
    protected final     int         z;

    public MapLocation(final int x, final int z) {
        this.x = x;
        this.z = z;
    }

    public MapLocation(final Block block) {
        this.x = block.getX();
        this.z = block.getZ();
    }

    public MapLocation(final Map<String, Object> map) {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.x = w.getInt("x");
        this.z = w.getInt("z");
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public MapLocation addX(final int x) {
        return new MapLocation(this.x + x, this.z);
    }

    public MapLocation addZ(final int z) {
        return new MapLocation(this.x, this.z + z);
    }

    public MapLocation add(final int x, final int z) {
        return new MapLocation(this.x + x, this.z + z);
    }

    public MapLocation add(final MapLocation loc) {
        return new MapLocation(this.x + loc.x, this.z + loc.z);
    }

    public MapLocation subtractX(final int x) {
        return new MapLocation(this.x - x, this.z);
    }

    public MapLocation subtractZ(final int z) {
        return new MapLocation(this.x, this.z - z);
    }

    public MapLocation subtract(final int x, final int z) {
        return new MapLocation(this.x - x, this.z - z);
    }

    public MapLocation subtract(final MapLocation loc) {
        return new MapLocation(this.x - loc.x, this.z - loc.z);
    }


    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        return (this.x * this.x) + (this.z * this.z);
    }

    public double distance(final double x, final double z) {
        return Math.sqrt(this.distanceSquared(x, z));
    }

    public double distanceFromCenter(final double x, final double z) {
        return Math.sqrt(this.distanceSquaredFromCenter(x, z));
    }

    public double distance(final MapLocation location) {
        return Math.sqrt(this.distanceSquared(location));
    }

    public double distanceSquared(final double x, final double z) {
        final double deltaX = (double) this.x - x;
        final double deltaZ = (double) this.z - z;
        return (deltaX * deltaX) + (deltaZ * deltaZ);
    }

    @SuppressWarnings("MagicNumber")
    public double distanceSquaredFromCenter(final double x, final double z) {
        final double deltaX = ((double) this.x + 0.5) - x;
        final double deltaZ = ((double) this.z + 0.5) - z;
        return (deltaX * deltaX) + (deltaZ * deltaZ);
    }

    public double distanceSquared(final MapLocation location) {
        return this.distanceSquared(location.getX(), location.getZ());
    }

    public boolean isInAABB(final MapLocation min, final MapLocation max) {
        return (this.x >= min.x) && (this.x <= max.x) && (this.z >= min.z) && (this.z <= max.z);
    }

    public boolean isInSphere(final MapLocation origin, final double radius) {
        return (square(origin.x - this.x) + square(origin.z - this.z)) <= square(radius);
    }

    @Override
    public Map<String, Object> serialize() {
        return SerializationBuilder.start(3)/*.append("==", "Enigma_MapLocation")*/.append("x", this.x)
                .append("z", this.z)
                .build();
    }

    private static int square(final int x) {
        return x * x;
    }

    private static double square(final double x) {
        return x * x;
    }

    @Override
    public int hashCode() {
        int result = this.x;
        result = (31 * result) + this.z;
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapLocation)) {
            return false;
        }

        final MapLocation that = (MapLocation) o;

        return (this.x == that.x) && (this.z == that.z);

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("x", this.x)
                .append("z", this.z)
                .toString();
    }
}
