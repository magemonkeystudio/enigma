package com.gotofinal.diggler.chests.utils;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SerializableAs("Enigma_SkullMeta")
public class SkullBuilder implements DataBuilder {
    private String owner;

    public SkullBuilder() {
    }

    public SkullBuilder(final Map<String, Object> map) {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.owner = w.getString("owner");
    }

    public String getOwner() {
        return this.owner;
    }

    @SuppressWarnings("TypeMayBeWeakened")
    public SkullBuilder owner(final String owner) {
        this.owner = owner;
        return this;
    }

    public SkullBuilder clear() {
        this.owner = null;
        return this;
    }

    @Override
    public void apply(final ItemMeta itemMeta) {
        if (!(itemMeta instanceof SkullMeta)) {
            return;
        }
        final SkullMeta meta = (SkullMeta) itemMeta;
        meta.setOwner(Utils.fixColors(this.owner));
    }

    @Override
    public SkullBuilder use(final ItemMeta itemMeta) {
        if (!(itemMeta instanceof SkullMeta)) {
            return null;
        }
        final SkullMeta meta = (SkullMeta) itemMeta;
        this.owner = Utils.removeColors(meta.getOwner());
        return this;
    }

//    @Override
//    public DataBuilder applyFunc(final UnaryOperator<String> func)
//    {
//        if (this.owner != null)
//        {
//            this.owner = func.apply(this.owner);
//        }
//        return this;
//    }

    @Override
    public Map<String, Object> serialize() {
        final SerializationBuilder b = SerializationBuilder.start(1);
        b.append("owner", this.owner);
        return b.build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("owner", this.owner)
                .toString();
    }

    public static SkullBuilder start() {
        return new SkullBuilder();
    }
}
