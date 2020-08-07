package com.flanks255.simplybackpacks;


import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.FilterGui;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.gui.SBGui;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import com.flanks255.simplybackpacks.network.OpenMessage;
import com.flanks255.simplybackpacks.network.SBNetwork;
import com.flanks255.simplybackpacks.network.ToggleMessage;
import javafx.stage.Screen;
import net.java.games.input.Keyboard;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("simplybackpacks")
public class SimplyBackpacks {
    public static final String MODID = "simplybackpacks";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static SimpleChannel network;
    public static SBNetwork sbnetwork = new SBNetwork();

    public static final ItemBackpackBase commonBackpack = new ItemBackpackBase("commonbackpack", 18, Rarity.COMMON);
    public static final ItemBackpackBase uncommonBackpack = new ItemBackpackBase("uncommonbackpack", 33, Rarity.UNCOMMON);
    public static final ItemBackpackBase rareBackpack = new ItemBackpackBase("rarebackpack", 66, Rarity.RARE);
    public static final ItemBackpackBase epicBackpack = new ItemBackpackBase("epicbackpack", 99, Rarity.EPIC);

    private NonNullList<KeyBinding> keyBinds= NonNullList.create();

     public SimplyBackpacks() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientStuff);

        MinecraftForge.EVENT_BUS.addListener(this::pickupEvent);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
    }


    public void setup(final FMLCommonSetupEvent event) {
         network = sbnetwork.register();
    }

    private void pickupEvent(EntityItemPickupEvent event) {
        if (event.getPlayer().openContainer instanceof SBContainer || event.getPlayer().isSneaking() || event.getItem().getItem().getItem() instanceof ItemBackpackBase)
            return;
        PlayerInventory playerInv = event.getPlayer().inventory;
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = playerInv.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBackpackBase && ((ItemBackpackBase) stack.getItem()).pickupEvent(event, stack)) {
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }

    public static ItemStack findBackpack(PlayerEntity player) {
         if (player.getHeldItemMainhand().getItem() instanceof ItemBackpackBase)
             return player.getHeldItemMainhand();
         if (player.getHeldItemOffhand().getItem() instanceof ItemBackpackBase)
            return player.getHeldItemOffhand();

         PlayerInventory inventory = player.inventory;
         for (int i = 0; i <= 35; i++) {
             ItemStack stack = inventory.getStackInSlot(i);
             if (stack.getItem() instanceof  ItemBackpackBase)
                 return stack;
         }
         return ItemStack.EMPTY;
    }

    private void onClientTick(TickEvent.ClientTickEvent event) {
        if (keyBinds.get(0).isPressed())
            network.sendToServer(new ToggleMessage());
        if (keyBinds.get(1).isPressed())
            network.sendToServer(new OpenMessage());
    }

    private void clientStuff(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(SBContainer.type, SBGui::new);
        ScreenManager.registerFactory(FilterContainer.type, FilterGui::new);

        keyBinds.add(0, new KeyBinding("key.simplybackpacks.backpackpickup.desc", -1, "key.simplybackpacks.category"));
        keyBinds.add(1, new KeyBinding("key.simplybackpacks.backpackopen.desc", -1, "key.simplybackpacks.category"));
        ClientRegistry.registerKeyBinding(keyBinds.get(0));
        ClientRegistry.registerKeyBinding(keyBinds.get(1));
    }
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
                itemRegistryEvent.getRegistry().register(commonBackpack.setName());
                itemRegistryEvent.getRegistry().register(uncommonBackpack.setName());
                itemRegistryEvent.getRegistry().register(rareBackpack.setName());
                itemRegistryEvent.getRegistry().register(epicBackpack.setName());
        }
        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> containerRegistryEvent) {
            containerRegistryEvent.getRegistry().register(SBContainer.type);
            containerRegistryEvent.getRegistry().register(FilterContainer.type);
        }
        @SubscribeEvent
        public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
            event.getRegistry().register(new CopyBackpackDataRecipe.Serializer().setRegistryName(new ResourceLocation(MODID, "backpack_upgrade")));
        }
    }
}
