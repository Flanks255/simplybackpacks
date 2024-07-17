package com.flanks255.simplybackpacks.configuration;

import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collections;
import java.util.List;

public class CommonConfiguration {
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON_CONFIG;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_BLACKLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_WHITELIST;

    private static final List<String> DEFAULT_BLACKLIST = Collections.EMPTY_LIST;
    private static final List<String> DEFAULT_WHITELIST = Collections.EMPTY_LIST;


    static {
        COMMON_BUILDER.comment("Anti-Nesting").push("antinesting");
            ITEM_BLACKLIST = COMMON_BUILDER.comment("List of Resource Locations for items to be blocked").defineList("itemBlacklist", DEFAULT_BLACKLIST, obj -> obj instanceof String && BackpackUtils.isValidResourceLocation((String) obj));
            ITEM_WHITELIST = COMMON_BUILDER.comment("List of Resource Locations for items to be allowed despite matching other blocking checks.").defineList("itemWhitelist", DEFAULT_WHITELIST, obj -> obj instanceof String && BackpackUtils.isValidResourceLocation((String) obj));
        COMMON_BUILDER.pop();


        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
