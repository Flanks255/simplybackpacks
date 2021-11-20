package com.flanks255.simplybackpacks.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class Generator {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(new ItemModels(generator, event.getExistingFileHelper()));
        SBBlockTags blockTags = new SBBlockTags(generator, event.getExistingFileHelper());
        generator.addProvider(new SBItemTags(generator, blockTags, event.getExistingFileHelper()));
        generator.addProvider(new SBEnchantmentGen(generator, event.getExistingFileHelper()));
        generator.addProvider(new SBRecipes(generator));
    }
}
