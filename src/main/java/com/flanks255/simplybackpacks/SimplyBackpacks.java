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
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.ImmutableTriple;
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
    public static final Tag.Named<Item> HOLDS_ITEMS = ItemTags.bind(new ResourceLocation("forge", "holds_items").toString());
    //curios:back
    public static final Tag.Named<Item> CURIOS_BACK = ItemTags.bind(new ResourceLocation("curios", "back").toString());
    //forge:soulbound
    public static final Tag.Named<Enchantment> SOULBOUND = ForgeTagHandler.makeWrapperTag(ForgeRegistries.ENCHANTMENTS, new ResourceLocation("forge", "soulbound"));


    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
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
        bus.addListener(this::onConfigReload);

        MinecraftForge.EVENT_BUS.addListener(this::onCommandsRegister);

        bus.addListener(this::setup);
        bus.addListener(this::clientStuff);
        bus.addListener(Generator::gatherData);
        bus.addListener(this::onEnqueueIMC);

        MinecraftForge.EVENT_BUS.addListener(this::pickupEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);

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

    private void onCommandsRegister(RegisterCommandsEvent event) {
        SBCommands.register(event.getDispatcher());
    }

    private void pickupEvent(EntityItemPickupEvent event) {
        if (event.getPlayer().containerMenu instanceof SBContainer || event.getPlayer().isCrouching() || event.getItem().getItem().getItem() instanceof BackpackItem)
            return;

        if (BackpackUtils.curiosLoaded) {
            ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackItem::isBackpack, event.getEntityLiving())
                .map(ImmutableTriple::getRight).orElse(ItemStack.EMPTY);

            if (!stack.isEmpty()) {
                if (BackpackItem.pickupEvent(event, stack)) {
                    event.setResult(Event.Result.ALLOW);
                    return;
                }
            }
        }

        Inventory playerInv = event.getPlayer().getInventory();
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

    private void clientStuff(final FMLClientSetupEvent event) {
        MenuScreens.register(SBCONTAINER.get(), SBGui::new);
        MenuScreens.register(FILTERCONTAINER.get(), FilterGui::new);

        this.keyBinds.add(0, new KeyMapping("key.simplybackpacks.backpackpickup.desc", -1, "key.simplybackpacks.category"));
        this.keyBinds.add(1, new KeyMapping("key.simplybackpacks.backpackopen.desc", -1, "key.simplybackpacks.category"));
        ClientRegistry.registerKeyBinding(this.keyBinds.get(0));
        ClientRegistry.registerKeyBinding(this.keyBinds.get(1));
    }

    private void onConfigReload(ModConfigEvent event) {
        ConfigCache.RefreshCache();
    }

}
