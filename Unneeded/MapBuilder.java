package com.gotofinal.diggler.chests.utils;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SerializableAs("RC_MapMeta")
public class MapBuilder implements DataBuilder
{
    private boolean scaling;

    public MapBuilder()
    {
    }

    public MapBuilder(final Map<String, Object> map)
    {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.scaling = w.getBoolean("scaling");
    }

    public boolean isScaling()
    {
        return this.scaling;
    }

    public MapBuilder scaling(final boolean scaling)
    {
        this.scaling = scaling;
        return this;
    }

    public MapBuilder enableScaling()
    {
        return this.scaling(true);
    }

    public MapBuilder disableScaling()
    {
        return this.scaling(false);
    }

    @Override
    public void apply(final ItemMeta itemMeta)
    {
        if (! (itemMeta instanceof MapMeta))
        {
            return;
        }
        final MapMeta meta = (MapMeta) itemMeta;
        meta.setScaling(this.scaling);
    }

    @Override
    public MapBuilder use(final ItemMeta itemMeta)
    {
        if (! (itemMeta instanceof MapMeta))
        {
            return null;
        }
        final MapMeta meta = (MapMeta) itemMeta;
        this.scaling = meta.isScaling();
        return this;
    }

    @Override
    public Map<String, Object> serialize()
    {
        final SerializationBuilder b = SerializationBuilder.start(1);
        b.append("scaling", this.scaling);
        return b.build();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("scaling", this.scaling).toString();
    }

    public static MapBuilder start()
    {
        return new MapBuilder();
    }
}
