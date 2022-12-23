package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class SBEnchantmentGen extends TagsProvider<Enchantment> {
    private final DataGenerator generator;

    protected SBEnchantmentGen(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> something, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn.getPackOutput(), Registries.ENCHANTMENT, something, SimplyBackpacks.MODID, existingFileHelper);
        generator = generatorIn;
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider something) {
        this.tag(SimplyBackpacks.SOULBOUND).addOptional(new ResourceLocation("ensorcellation","soulbound"));
        this.tag(SimplyBackpacks.SOULBOUND).addOptional(new ResourceLocation("tombstone","soulbound"));
    }

    @Override
    @Nonnull
    protected Path getPath(ResourceLocation id) {
        return this.generator.getPackOutput().getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/enchantments/" + id.getPath() + ".json");
    }

    @Override
    @Nonnull
    public String getName() {
        return "Enchantment Tags";
    }
}
