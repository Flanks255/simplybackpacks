package com.flanks255.simplybackpacks.save;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackDataManager extends WorldSavedData {
    public BackpackDataManager() {
        super(NAME);
    }

    private static Map<UUID, BackpackData> data = new HashMap<>();
    public static String NAME = SimplyBackpacks.MODID + "_backpackdata";

    private static BackpackDataManager INSTANCE = null;

    public static BackpackDataManager get(World world) {
        if (INSTANCE == null) {
            ServerWorld serverWorld = world.getServer().func_241755_D_();
            return serverWorld.getSavedData().getOrCreate(BackpackDataManager::new, NAME);
        }
        else
            return INSTANCE;
    }

    @Nullable
    public BackpackData get(UUID uuid) {
        return data.getOrDefault(uuid, null);
    }

    public BackpackData create(UUID uuid, Backpack tier) {
        BackpackData backpack = new BackpackData(uuid, tier);

        data.put(uuid, backpack);
        return backpack;
    }

    public boolean exists(UUID uuid) {
        return data.containsKey(uuid);
    }

    @Override
    public void read(CompoundNBT nbt) {
                if (nbt.contains("Backpacks")) {
                    ListNBT backpacks = nbt.getList("Backpacks", Constants.NBT.TAG_COMPOUND);
                    backpacks.forEach(backpackNBT -> BackpackData.fromNBT((CompoundNBT) backpackNBT).ifPresent(backpack -> data.put(backpack.getID(), backpack)));
                }
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT backpacks = new ListNBT();

        data.forEach(((uuid, backpackData) -> backpacks.add(backpackData.toNBT())));
        compound.put("Backpacks", backpacks);

        return compound;
    }
}
