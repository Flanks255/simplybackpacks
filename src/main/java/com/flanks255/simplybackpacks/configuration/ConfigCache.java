package com.flanks255.simplybackpacks.configuration;


import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigCache {
    public static void RefreshCache() {
        BLACKLIST = CommonConfiguration.ITEM_BLACKLIST.get().stream().map(ResourceLocation::tryParse).collect(Collectors.toList());
        WHITELIST = CommonConfiguration.ITEM_WHITELIST.get().stream().map(ResourceLocation::tryParse).collect(Collectors.toList());
    }

    public static void listen(ModConfigEvent event) {
        RefreshCache();
    }

    public static List<ResourceLocation> BLACKLIST;
    public static List<ResourceLocation> WHITELIST;
}
