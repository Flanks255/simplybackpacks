package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class SBItemTags extends ItemTagsProvider {
    public SBItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, SimplyBackpacks.MODID, existingFileHelper);
    }

    @Override
    protected void registerTags() {
        tagBackpacks();
        tagShulkers();
        tagQuantumBags();


        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("mekanism", "personal_chest"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "modular_storage"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "modular_storage"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "storage_module0"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "storage_module1"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "storage_module2"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "storage_module3"));

        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("immersiveengineering", "crate"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("immersiveengineering", "reinforced_crate"));

        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("industrialforegoing", "infinity_backpack"));

        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("pneumaticcraft", "reinforced_chest"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("pneumaticcraft", "smart_chest"));

        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("forcecraft", "force_pack"));

    }

    private void tagBackpacks() {
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(SimplyBackpacks.COMMONBACKPACK.get());
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(SimplyBackpacks.UNCOMMONBACKPACK.get());
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(SimplyBackpacks.RAREBACKPACK.get());
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(SimplyBackpacks.EPICBACKPACK.get());
    }

    private void tagShulkers() {
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.BLACK_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.BLUE_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.BROWN_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.CYAN_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.GRAY_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.GREEN_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.LIGHT_BLUE_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.LIGHT_GRAY_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.LIME_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.MAGENTA_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.ORANGE_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.PINK_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.PURPLE_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.RED_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.WHITE_SHULKER_BOX);
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).add(Items.YELLOW_SHULKER_BOX);
    }

    private void tagQuantumBags() {
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_white"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_orange"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_magenta"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_light_blue"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_yellow"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_lime"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_pink"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_gray"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_light_gray"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_cyan"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_purple"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_blue"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_brown"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_green"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_red"));
        this.getOrCreateBuilder(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_black"));
    }

}
