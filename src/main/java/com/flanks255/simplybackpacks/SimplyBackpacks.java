package com.flanks255.simplybackpacks;


import com.flanks255.simplybackpacks.commands.SBCommands;
import com.flanks255.simplybackpacks.configuration.CommonConfiguration;
import com.flanks255.simplybackpacks.configuration.ConfigCache;
import com.flanks255.simplybackpacks.crafting.CopyBackpackDataRecipe;
import com.flanks255.simplybackpacks.crafting.TargetNBTIngredient;
import com.flanks255.simplybackpacks.data.Generator;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.FilterGui;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.gui.SBGui;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.network.OpenMessage;
import com.flanks255.simplybackpacks.network.SBNetwork;
import com.flanks255.simplybackpacks.network.ToggleMessage;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import com.flanks255.simplybackpacks.util.RecipeUnlocker;
import com.flanks255.simplybackpacks.util.TagLookup;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod("simplybackpacks")
public class SimplyBackpacks {
    public static final String MODID = "simplybackpacks";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static SimpleChannel NETWORK;

    //forge:holds_items
    public static final TagKey<Item> HOLDS_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "holds_items"));
    //curios:back
    public static final TagKey<Item> CURIOS_BACK = TagKey.create(Registries.ITEM, new ResourceLocation("curios", "back"));
    //forge:soulbound
    public static final TagKey<Enchantment> SOULBOUND = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation("forge", "soulbound"));
    public static final TagLookup<Enchantment> SOULBOUND_LOOKUP = new TagLookup<>(ForgeRegistries.ENCHANTMENTS, SOULBOUND);


    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<RecipeSerializer<?>> COPYRECIPE = RECIPES.register("backpack_upgrade", CopyBackpackDataRecipe.Serializer::new);
    public static final RegistryObject<MenuType<SBContainer>> SBCONTAINER = CONTAINERS.register("sb_container", () -> IForgeMenuType.create(SBContainer::fromNetwork));
    public static final RegistryObject<MenuType<FilterContainer>> FILTERCONTAINER = CONTAINERS.register("filter_container", () -> IForgeMenuType.create(FilterContainer::fromNetwork));

    public static final RegistryObject<Item> COMMONBACKPACK = ITEMS.register("commonbackpack", () -> new BackpackItem("commonbackpack", Backpack.COMMON));
    public static final RegistryObject<Item> UNCOMMONBACKPACK = ITEMS.register("uncommonbackpack", () -> new BackpackItem("uncommonbackpack", Backpack.UNCOMMON));
    public static final RegistryObject<Item> RAREBACKPACK = ITEMS.register("rarebackpack", () -> new BackpackItem("rarebackpack", Backpack.RARE));
    public static final RegistryObject<Item> EPICBACKPACK = ITEMS.register("epicbackpack", () -> new BackpackItem("epicbackpack", Backpack.EPIC));
    public static final RegistryObject<Item> ULTIMATEBACKPACK = ITEMS.register("ultimatebackpack", () -> new BackpackItem("ultimatebackpack", Backpack.ULTIMATE));

    private final NonNullList<KeyMapping> keyBinds = NonNullList.create();

    public SimplyBackpacks() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        CONTAINERS.register(bus);
        RECIPES.register(bus);

        //Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfiguration.COMMON_CONFIG);
        bus.addListener(ConfigCache::listen);
        MinecraftForge.EVENT_BUS.addListener(SBCommands::listen);

        bus.addListener(this::setup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            bus.addListener(this::clientStuff);
            bus.addListener(this::registerKeyBinding);
            MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
            bus.addListener(this::creativeTabEvent);
        }
        bus.addListener(Generator::gatherData);
        bus.addListener(this::onEnqueueIMC);

        MinecraftForge.EVENT_BUS.addListener(this::pickupEvent);

        BackpackUtils.curiosLoaded = ModList.get().isLoaded("curios");
        RecipeUnlocker.register(MODID, MinecraftForge.EVENT_BUS, 2);
    }

    private void onEnqueueIMC(InterModEnqueueEvent event) {
        if (BackpackUtils.curiosLoaded) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
        }
    }


    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
            CraftingHelper.register(TargetNBTIngredient.Serializer.NAME, TargetNBTIngredient.SERIALIZER));
        NETWORK = SBNetwork.register();
    }

    private void pickupEvent(EntityItemPickupEvent event) {
        if (event.getEntity().containerMenu instanceof SBContainer || event.getEntity().isCrouching() || event.getItem().getItem().getItem() instanceof BackpackItem)
            return;

        if (BackpackUtils.curiosLoaded) {
            var curioslot = CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), BackpackItem::isBackpack);

            if (curioslot.isPresent()) {
                ItemStack stack = curioslot.get().stack();
                if (BackpackItem.pickupEvent(event, stack)) {
                    event.setResult(Event.Result.ALLOW);
                    return;
                }
            }
        }

        Inventory playerInv = event.getEntity().getInventory();
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = playerInv.getItem(i);
            if (stack.getItem() instanceof BackpackItem && BackpackItem.pickupEvent(event, stack)) {
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }

    private void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.keyBinds.get(0).consumeClick())
            NETWORK.sendToServer(new ToggleMessage());
        if (this.keyBinds.get(1).consumeClick())
            NETWORK.sendToServer(new OpenMessage());
    }

    private void registerKeyBinding(final RegisterKeyMappingsEvent event) {
        this.keyBinds.add(0, new KeyMapping("key.simplybackpacks.backpackpickup.desc", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, -1, "key.simplybackpacks.category"));
        this.keyBinds.add(1, new KeyMapping("key.simplybackpacks.backpackopen.desc", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, -1, "key.simplybackpacks.category"));
        event.register(this.keyBinds.get(0));
        event.register(this.keyBinds.get(1));
    }

    private void creativeTabEvent(final CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(COMMONBACKPACK.get());
            event.accept(UNCOMMONBACKPACK.get());
            event.accept(RAREBACKPACK.get());
            event.accept(EPICBACKPACK.get());
            event.accept(ULTIMATEBACKPACK.get());
        }
    }

    private void clientStuff(final FMLClientSetupEvent event) {
        MenuScreens.register(SBCONTAINER.get(), SBGui::new);
        MenuScreens.register(FILTERCONTAINER.get(), FilterGui::new);
    }
}
