package com.gotofinal.diggler.chests.utils;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.meta.ItemMeta;

public interface DataBuilder extends ConfigurationSerializable {

    void apply(ItemMeta itemMeta);

    DataBuilder use(ItemMeta itemMeta);

//    default DataBuilder applyFunc(final UnaryOperator<String> func)
//    {
//        return this;
//    }
}
