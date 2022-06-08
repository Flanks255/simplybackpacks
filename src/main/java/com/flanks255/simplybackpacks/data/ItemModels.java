package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SimplyBackpacks.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerBackpacks();
    }

    private void registerBackpacks() {
        simpleItem(SimplyBackpacks.COMMONBACKPACK.get());
        simpleItem(SimplyBackpacks.UNCOMMONBACKPACK.get());
        simpleItem(SimplyBackpacks.RAREBACKPACK.get());
        simpleItem(SimplyBackpacks.EPICBACKPACK.get());
        simpleItem(SimplyBackpacks.ULTIMATEBACKPACK.get());
    }

    private void simpleItem(Item item) {
        String name = ForgeRegistries.ITEMS.getKey(item).getPath();
        singleTexture(name, mcLoc("item/handheld"), "layer0", modLoc("item/" + name));
    }
}
