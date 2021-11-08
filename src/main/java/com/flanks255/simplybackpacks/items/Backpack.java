package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

/**
 * Represents a singleton instance of a backpack. Sets up each backpack in its own way
 */
public enum Backpack {
    COMMON("Common", Rarity.COMMON, 18, "common_gui.png", 176, 150, 7, 67, SimplyBackpacks.COMMONBACKPACK),
    UNCOMMON("Uncommon", Rarity.UNCOMMON, 33, "uncommon_gui.png", 212, 168, 25, 85, SimplyBackpacks.UNCOMMONBACKPACK),
    RARE("Rare", Rarity.RARE, 66, "rare_gui.png", 212, 222, 25, 139, SimplyBackpacks.RAREBACKPACK),
    EPIC("Epic", Rarity.EPIC, 99, "epic_gui.png", 212, 276, 25, 193, SimplyBackpacks.EPICBACKPACK),
    ULTIMATE("Ultimate", Rarity.EPIC, 144, "ultimate_gui.png",302,276,71,193, SimplyBackpacks.ULTIMATEBACKPACK);

    public Rarity rarity;
    public int slots;

    public ResourceLocation texture;
    public int xSize;
    public int ySize;
    public int slotXOffset;
    public int slotYOffset;
    public String name;
    public RegistryObject<Item> item;

    Backpack(String name, Rarity rarity, int slots, String location, int xSize, int ySize, int slotXOffset, int slotYOffset, RegistryObject<Item> itemIn) {
        this.name = name;
        this.rarity = rarity;
        this.slots = slots;
        this.texture = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/" + location);
        this.xSize = xSize;
        this.ySize = ySize;
        this.slotXOffset = slotXOffset;
        this.slotYOffset = slotYOffset;
        this.item = itemIn;
    }
}