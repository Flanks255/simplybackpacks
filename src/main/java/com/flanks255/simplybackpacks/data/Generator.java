package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class Generator {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(new ItemModels(generator, event.getExistingFileHelper()));
        SBBlockTags blockTags = new SBBlockTags(generator, event.getExistingFileHelper());
        generator.addProvider(new SBItemTags(generator, blockTags, event.getExistingFileHelper()));
        generator.addProvider(new SBEnchantmentGen(generator, Registry.ENCHANTMENT, SimplyBackpacks.MODID, event.getExistingFileHelper()));
        generator.addProvider(new SBRecipes(generator));
    }
}
