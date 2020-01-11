package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SBNetwork {
    public ResourceLocation channelName;
    public String networkVersion;

    private SimpleChannel network;

    public SimpleChannel register() {
        channelName = new ResourceLocation(SimplyBackpacks.MODID, "network");
        networkVersion = new ResourceLocation(SimplyBackpacks.MODID, "1").toString();

        network = NetworkRegistry.ChannelBuilder.named(channelName)
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

        return network;
    }

    public SimpleChannel getNetworkChannel() {
        return network;
    }
}
