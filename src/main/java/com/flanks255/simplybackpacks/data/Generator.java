package com.flanks255.simplybackpacks.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class Generator {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(true, new ItemModels(generator, event.getExistingFileHelper()));
        SBBlockTags blockTags = new SBBlockTags(generator, event.getExistingFileHelper());
        generator.addProvider(true, new SBItemTags(generator, blockTags, event.getExistingFileHelper()));
        generator.addProvider(true, new SBEnchantmentGen(generator, event.getExistingFileHelper()));
        generator.addProvider(true, new SBRecipes(generator));
    }
}
