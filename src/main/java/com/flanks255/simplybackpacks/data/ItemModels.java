package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SimplyBackpacks.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerBackpack();
    }

    private void registerBackpack() {
        /*
        String path = SimplyBackpacks.BACKPACKITEM.get().getRegistryName().getPath();
        final ResourceLocation tier = new ResourceLocation(SimplyBackpacks.MODID, "tier");
        withExistingParent(path, mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/common"))
                .override()
                .predicate(tier, 1)
                .model(new ModelFile.UncheckedModelFile(modLoc("item/uncommonbackpack"))).end()
                .override()
                .predicate(tier, 2)
                .model(new ModelFile.UncheckedModelFile(modLoc("item/rarebackpack"))).end()
                .override()
                .predicate(tier, 3)
                .model(new ModelFile.UncheckedModelFile(modLoc("item/epicbackpack"))).end();*/
    }
}
