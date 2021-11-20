package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class SBItemTags extends ItemTagsProvider {
    public SBItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, SimplyBackpacks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tagShulkers();
        tagQuantumBags();
        tagKrates();


        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("mekanism", "personal_chest"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "modular_storage"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "modular_storage"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "storage_module0"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "storage_module1"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "storage_module2"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("rftoolsstorage", "storage_module3"));

        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("immersiveengineering", "crate"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("immersiveengineering", "reinforced_crate"));

        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("industrialforegoing", "infinity_backpack"));

        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("pneumaticcraft", "reinforced_chest"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("pneumaticcraft", "smart_chest"));

        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("forcecraft", "force_pack"));

        this.tag(SimplyBackpacks.CURIOS_BACK).add(SimplyBackpacks.COMMONBACKPACK.get());
        this.tag(SimplyBackpacks.CURIOS_BACK).add(SimplyBackpacks.UNCOMMONBACKPACK.get());
        this.tag(SimplyBackpacks.CURIOS_BACK).add(SimplyBackpacks.RAREBACKPACK.get());
        this.tag(SimplyBackpacks.CURIOS_BACK).add(SimplyBackpacks.EPICBACKPACK.get());
        this.tag(SimplyBackpacks.CURIOS_BACK).add(SimplyBackpacks.ULTIMATEBACKPACK.get());
    }

    private void tagKrates() {
        TagOtherModsItem("krate","krate_small");
        TagOtherModsItem("krate","krate_basic");
        TagOtherModsItem("krate","krate_big");
        TagOtherModsItem("krate","krate_large");
    }

    private void TagOtherModsItem(String modid, String item) {
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation(modid, item));
    }

    private void tagShulkers() {
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.BLACK_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.BLUE_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.BROWN_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.CYAN_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.GRAY_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.GREEN_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.LIGHT_BLUE_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.LIGHT_GRAY_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.LIME_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.MAGENTA_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.ORANGE_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.PINK_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.PURPLE_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.RED_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.WHITE_SHULKER_BOX);
        this.tag(SimplyBackpacks.HOLDS_ITEMS).add(Items.YELLOW_SHULKER_BOX);
    }

    private void tagQuantumBags() {
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_white"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_orange"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_magenta"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_light_blue"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_yellow"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_lime"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_pink"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_gray"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_light_gray"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_cyan"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_purple"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_blue"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_brown"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_green"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_red"));
        this.tag(SimplyBackpacks.HOLDS_ITEMS).addOptional(new ResourceLocation("quantumstorage", "quantum_bag_black"));
    }

}
