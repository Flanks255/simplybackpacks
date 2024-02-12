package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), SimplyBackpacks.MODID, existingFileHelper);
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
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        singleTexture(name, mcLoc("item/handheld"), "layer0", modLoc("item/" + name));
    }
}
