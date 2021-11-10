package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;

public class SBEnchantmentGen extends TagsProvider<Enchantment> {


    protected SBEnchantmentGen(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn, Registry.ENCHANTMENT, SimplyBackpacks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(SimplyBackpacks.SOULBOUND).addOptional(new ResourceLocation("ensorcellation","soulbound"));
        this.tag(SimplyBackpacks.SOULBOUND).addOptional(new ResourceLocation("tombstone","soulbound"));
    }

    @Override
    @Nonnull
    protected Path getPath(ResourceLocation id) {
        return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/enchantments/" + id.getPath() + ".json");
    }

    @Override
    @Nonnull
    public String getName() {
        return "Enchantment Tags";
    }
}
