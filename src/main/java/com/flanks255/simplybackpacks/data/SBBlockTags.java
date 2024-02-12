package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class SBBlockTags extends BlockTagsProvider {
    public SBBlockTags(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> something, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn.getPackOutput(), something, SimplyBackpacks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider something) {

    }
}
