package com.gotofinal.diggler.chests.utils;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.*;

@SerializableAs("RC_Item")
public class ItemBuilder implements ConfigurationSerializable {
    protected Material material = Material.AIR;
    protected int amount = 1;
    protected short durability = 0;
    protected String name;
    protected List<String> lore = new ArrayList<>(5);
    protected Map<Enchantment, Integer> enchants = new LinkedHashMap<>(3);
    protected DataBuilder dataBuilder = null;
    //  protected UnaryOperator<String> func;

    public ItemBuilder() {
    }

    @SuppressWarnings("unchecked")
    public ItemBuilder(final Map<String, Object> map) {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.material = Utils.getMaterial(w.getString("material", "AIR"));
        this.amount = w.getInt("amount", 1);
        this.durability = w.getShort("durability");
        this.name = w.getString("name");
        this.lore = w.getStringList("lore");
        final Map<Object, Object> enchantsMap = w.getTypedObject("enchants");
        for (final Map.Entry<Object, Object> entry : enchantsMap.entrySet()) {
            this.enchants.put(Enchantment.getByName(entry.getKey().toString()), ((Number) entry.getValue()).intValue());
        }
        this.dataBuilder = w.getTypedObject("data");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("material", this.material).append("amount", this.amount).append("durability", this.durability).append("name", this.name).append("lore", this.lore).append("enchants", this.enchants).append("dataBuilder", this.dataBuilder).toString();
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getAmount() {
        return this.amount;
    }

    public short getDurability() {
        return this.durability;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public String getName() {
        return this.name;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return this.enchants;
    }

    public DataBuilder getDataBuilder() {
        return this.dataBuilder;
    }

    public ItemBuilder material(final Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder material(final ItemStack source) {
        this.material = source.getType();
        return this;
    }

    public ItemBuilder durability(final int damage) {
        return this.durability((short) damage);
    }

    public ItemBuilder durability(final short damage) {
        this.durability = damage;
        return this;
    }

    public ItemBuilder durability(final ItemStack source) {
        this.durability = source.getDurability();
        return this;
    }

    public ItemBuilder amount(final int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder amount(final ItemStack source) {
        this.amount = source.getAmount();
        return this;
    }
//
//    public ItemBuilder apply(final UnaryOperator<String> func)
//    {
//        this.func = func;
//        return this;
//    }

    public ItemBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder name(final ItemMeta source) {
        this.name = Utils.removeColors(source.getDisplayName());
        return this;
    }

    public ItemBuilder clearName() {
        this.name = null;
        return this;
    }

    public ItemBuilder lore(final List<String> lore) {
        this.lore = (lore == null) ? new ArrayList<String>(5) : new ArrayList<>(lore);
        return this;
    }

    public ItemBuilder lore(final ItemMeta source) {
        this.lore = source.hasLore() ? Utils.removeColors(new ArrayList<>(source.getLore())) : new ArrayList<String>(5);
        return this;
    }

    public ItemBuilder newLoreLine(final String lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemBuilder newLoreLine(final Object lore) {
        this.lore.add(lore.toString());
        return this;
    }

    public ItemBuilder newLoreLine(final Collection<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public ItemBuilder newLoreLine(final String... lore) {
        this.newLoreLine(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder insertLoreLine(final int index, final String lore) {
        this.lore.add(index, lore);
        return this;
    }

    public ItemBuilder insertLoreLine(final int index, final Collection<String> lore) {
        this.lore.addAll(index, lore);
        return this;
    }

    public ItemBuilder insertLoreLine(final int index, final String... lore) {
        this.insertLoreLine(index, Arrays.asList(lore));
        return this;
    }

    public ItemBuilder removeLoreLine(final String lore) {
        this.lore.remove(lore);
        return this;
    }

    public ItemBuilder removeLoreLine(final Collection<String> lore) {
        this.lore.removeAll(lore);
        return this;
    }

    public ItemBuilder removeLoreLine(final String... lore) {
        this.removeLoreLine(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder removeLoreLine(final int lore) {
        this.lore.remove(lore);
        return this;
    }

    public ItemBuilder setLoreLine(final int index, final String lore) {
        this.lore.set(index, lore);
        return this;
    }

    public ItemBuilder clearLore() {
        this.lore.clear();
        return this;
    }

    public ItemBuilder enchant(final Map<Enchantment, Integer> enchants) {
        this.enchants = new LinkedHashMap<>(enchants);
        return this;
    }

    public ItemBuilder enchant(final ItemMeta source) {
        this.enchants = source.hasEnchants() ? new LinkedHashMap<>(source.getEnchants()) : new LinkedHashMap<Enchantment, Integer>(3);
        return this;
    }

    public ItemBuilder enchant(final Enchantment enchantment, final int power) {
        this.enchants.put(enchantment, power);
        return this;
    }

    public ItemBuilder enchant(final Enchantment enchantment) {
        this.enchant(enchantment, 1);
        return this;
    }

    public ItemBuilder unEnchant(final Enchantment enchantment) {
        this.enchants.remove(enchantment);
        return this;
    }

    public ItemBuilder clearEnchants() {
        this.enchants.clear();
        return this;
    }

    public ItemBuilder data(final DataBuilder dataBuilder) {
        this.dataBuilder = dataBuilder;
        return this;
    }

    public ItemBuilder data(final ItemMeta meta) {
        // TODO: maybe find some way to do that more OOP
        if (meta instanceof BookMeta) {
            this.dataBuilder = new BookDataBuilder();
        }
        if (meta instanceof EnchantmentStorageMeta) {
            this.dataBuilder = new EnchantmentStorageBuilder();
        }
        if (meta instanceof FireworkEffectMeta) {
            this.dataBuilder = new FireworkEffectBuilder();
        }
        if (meta instanceof FireworkMeta) {
            this.dataBuilder = new FireworkBuilder();
        }
        if (meta instanceof LeatherArmorMeta) {
            this.dataBuilder = new LeatherArmorBuilder();
        }
        if (meta instanceof MapMeta) {
            this.dataBuilder = new MapBuilder();
        }
        if (meta instanceof PotionMeta) {
            this.dataBuilder = new PotionDataBuilder();
        }
        if (meta instanceof SkullMeta) {
            this.dataBuilder = new SkullBuilder();
        }
        if (this.dataBuilder != null) {
            this.dataBuilder.use(meta);
        }
        return this;
    }

    public ItemStack build() {
        final ItemStack item = new ItemStack(this.material, this.amount, this.durability);
//        this.applyFunc();
        final ItemMeta meta = Bukkit.getItemFactory().getItemMeta(this.material);
        if (this.name != null) {
            meta.setDisplayName(Utils.fixColors(this.name));
        }
        if ((this.lore != null) && !this.lore.isEmpty()) {
            final List<String> lore = new ArrayList<>(this.lore.size() + 5);
            for (final String loreLine : this.lore) {
                Collections.addAll(lore, loreLine.split("\n"));
            }
//            this.lore.stream().forEach(str -> Collections.addAll(lore, str.split("\n")));
            meta.setLore(Utils.fixColors(lore));
        }
        if (this.enchants != null) {
            for (final Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        if (this.dataBuilder != null) {
            this.dataBuilder.apply(meta);
        }
        item.setItemMeta(meta);
        return item;
    }

//    private void applyFunc()
//    {
//        if (this.func == null)
//        {
//            return;
//        }
//        if (this.name != null)
//        {
//            this.name = this.func.apply(this.name);
//        }
//        if ((this.lore != null) && ! this.lore.isEmpty())
//        {
//            this.lore = Stream.of(this.func.apply(StringUtils.join(this.lore, '\n')).split("\n")).filter(s -> ! s.equals("<NO-LINE>")).collect(Collectors.toList());
//        }
//        if (this.dataBuilder != null)
//        {
//            this.dataBuilder.applyFunc(this.func);
//        }
//    }

    public ItemBuilder reset() {
        this.material = Material.AIR;
        this.amount = 1;
        this.durability = 0;
        this.name = null;
        this.lore.clear();
        this.enchants.clear();
        this.dataBuilder = null;
        return this;
    }

    @Override
    public Map<String, Object> serialize() {
        final SerializationBuilder b = SerializationBuilder.start(7);
        b.append("material", this.material);
        b.append("amount", this.amount);
        b.append("durability", this.durability);
        b.append("name", this.name);
        b.append("lore", this.lore);
        final SerializationBuilder enchant = SerializationBuilder.start(this.enchants.size());
        for (final Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
            enchant.append(entry.getKey(), entry.getValue());
        }
        b.append("enchants", enchant);
        b.append("data", this.dataBuilder);
        return b.build();
    }

    public static ItemBuilder newItem(final Material material) {
        return new ItemBuilder().material(material);
    }

    public static ItemBuilder newItem(final ItemStack itemStack) {
        if (itemStack == null) {
            return new ItemBuilder();
        }
        final ItemBuilder itemBuilder = new ItemBuilder().material(itemStack).amount(itemStack).durability(itemStack);
        final ItemMeta meta = Utils.getItemMeta(itemStack);
        if (meta == null) {
            return itemBuilder;
        }
        return itemBuilder.name(meta).lore(meta).enchant(meta).data(meta);
    }
}
