package com.flanks255.simplybackpacks.save;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class BackpackManager extends WorldSavedData {
    private static final String NAME = SimplyBackpacks.MODID + "_backpack_data";

    private static final HashMap<UUID, BackpackData> data = new HashMap<>();

    public static BackpackManager clientCache = new BackpackManager();

    public BackpackManager() {
        super(NAME);
    }

    public static BackpackManager get() {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return ServerLifecycleHooks.getCurrentServer().getWorld(World.OVERWORLD).getSavedData().getOrCreate(BackpackManager::new, NAME);
        else
            return clientCache;
    }
    public Optional<BackpackData> getBackpack(UUID uuid) {
        if (data.containsKey(uuid))
            return Optional.of(data.get(uuid));
        return Optional.empty();
    }

    public BackpackData getOrCreateBackpack(UUID uuid, Backpack tier) {
        return data.computeIfAbsent(uuid, id -> {
            markDirty();
            return new BackpackData(id, tier);
        });
    }

    public LazyOptional<IItemHandler> getCapability(UUID uuid) {
        if (data.containsKey(uuid))
            return data.get(uuid).getOptional();

            return LazyOptional.empty();
    }

    @Override
    public void read(CompoundNBT nbt) {
        if (nbt.contains("Backpacks")) {
            ListNBT list = nbt.getList("Backpacks", Constants.NBT.TAG_COMPOUND);
            list.forEach((backpackNBT) -> BackpackData.fromNBT((CompoundNBT) backpackNBT).ifPresent((backpack) -> data.put(backpack.getUuid(), backpack)));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT backpacks = new ListNBT();
        data.forEach(((uuid, backpackData) -> backpacks.add(backpackData.toNBT())));
        compound.put("Backpacks", backpacks);
        return compound;
    }
}
