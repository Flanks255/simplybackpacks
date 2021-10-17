package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class SBNetwork {
    public static final ResourceLocation channelName = new ResourceLocation(SimplyBackpacks.MODID, "network");
    public static final String networkVersion = new ResourceLocation(SimplyBackpacks.MODID, "1").toString();

    public static SimpleChannel register() {
        final SimpleChannel network = NetworkRegistry.ChannelBuilder.named(channelName)
                .clientAcceptedVersions(version -> true)
                .serverAcceptedVersions(version -> true)
                .networkProtocolVersion(() -> networkVersion)
                .simpleChannel();

        network.messageBuilder(ToggleMessage.class, 1)
                .decoder(ToggleMessage::decode)
                .encoder(ToggleMessage::encode)
                .consumer(ToggleMessage::handle)
                .add();
        network.messageBuilder(OpenMessage.class, 2)
                .decoder(OpenMessage::decode)
                .encoder(OpenMessage::encode)
                .consumer(OpenMessage::handle)
                .add();
        network.messageBuilder(ToggleMessageMessage.class, 3)
                .decoder(ToggleMessageMessage::decode)
                .encoder(ToggleMessageMessage::encode)
                .consumer(ToggleMessageMessage::handle)
                .add();
        network.messageBuilder(FilterMessage.class, 4)
                .decoder(FilterMessage::decode)
                .encoder(FilterMessage::encode)
                .consumer(FilterMessage::handle)
                .add();

        return network;
    }
}
