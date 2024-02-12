package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Represents a singleton instance of a backpack. Sets up each backpack in its own way
 */
public enum Backpack {
    COMMON("Common", Rarity.COMMON, 18, 2, 9, "common_gui.png", 176, 150, 7, 67, SimplyBackpacks.COMMONBACKPACK),
    UNCOMMON("Uncommon", Rarity.UNCOMMON, 33, 3, 11, "uncommon_gui.png", 212, 168, 25, 85, SimplyBackpacks.UNCOMMONBACKPACK),
    RARE("Rare", Rarity.RARE, 66, 6, 11, "rare_gui.png", 212, 222, 25, 139, SimplyBackpacks.RAREBACKPACK),
    EPIC("Epic", Rarity.EPIC, 99, 9, 11,"epic_gui.png", 212, 276, 25, 193, SimplyBackpacks.EPICBACKPACK),
    ULTIMATE("Ultimate", Rarity.EPIC, 158, 13, 16, "ultimate_gui.png",302,258,71,175, SimplyBackpacks.ULTIMATEBACKPACK);

    public final Rarity rarity;
    public final int slots;

    public final ResourceLocation texture;
    public final int xSize;
    public final int ySize;
    //offset from left edge of texture, to left edge of first player inventory slot.
    public final int slotXOffset;
    //offset from left edge of texture, to left edge of first player inventory slot.
    public final int slotYOffset;
    public final int slotRows;
    public final int slotCols;
    public final String name;
    public final DeferredItem<Item> item;

    Backpack(String name, Rarity rarity, int slots, int rows, int cols, String location, int xSize, int ySize, int slotXOffset, int slotYOffset, DeferredItem<Item> itemIn) {
        this.name = name;
        this.rarity = rarity;
        this.slots = slots;
        this.slotRows = rows;
        this.slotCols = cols;
        this.texture = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/" + location);
        this.xSize = xSize;
        this.ySize = ySize;
        this.slotXOffset = slotXOffset;
        this.slotYOffset = slotYOffset;
        this.item = itemIn;
    }
}