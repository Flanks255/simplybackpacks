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
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
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
    public static final ITag.INamedTag<Item> HOLDS_ITEMS = ItemTags.makeWrapperTag(new ResourceLocation("forge", "holds_items").toString());
    public static final ITag.INamedTag<Item> CURIOS_BACK = ItemTags.makeWrapperTag(new ResourceLocation("curios", "back").toString());
    public static final ITag.INamedTag<Enchantment> SOULBOUND = ForgeTagHandler.makeWrapperTag(ForgeRegistries.ENCHANTMENTS, new ResourceLocation("forge", "soulbound"));


    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<IRecipeSerializer<?>> COPYRECIPE = RECIPES.register("backpack_upgrade", CopyBackpackDataRecipe.Serializer::new);
    public static final RegistryObject<ContainerType<SBContainer>> SBCONTAINER = CONTAINERS.register("sb_container", () -> IForgeContainerType.create(SBContainer::fromNetwork));
    public static final RegistryObject<ContainerType<FilterContainer>> FILTERCONTAINER = CONTAINERS.register("filter_container", () -> IForgeContainerType.create(FilterContainer::fromNetwork));

    public static final RegistryObject<Item> COMMONBACKPACK = ITEMS.register("commonbackpack", () -> new BackpackItem("commonbackpack", Backpack.COMMON));
    public static final RegistryObject<Item> UNCOMMONBACKPACK = ITEMS.register("uncommonbackpack", () -> new BackpackItem("uncommonbackpack", Backpack.UNCOMMON));
    public static final RegistryObject<Item> RAREBACKPACK = ITEMS.register("rarebackpack", () -> new BackpackItem("rarebackpack", Backpack.RARE));
    public static final RegistryObject<Item> EPICBACKPACK = ITEMS.register("epicbackpack", () -> new BackpackItem("epicbackpack", Backpack.EPIC));
    public static final RegistryObject<Item> ULTIMATEBACKPACK = ITEMS.register("ultimatebackpack", () -> new BackpackItem("ultimatebackpack", Backpack.ULTIMATE));

    private final NonNullList<KeyBinding> keyBinds = NonNullList.create();

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
        if (event.getPlayer().openContainer instanceof SBContainer || event.getPlayer().isSneaking() || event.getItem().getItem().getItem() instanceof BackpackItem)
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

        PlayerInventory playerInv = event.getPlayer().inventory;
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = playerInv.getStackInSlot(i);
            if (stack.getItem() instanceof BackpackItem && BackpackItem.pickupEvent(event, stack)) {
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }

    private void onClientTick(TickEvent.ClientTickEvent event) {
        if (keyBinds.get(0).isPressed())
            NETWORK.sendToServer(new ToggleMessage());
        if (keyBinds.get(1).isPressed())
            NETWORK.sendToServer(new OpenMessage());
    }

    private void clientStuff(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(SBCONTAINER.get(), SBGui::new);
        ScreenManager.registerFactory(FILTERCONTAINER.get(), FilterGui::new);

        keyBinds.add(0, new KeyBinding("key.simplybackpacks.backpackpickup.desc", -1, "key.simplybackpacks.category"));
        keyBinds.add(1, new KeyBinding("key.simplybackpacks.backpackopen.desc", -1, "key.simplybackpacks.category"));
        ClientRegistry.registerKeyBinding(keyBinds.get(0));
        ClientRegistry.registerKeyBinding(keyBinds.get(1));
    }

    private void onConfigReload(ModConfig.ModConfigEvent event) {
        ConfigCache.RefreshCache();
    }

}
