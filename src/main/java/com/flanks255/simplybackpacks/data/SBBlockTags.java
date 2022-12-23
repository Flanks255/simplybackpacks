package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class SBBlockTags extends BlockTagsProvider {
    public SBBlockTags(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> something, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn.getPackOutput(), something, SimplyBackpacks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider something) {

    }
}
