package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

/**
 * Represents a singleton instance of a backpack. Sets up each backpack in it's own way
 */
public enum Backpack {
    COMMON(Rarity.COMMON, 18, "common_gui.png", 176, 150, 7, 67),
    UNCOMMON(Rarity.UNCOMMON, 33, "uncommon_gui.png", 212, 168, 25, 85),
    RARE(Rarity.RARE, 66, "rare_gui.png", 212, 222, 25, 139),
    EPIC(Rarity.EPIC, 99, "epic_gui.png", 212, 276, 25, 193),
    ULTIMATE(Rarity.EPIC, 256, "",1,1,25,190);

    public Rarity rarity;
    public int slots;

    public ResourceLocation location;
    public int xSize;
    public int ySize;
    public int slotXOffset;
    public int slotYOffset;

    Backpack(Rarity rarity, int slots, String location, int xSize, int ySize, int slotXOffset, int slotYOffset) {
        this.rarity = rarity;
        this.slots = slots;
        this.location = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/" + location);
        this.xSize = xSize;
        this.ySize = ySize;
        this.slotXOffset = slotXOffset;
        this.slotYOffset = slotYOffset;
    }
}