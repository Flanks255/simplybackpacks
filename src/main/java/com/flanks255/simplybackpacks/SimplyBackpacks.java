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
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.network.HotkeyPacket;
import com.flanks255.simplybackpacks.network.SBNetwork;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import com.flanks255.simplybackpacks.util.RecipeUnlocker;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(SimplyBackpacks.MODID)
public class SimplyBackpacks {
    public static final String MODID = "simplybackpacks";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    //forge:holds_items
    public static final TagKey<Item> HOLDS_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "holds_items"));
    //curios:back
    public static final TagKey<Item> CURIOS_BACK = TagKey.create(Registries.ITEM, new ResourceLocation("curios", "back"));
    //forge:soulbound
    public static final TagKey<Enchantment> SOULBOUND = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation("forge", "soulbound"));
    //public static final TagLookup<Enchantment> SOULBOUND_LOOKUP = new TagLookup<>(BuiltInRegistries.ENCHANTMENT, SOULBOUND);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<IngredientType<?>> INGREDIENTS = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, SimplyBackpacks.MODID);
    public static final DeferredHolder<IngredientType<?>, IngredientType<TargetNBTIngredient>> TARGET_INGREDIENT = INGREDIENTS.register("nbt_target", () -> new IngredientType<>(TargetNBTIngredient.CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COPYRECIPE = RECIPES.register("backpack_upgrade", CopyBackpackDataRecipe.Serializer::new);
    public static final DeferredHolder<MenuType<?>, MenuType<SBContainer>> SBCONTAINER = CONTAINERS.register("sb_container", () -> IMenuTypeExtension.create(SBContainer::fromNetwork));
    public static final DeferredHolder<MenuType<?>, MenuType<FilterContainer>> FILTERCONTAINER = CONTAINERS.register("filter_container", () -> IMenuTypeExtension.create(FilterContainer::fromNetwork));

    public static final DeferredItem<Item> COMMONBACKPACK = ITEMS.register("commonbackpack", () -> new BackpackItem("commonbackpack", Backpack.COMMON));
    public static final DeferredItem<Item> UNCOMMONBACKPACK = ITEMS.register("uncommonbackpack", () -> new BackpackItem("uncommonbackpack", Backpack.UNCOMMON));
    public static final DeferredItem<Item> RAREBACKPACK = ITEMS.register("rarebackpack", () -> new BackpackItem("rarebackpack", Backpack.RARE));
    public static final DeferredItem<Item> EPICBACKPACK = ITEMS.register("epicbackpack", () -> new BackpackItem("epicbackpack", Backpack.EPIC));
    public static final DeferredItem<Item> ULTIMATEBACKPACK = ITEMS.register("ultimatebackpack", () -> new BackpackItem("ultimatebackpack", Backpack.ULTIMATE));
    private final NonNullList<KeyMapping> keyBinds = NonNullList.create();

    public SimplyBackpacks(IEventBus bus) {
        IEventBus neoBus = NeoForge.EVENT_BUS;
        ITEMS.register(bus);
        CONTAINERS.register(bus);
        RECIPES.register(bus);
        INGREDIENTS.register(bus);

        //Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfiguration.COMMON_CONFIG);
        bus.addListener(ConfigCache::listen);
        neoBus.addListener(SBCommands::listen);
        bus.addListener(this::registerCaps);
        bus.addListener(SBNetwork::register);

        bus.addListener(this::setup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            bus.addListener(this::menuScreenEvent);
            bus.addListener(this::registerKeyBinding);
            neoBus.addListener(this::onClientTick);
            bus.addListener(this::creativeTabEvent);
        }
        bus.addListener(Generator::gatherData);

        neoBus.addListener(this::pickupEvent);

        BackpackUtils.curiosLoaded = ModList.get().isLoaded("curios");
        RecipeUnlocker.register(MODID, neoBus, 2);
    }

    public void setup(final FMLCommonSetupEvent event) {
    }

    private void pickupEvent(EntityItemPickupEvent event) {
        if (event.getEntity().containerMenu instanceof SBContainer || event.getEntity().isCrouching() || event.getItem().getItem().getItem() instanceof BackpackItem)
            return;

        if (BackpackUtils.curiosLoaded) {
            var curiosInventory = CuriosApi.getCuriosInventory(event.getEntity());
            if (curiosInventory.isPresent()) {
                var slotResult = curiosInventory.get().findFirstCurio(BackpackItem::isBackpack);
                if (slotResult.isPresent()) {
                    ItemStack stack = slotResult.get().stack();
                    if (BackpackItem.pickupEvent(event, stack)) {
                        event.setResult(Event.Result.ALLOW);
                        return;
                    }
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
            PacketDistributor.SERVER.noArg().send(new HotkeyPacket(HotkeyPacket.HotKey.TOGGLE));
        if (this.keyBinds.get(1).consumeClick())
            PacketDistributor.SERVER.noArg().send(new HotkeyPacket(HotkeyPacket.HotKey.OPEN));
    }

    private void registerKeyBinding(final RegisterKeyMappingsEvent event) {
        this.keyBinds.add(0, new KeyMapping("key.simplybackpacks.backpackpickup.desc", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, -1, "key.simplybackpacks.category"));
        this.keyBinds.add(1, new KeyMapping("key.simplybackpacks.backpackopen.desc", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, -1, "key.simplybackpacks.category"));
        event.register(this.keyBinds.get(0));
        event.register(this.keyBinds.get(1));
    }

    private void creativeTabEvent(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().compareTo(CreativeModeTabs.TOOLS_AND_UTILITIES) == 0) {
            event.accept(COMMONBACKPACK.get());
            event.accept(UNCOMMONBACKPACK.get());
            event.accept(RAREBACKPACK.get());
            event.accept(EPICBACKPACK.get());
            event.accept(ULTIMATEBACKPACK.get());
        }
    }

    private void menuScreenEvent(final RegisterMenuScreensEvent event) {
        event.register(SBCONTAINER.get(), SBGui::new);
        event.register(FILTERCONTAINER.get(), FilterGui::new);
    }

    private void registerCaps(final RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> BackpackManager.get().getCapability(stack)
                , COMMONBACKPACK, UNCOMMONBACKPACK, RAREBACKPACK, EPICBACKPACK, ULTIMATEBACKPACK);
    }
}
