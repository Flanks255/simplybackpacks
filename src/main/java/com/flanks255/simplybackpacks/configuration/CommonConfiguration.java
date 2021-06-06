package com.flanks255.simplybackpacks.configuration;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.List;

public class CommonConfiguration {
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_BLACKLIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_WHITELIST;

    private static List<String> DEFAULT_BLACKLIST = Collections.EMPTY_LIST;
    private static List<String> DEFAULT_WHITELIST = Collections.EMPTY_LIST;


    static {
        COMMON_BUILDER.comment("Anti-Nesting").push("antinesting");
            ITEM_BLACKLIST = COMMON_BUILDER.comment("List of Resource Locations for items to be blocked").defineList("itemBlacklist", DEFAULT_BLACKLIST, obj -> obj instanceof String && ResourceLocation.isResouceNameValid((String) obj));
            ITEM_WHITELIST = COMMON_BUILDER.comment("List of Resource Locations for items to be allowed despite matching other blocking checks.").defineList("itemWhitelist", DEFAULT_WHITELIST, obj -> obj instanceof String && ResourceLocation.isResouceNameValid((String) obj));
        COMMON_BUILDER.pop();


        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
