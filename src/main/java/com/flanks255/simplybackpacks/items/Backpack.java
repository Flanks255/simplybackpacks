package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

/**
 * Represents a singleton instance of a backpack. Sets up each backpack in its own way
 */
public enum Backpack {
    COMMON("Common", Rarity.COMMON, 18, "common_gui.png", 176, 150, 7, 67),
    UNCOMMON("Uncommon", Rarity.UNCOMMON, 33, "uncommon_gui.png", 212, 168, 25, 85),
    RARE("Rare", Rarity.RARE, 66, "rare_gui.png", 212, 222, 25, 139),
    EPIC("Epic", Rarity.EPIC, 99, "epic_gui.png", 212, 276, 25, 193),
    ULTIMATE("Ultimate", Rarity.EPIC, 256, "",1,1,25,190);

    public Rarity rarity;
    public int slots;

    public ResourceLocation texture;
    public int xSize;
    public int ySize;
    public int slotXOffset;
    public int slotYOffset;
    public String name;

    Backpack(String name, Rarity rarity, int slots, String location, int xSize, int ySize, int slotXOffset, int slotYOffset) {
        this.name = name;
        this.rarity = rarity;
        this.slots = slots;
        this.texture = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/" + location);
        this.xSize = xSize;
        this.ySize = ySize;
        this.slotXOffset = slotXOffset;
        this.slotYOffset = slotYOffset;
    }
}