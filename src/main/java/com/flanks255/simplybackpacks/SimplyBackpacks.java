package com.flanks255.simplybackpacks;


import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.FilterGui;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.gui.SBGui;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.network.OpenMessage;
import com.flanks255.simplybackpacks.network.SBNetwork;
import com.flanks255.simplybackpacks.network.ToggleMessage;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.util.ICuriosHelper;

@Mod("simplybackpacks")
public class SimplyBackpacks {
    public static final String MODID = "simplybackpacks";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // Mod Deferred Registers, this should be moved if it continues to grow.
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<Item> COMMON_BACKPACK = ITEMS.register("commonbackpack", () -> new BackpackItem(Backpack.COMMON));
    public static final RegistryObject<Item> UNCOMMON_BACKPACK = ITEMS.register("uncommonbackpack", () -> new BackpackItem(Backpack.UNCOMMON));
    public static final RegistryObject<Item> RARE_BACKPACK = ITEMS.register("rarebackpack", () -> new BackpackItem(Backpack.RARE));
    public static final RegistryObject<Item> EPIC_BACKPACK = ITEMS.register("epicbackpack", () -> new BackpackItem(Backpack.EPIC));

    public static final RegistryObject<ContainerType<SBContainer>> BACKPACK_CONTAINER = CONTAINERS.register("sb_container", () -> IForgeContainerType.create(SBContainer::new));
    public static final RegistryObject<ContainerType<FilterContainer>> FILTER_CONTAINER = CONTAINERS.register("sb_filter_container", () -> IForgeContainerType.create(FilterContainer::new));

    private static final RegistryObject<IRecipeSerializer<CopyBackpackDataRecipe>> COPY_RECIPE = RECIPES.register("backpack_upgrade", CopyBackpackDataRecipe.Serializer::new);

    public static SimpleChannel network;
    public static SBNetwork sbnetwork = new SBNetwork();

    private final NonNullList<KeyBinding> keyBinds = NonNullList.create();

    public static boolean curiosLoaded;

    public SimplyBackpacks() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        CONTAINERS.register(bus);
        RECIPES.register(bus);

        bus.addListener(this::setup);
        bus.addListener(this::clientStuff);
        bus.addListener(this::onEnqueueIMC);

        MinecraftForge.EVENT_BUS.addListener(this::pickupEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);

        curiosLoaded = ModList.get().isLoaded("curios");
    }

    public void setup(final FMLCommonSetupEvent event) {
        network = sbnetwork.register();
    }

    private void pickupEvent(EntityItemPickupEvent event) {
        if (event.getPlayer().openContainer instanceof SBContainer || event.getPlayer().isSneaking() || BackpackItem.isBackpack(event.getItem().getItem()))
            return;

        // Use curios first if it can
        if (curiosLoaded) {
            boolean handled = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackItem::isBackpack, event.getPlayer()).map((data) -> {
                // A cast here should be safe...
                if (((BackpackItem) data.getRight().getItem()).pickupEvent(event.getItem().getItem(), data.getRight())) {
                    event.setResult(Event.Result.ALLOW);
                    return true;
                }

                return false;
            }).orElse(false);

            if (handled) {
                return;
            }
        }

        PlayerInventory playerInv = event.getPlayer().inventory;
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = playerInv.getStackInSlot(i);
            if (BackpackItem.isBackpack(stack) && ((BackpackItem) stack.getItem()).pickupEvent(event.getItem().getItem(), stack)) {
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }

    private void onClientTick(TickEvent.ClientTickEvent event) {
        if (keyBinds.get(0).isPressed())
            network.sendToServer(new ToggleMessage());

        if (keyBinds.get(1).isPressed())
            network.sendToServer(new OpenMessage());
    }

    private void clientStuff(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(BACKPACK_CONTAINER.get(), SBGui::new);
        ScreenManager.registerFactory(FILTER_CONTAINER.get(), FilterGui::new);

        keyBinds.add(0, new KeyBinding("key.simplybackpacks.backpackpickup.desc", -1, "key.simplybackpacks.category"));
        keyBinds.add(1, new KeyBinding("key.simplybackpacks.backpackopen.desc", -1, "key.simplybackpacks.category"));

        ClientRegistry.registerKeyBinding(keyBinds.get(0));
        ClientRegistry.registerKeyBinding(keyBinds.get(1));
    }

    private void onEnqueueIMC(InterModEnqueueEvent event) {
        if (curiosLoaded) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
        }
    }
}
