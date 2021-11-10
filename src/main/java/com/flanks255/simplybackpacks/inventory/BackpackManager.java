package com.flanks255.simplybackpacks.inventory;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class BackpackManager extends WorldSavedData {
    private static final String NAME = SimplyBackpacks.MODID + "_backpack_data";

    private static final HashMap<UUID, BackpackData> data = new HashMap<>();

    public static final BackpackManager blankClient = new BackpackManager();

    public BackpackManager() {
        super(NAME);
    }

    public HashMap<UUID, BackpackData> getMap() { return data; }

    public static BackpackManager get() {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage().computeIfAbsent(BackpackManager::new, NAME);
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

    @Override
    public void load(CompoundNBT nbt) {
        if (nbt.contains("Backpacks")) {
            ListNBT list = nbt.getList("Backpacks", Constants.NBT.TAG_COMPOUND);
            list.forEach((backpackNBT) -> BackpackData.fromNBT((CompoundNBT) backpackNBT).ifPresent((backpack) -> data.put(backpack.getUuid(), backpack)));
        }
    }

    @Override
    @Nonnull
    public CompoundNBT save(CompoundNBT compound) {
        ListNBT backpacks = new ListNBT();
        data.forEach(((uuid, backpackData) -> backpacks.add(backpackData.toNBT())));
        compound.put("Backpacks", backpacks);
        return compound;
    }
}
