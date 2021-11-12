package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SBNetwork {
    public static final ResourceLocation channelName = new ResourceLocation(SimplyBackpacks.MODID, "network");
    public static final String networkVersion = new ResourceLocation(SimplyBackpacks.MODID, "1").toString();

    public static SimpleChannel register() {
        final SimpleChannel network = NetworkRegistry.ChannelBuilder.named(channelName)
            .clientAcceptedVersions(version -> true)
            .serverAcceptedVersions(version -> true)
            .networkProtocolVersion(() -> networkVersion)
            .simpleChannel();

        network.registerMessage(1, ToggleMessage.class, ToggleMessage::encode, ToggleMessage::decode, ToggleMessage::handle);
        network.registerMessage(2, OpenMessage.class, OpenMessage::encode, OpenMessage::decode, OpenMessage::handle);
        network.registerMessage( 3, ToggleMessageMessage.class, ToggleMessageMessage::encode, ToggleMessageMessage::decode, ToggleMessageMessage::handle);
        network.registerMessage(4, FilterMessage.class, FilterMessage::encode, FilterMessage::decode, FilterMessage::handle);

        return network;
    }
}
