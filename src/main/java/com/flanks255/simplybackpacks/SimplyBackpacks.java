package com.flanks255.simplybackpacks;


import com.flanks255.simplybackpacks.configuration.CommonConfiguration;
import com.flanks255.simplybackpacks.configuration.ConfigCache;
import com.flanks255.simplybackpacks.crafting.CopyBackpackDataRecipe;
import com.flanks255.simplybackpacks.crafting.TargetNBTIngredient;
import com.flanks255.simplybackpacks.data.Generator;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.FilterGui;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.gui.SBGui;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import com.flanks255.simplybackpacks.network.OpenMessage;
import com.flanks255.simplybackpacks.network.SBNetwork;
import com.flanks255.simplybackpacks.network.ToggleMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("simplybackpacks")
public class SimplyBackpacks {
    public static final String MODID = "simplybackpacks";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static SimpleChannel NETWORK;

    //forge:holds_items
    public static final Tag.Named<Item> HOLDS_ITEMS = ItemTags.bind(new ResourceLocation("forge", "holds_items").toString());
    //storagedrawers:drawers
    public static final Tag.Named<Item> STORAGEDRAWERS = ItemTags.createOptional(new ResourceLocation("storagedrawers", "drawers"));
    public static final Tag.Named<Enchantment> SOULBOUND = ForgeTagHandler.makeWrapperTag(ForgeRegistries.ENCHANTMENTS, new ResourceLocation("forge", "soulbound"));


    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    //public static final RegistryObject<Item> BACKPACKITEM = ITEMS.register("backpack", BackpackItem::new);

    public static final RegistryObject<RecipeSerializer<?>> COPYRECIPE = RECIPES.register("backpack_upgrade", CopyBackpackDataRecipe.Serializer::new);
    public static final RegistryObject<MenuType<SBContainer>> SBCONTAINER = CONTAINERS.register("sb_container", () -> IForgeContainerType.create(SBContainer::new));
    public static final RegistryObject<MenuType<FilterContainer>> FILTERCONTAINER = CONTAINERS.register("filter_container", () -> IForgeContainerType.create(FilterContainer::new));

    public static final RegistryObject<Item> COMMONBACKPACK = ITEMS.register("commonbackpack", () -> new ItemBackpackBase("commonbackpack", 18, Rarity.COMMON));
    public static final RegistryObject<Item> UNCOMMONBACKPACK = ITEMS.register("uncommonbackpack", () -> new ItemBackpackBase("uncommonbackpack", 33, Rarity.UNCOMMON));
    public static final RegistryObject<Item> RAREBACKPACK = ITEMS.register("rarebackpack", () -> new ItemBackpackBase("rarebackpack", 66, Rarity.RARE));
    public static final RegistryObject<Item> EPICBACKPACK = ITEMS.register("epicbackpack", () -> new ItemBackpackBase("epicbackpack", 99, Rarity.EPIC));

    private final NonNullList<KeyMapping> keyBinds = NonNullList.create();

     public SimplyBackpacks() {
         IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
         ITEMS.register(bus);
         CONTAINERS.register(bus);
         RECIPES.register(bus);

         //Configs
         ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfiguration.COMMON_CONFIG);
         bus.addListener(this::onConfigReload);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientStuff);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(Generator::gatherData);

        MinecraftForge.EVENT_BUS.addListener(this::pickupEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
    }


    public void setup(final FMLCommonSetupEvent event) {
         event.enqueueWork(() ->
             CraftingHelper.register(TargetNBTIngredient.Serializer.NAME, TargetNBTIngredient.SERIALIZER));
         NETWORK = SBNetwork.register();
    }

    private void pickupEvent(EntityItemPickupEvent event) {
        if (event.getPlayer().containerMenu instanceof SBContainer || event.getPlayer().isShiftKeyDown() || event.getItem().getItem().getItem() instanceof ItemBackpackBase)
            return;
        Inventory playerInv = event.getPlayer().getInventory();
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = playerInv.getItem(i);
            if (stack.getItem() instanceof ItemBackpackBase && ((ItemBackpackBase) stack.getItem()).pickupEvent(event, stack)) {
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }

    public static ItemStack findBackpack(Player player) {
         if (player.getMainHandItem().getItem() instanceof ItemBackpackBase)
             return player.getMainHandItem();
         if (player.getOffhandItem().getItem() instanceof ItemBackpackBase)
            return player.getOffhandItem();

         Inventory inventory = player.getInventory();
         for (int i = 0; i <= 35; i++) {
             ItemStack stack = inventory.getItem(i);
             if (stack.getItem() instanceof  ItemBackpackBase)
                 return stack;
         }
         return ItemStack.EMPTY;
    }

    private void onClientTick(TickEvent.ClientTickEvent event) {
        if (keyBinds.get(0).consumeClick())
            NETWORK.sendToServer(new ToggleMessage());
        if (keyBinds.get(1).consumeClick())
            NETWORK.sendToServer(new OpenMessage());
    }

    private void clientStuff(final FMLClientSetupEvent event) {
        MenuScreens.register(SBCONTAINER.get(), SBGui::new);
        MenuScreens.register(FILTERCONTAINER.get(), FilterGui::new);

        keyBinds.add(0, new KeyMapping("key.simplybackpacks.backpackpickup.desc", -1, "key.simplybackpacks.category"));
        keyBinds.add(1, new KeyMapping("key.simplybackpacks.backpackopen.desc", -1, "key.simplybackpacks.category"));
        ClientRegistry.registerKeyBinding(keyBinds.get(0));
        ClientRegistry.registerKeyBinding(keyBinds.get(1));
/*
        ItemModelsProperties.registerProperty(BACKPACKITEM.get(), new ResourceLocation(MODID, "tier"),
                (stack, world, entity) -> stack.getOrCreateTag().getInt("tier")
                );*/
    }

    private void onConfigReload(ModConfigEvent event) {
        ConfigCache.RefreshCache();
    }

    public static boolean filterItem(ItemStack stack) {
         //check for backpacks
        if (stack.getItem() instanceof ItemBackpackBase)
            return false;

        //check the config whitelist, overrides all checks further.
        if (ConfigCache.WHITELIST.contains(stack.getItem().getRegistryName()))
            return true;

        //check for common storage tags.
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("Items") || tag.contains("Inventory"))
                return false;
        }

        //check for forge:holds_items / storagedrawers:drawers
        if (stack.is(HOLDS_ITEMS) || stack.is(STORAGEDRAWERS))
            return false;

        // if all else fails, check the config blacklist
        return !ConfigCache.BLACKLIST.contains(stack.getItem().getRegistryName());
    }
}
