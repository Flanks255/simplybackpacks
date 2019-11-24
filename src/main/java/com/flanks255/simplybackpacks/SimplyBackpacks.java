package com.flanks255.simplybackpacks;


import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("simplybackpacks")
public class SimplyBackpacks {
    private static final String MODID = "simplybackpacks";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    public static SimpleChannel network;


    public SimplyBackpacks() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientStuff);
    }


    public void setup(final FMLCommonSetupEvent event) {

    }

    private void clientStuff(final FMLClientSetupEvent event) {

    }

    public static class RegistryEvents {
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {

        }
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> containerRegistryEvent) {

        }
    }
}
