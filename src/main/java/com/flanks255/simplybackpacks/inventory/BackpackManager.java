package com.flanks255.simplybackpacks.inventory;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;

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
            return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(BackpackManager::load, BackpackManager::new, NAME);
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

    public LazyOptional<IItemHandler> getCapability(UUID uuid) {
        if (data.containsKey(uuid))
            return data.get(uuid).getOptional();

        return LazyOptional.empty();
    }

    public LazyOptional<IItemHandler> getCapability(ItemStack stack) {
        if (stack.getOrCreateTag().contains("UUID")) {
            UUID uuid = stack.getTag().getUUID("UUID");
            if (data.containsKey(uuid))
                return data.get(uuid).getOptional();
        }

        return LazyOptional.empty();
    }

    public static BackpackManager load(CompoundTag nbt) {
        if (nbt.contains("Backpacks")) {
            ListTag list = nbt.getList("Backpacks", Constants.NBT.TAG_COMPOUND);
            list.forEach((backpackNBT) -> BackpackData.fromNBT((CompoundTag) backpackNBT).ifPresent((backpack) -> data.put(backpack.getUuid(), backpack)));
        }
        return new BackpackManager();
    }

    @Override
    @Nonnull
    public CompoundTag save(CompoundTag compound) {
        ListTag backpacks = new ListTag();
        data.forEach(((uuid, backpackData) -> backpacks.add(backpackData.toNBT())));
        compound.put("Backpacks", backpacks);
        return compound;
    }
}
