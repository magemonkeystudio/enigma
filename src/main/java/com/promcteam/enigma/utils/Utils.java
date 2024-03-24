package com.promcteam.enigma.utils;

import me.travja.darkrise.core.legacy.util.DeserializationWorker;
import me.travja.darkrise.core.legacy.util.SerializationBuilder;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Utils {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private Utils() {
    }

    public static Integer parseInt(final String num) {
        try {
            return Integer.valueOf(num);
        } catch (final Exception ignored) {
            return null;
        }
    }

    public static String fixColors(final String string) {
        if (string == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String[] fixColors(final String... strings) {
        if (strings == null) {
            return EMPTY_STRING_ARRAY;
        }
        for (int i = 0; i < strings.length; i++) {
            strings[i] = fixColors(strings[i]);
        }
        return strings;
    }

    public static List<String> fixColors(final List<String> list) {
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            list.set(i, fixColors(list.get(i)));
        }
        return list;
    }

    public static List<String> removeColors(final List<String> list) {
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            list.set(i, removeColors(list.get(i)));
        }
        return list;
    }

    public static String[] removeColors(final String... strings) {
        if (strings == null) {
            return EMPTY_STRING_ARRAY;
        }
        for (int i = 0; i < strings.length; i++) {
            strings[i] = removeColors(strings[i]);
        }
        return strings;
    }

    public static String removeColors(final String string) {
        if (string == null) {
            return null;
        }
        final char[] b = string.toCharArray();
        for (int i = 0; i < (b.length - 1); i++) {
            if ((b[i] == ChatColor.COLOR_CHAR) && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1)) {
                b[i] = '&';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static Integer toInt(final String str) {
        try {
            return Integer.parseInt(str);
        } catch (final Exception e) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static Material getMaterial(final String mat) {
        Material material = Material.getMaterial(mat);
        if (material == null) {
            material = Material.matchMaterial(mat);
        }
        return material;
    }

    @SuppressWarnings("MagicNumber")
    public static Color simpleDeserializeColor(final String string) {
        if (string == null) {
            return null;
        }
        return Color.fromRGB(Integer.parseInt(string, 16));
    }

    public static List<Color> simpleDeserializeColors(final Collection<String> strings) {
        if (strings == null) {
            return new ArrayList<>(1);
        }
        final List<Color> result = new ArrayList<>(strings.size());
        for (final String str : strings) {
            result.add(simpleDeserializeColor(str));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static FireworkEffect simpleDeserializeEffect(final Map<Object, Object> map) {
        if (map == null) {
            return null;
        }
        final DeserializationWorker w = DeserializationWorker.startUnsafe(map);

        final Type        type       = w.getEnum("type", Type.BALL);
        final boolean     trail      = w.getBoolean("trail");
        final boolean     flicker    = w.getBoolean("flicker");
        final List<Color> colors     = simpleDeserializeColors(w.getTypedObject("colors"));
        final List<Color> fadeColors = simpleDeserializeColors(w.getTypedObject("fadeColors"));
        return FireworkEffect.builder()
                .with(type)
                .trail(trail)
                .flicker(flicker)
                .withColor(colors)
                .withFade(fadeColors)
                .build();
    }

    public static List<FireworkEffect> simpleDeserializeEffects(final Collection<Map<Object, Object>> list) {
        if (list == null) {
            return new ArrayList<>(1);
        }
        final List<FireworkEffect> result = new ArrayList<>(list.size());
        for (final Map<Object, Object> map : list) {
            result.add(simpleDeserializeEffect(map));
        }
        return result;
    }

    @SuppressWarnings("MagicNumber")
    public static String simpleSerializeColor(final Color color) {
        if (color == null) {
            return null;
        }
        return Integer.toString(color.asRGB(), 16);
    }

    public static List<String> simpleSerializeColors(final Collection<Color> colors) {
        if (colors == null) {
            return new ArrayList<>(1);
        }
        final List<String> result = new ArrayList<>(colors.size());
        for (final Color color : colors) {
            result.add(simpleSerializeColor(color));
        }
        return result;
    }

    public static Map<String, Object> simpleSerializeEffect(final FireworkEffect effect) {
        if (effect == null) {
            return null;
        }
        final SerializationBuilder b = SerializationBuilder.start(5);
        b.append("type", effect.getType());
        b.append("trail", effect.hasTrail());
        b.append("flicker", effect.hasFlicker());
        b.append("colors", simpleSerializeColors(effect.getColors()));
        b.append("fadeColors", simpleSerializeColors(effect.getFadeColors()));
        return b.build();
    }

    public static List<Map<String, Object>> simpleSerializeEffects(final Collection<FireworkEffect> effects) {
        if (effects == null) {
            return new ArrayList<>(1);
        }
        final List<Map<String, Object>> result = new ArrayList<>(effects.size());
        for (final FireworkEffect effect : effects) {
            result.add(simpleSerializeEffect(effect));
        }
        return result;
    }

    public static ItemMeta getItemMeta(final ItemStack itemStack) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }
        return meta;
    }
}
