package com.gotofinal.diggler.chests.utils;

import java.util.Map;

import org.bukkit.FireworkEffect;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


@SerializableAs("Enigma_FireworkEffectMeta")
public class FireworkEffectBuilder implements DataBuilder {
    private FireworkEffect effect;

    public FireworkEffectBuilder() {
    }

    @SuppressWarnings("unchecked")
    public FireworkEffectBuilder(final Map<String, Object> map) {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.effect = Utils.simpleDeserializeEffect(w.<Map<Object, Object>>getTypedObject("effect"));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("effect", this.effect)
                .toString();
    }

    public FireworkEffect getEffect() {
        return this.effect;
    }

    public FireworkEffectBuilder effect(final FireworkEffect effect) {
        this.effect = effect;
        return this;
    }

    public FireworkEffectBuilder effect(final FireworkEffect.Builder effect) {
        this.effect = effect.build();
        return this;
    }

    @Override
    public void apply(final ItemMeta itemMeta) {
        if (!(itemMeta instanceof FireworkEffectMeta)) {
            return;
        }
        final FireworkEffectMeta meta = (FireworkEffectMeta) itemMeta;
        meta.setEffect(this.effect);
    }

    @Override
    public FireworkEffectBuilder use(final ItemMeta itemMeta) {
        if (!(itemMeta instanceof FireworkEffectMeta)) {
            return null;
        }
        final FireworkEffectMeta meta = (FireworkEffectMeta) itemMeta;
        this.effect = meta.getEffect();
        return this;
    }

    @Override
    public Map<String, Object> serialize() {
        final SerializationBuilder b = SerializationBuilder.start(1);
        b.append("effect", Utils.simpleSerializeEffect(this.effect));
        return b.build();
    }

    public static FireworkEffectBuilder start() {
        return new FireworkEffectBuilder();
    }
}
