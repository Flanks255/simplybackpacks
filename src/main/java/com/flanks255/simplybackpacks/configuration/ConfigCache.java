package com.flanks255.simplybackpacks.configuration;


import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigCache {
    public static void RefreshCache() {
        BLACKLIST = CommonConfiguration.ITEM_BLACKLIST.get().stream().map(ResourceLocation::new).collect(Collectors.toList());
        WHITELIST = CommonConfiguration.ITEM_WHITELIST.get().stream().map(ResourceLocation::new).collect(Collectors.toList());
    }

    public static List<ResourceLocation> BLACKLIST;
    public static List<ResourceLocation> WHITELIST;
}
