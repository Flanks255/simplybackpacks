package com.flanks255.simplybackpacks.inventory;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class BackpackManager extends SavedData {
    private static final String NAME = SimplyBackpacks.MODID + "_backpack_data";

    private static final HashMap<UUID, BackpackData> data = new HashMap<>();

    public static final BackpackManager blankClient = new BackpackManager();

    public HashMap<UUID, BackpackData> getMap() { return data; }

    public static BackpackManager get() {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new Factory<>(BackpackManager::new, BackpackManager::load), NAME);
        else
            return blankClient;
    }

    public Optional<BackpackData> getBackpack(UUID uuid) {
        if (data.containsKey(uuid))
            return Optional.of(data.get(uuid));
        return Optional.empty();
    }

    public BackpackData getOrCreateBackpack(UUID uuid, Backpack tier) {
        return data.computeIfAbsent(uuid, id -> {
            setDirty();
            return new BackpackData(id, tier);
        });
    }

    public void removeBackpack(UUID uuid) {
        getBackpack(uuid).ifPresent(backpack -> {
            data.remove(uuid);
            setDirty();
        });
    }

    public IItemHandler getCapability(UUID uuid) {
        if (data.containsKey(uuid))
            return data.get(uuid).getHandler();

        return null;
    }

    public IItemHandler getCapability(ItemStack stack) {
        if (stack.has(SimplyBackpacks.BACKPACK_UUID)) {
            UUID uuid = stack.get(SimplyBackpacks.BACKPACK_UUID);
            if (data.containsKey(uuid))
                return data.get(uuid).getHandler();
        }

        return null;
    }

    public static BackpackManager load(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        if (nbt.contains("Backpacks")) {
            ListTag list = nbt.getList("Backpacks", Tag.TAG_COMPOUND);
            list.forEach((backpackNBT) -> BackpackData.fromNBT((CompoundTag) backpackNBT, pRegistries).ifPresent((backpack) -> data.put(backpack.getUuid(), backpack)));
        }
        return new BackpackManager();
    }

    @Override
    @Nonnull
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider pRegistries) {
        ListTag backpacks = new ListTag();
        data.forEach(((uuid, backpackData) -> backpacks.add(backpackData.toNBT(pRegistries))));
        compound.put("Backpacks", backpacks);
        return compound;
    }
}
