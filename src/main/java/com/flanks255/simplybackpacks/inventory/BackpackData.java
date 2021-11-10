package com.flanks255.simplybackpacks.inventory;

import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;
import java.util.UUID;

public class BackpackData {
    private final UUID uuid;
    private Backpack tier;
    private final SBItemHandler inventory;
    private final LazyOptional<IItemHandler> optional;
    public final Metadata meta = new Metadata();

    private FilterItemHandler filter = new FilterItemHandler();

    public LazyOptional<IItemHandler> getOptional() {
        return optional;
    }

    public IItemHandler getHandler() {
        return inventory;
    }

    public FilterItemHandler getFilter() {
        return filter;
    }

    public Backpack getTier() {
        return tier;
    }

    public void updateAccessRecords(String player, long time) {
        if (meta.firstAccessedTime == 0) {
            //new Backpack, set creation data
            meta.firstAccessedTime = time;
            meta.firstAccessedPlayer = player;
        }

        meta.setLastAccessedTime(time);
        meta.setLastAccessedPlayer(player);
    }

    public BackpackData(UUID uuid, Backpack tier) {
        this.uuid = uuid;
        this.tier = tier;

        inventory = new SBItemHandler(tier.slots);
        optional = LazyOptional.of(() -> inventory);
    }

    public BackpackData(UUID uuid, CompoundNBT incomingNBT) {
        this.uuid = uuid;
        this.tier = Backpack.values()[Math.min(incomingNBT.getInt("Tier"), Backpack.ULTIMATE.ordinal())];

        inventory = new SBItemHandler(tier.slots);
        inventory.deserializeNBT(incomingNBT.getCompound("Inventory"));
        filter = new FilterItemHandler();
        filter.deserializeNBT(incomingNBT.getCompound("Filter"));
        optional = LazyOptional.of(() -> inventory);

        if (incomingNBT.contains("Metadata"))
            meta.deserializeNBT(incomingNBT.getCompound("Metadata"));
    }

    public UUID getUuid() {
        return uuid;
    }

    public static Optional<BackpackData> fromNBT(CompoundNBT nbt) {
        if (nbt.contains("UUID")) {
            UUID uuid = nbt.getUUID("UUID");
            return Optional.of(new BackpackData(uuid, nbt));
        }
        return Optional.empty();
    }

    public void upgrade(Backpack newTier) {
        if (newTier.ordinal() > tier.ordinal()) {
            tier = newTier;
            inventory.upgrade(tier.slots);
        }
    }

    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putUUID("UUID", uuid);
        nbt.putInt("Tier", tier.ordinal());

        nbt.put("Inventory", inventory.serializeNBT());
        nbt.put("Filter", filter.serializeNBT());

        nbt.put("Metadata", meta.serializeNBT());

        return nbt;
    }



    public static class Metadata implements INBTSerializable<CompoundNBT> {
        private String firstAccessedPlayer = "";

        private long firstAccessedTime = 0;
        private String lastAccessedPlayer = "";
        private long lastAccessedTime = 0;
        public long getLastAccessedTime() {
            return lastAccessedTime;
        }

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        public String getLastAccessedPlayer() {
            return lastAccessedPlayer;
        }

        public void setLastAccessedPlayer(String lastAccessedPlayer) {
            this.lastAccessedPlayer = lastAccessedPlayer;
        }

        public long getFirstAccessedTime() {
            return firstAccessedTime;
        }

        public String getFirstAccessedPlayer() {
            return firstAccessedPlayer;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();

            nbt.putString("firstPlayer", firstAccessedPlayer);
            nbt.putLong("firstTime", firstAccessedTime);
            nbt.putString("lastPlayer", lastAccessedPlayer);
            nbt.putLong("lastTime", lastAccessedTime);

            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            firstAccessedPlayer = nbt.getString("firstPlayer");
            firstAccessedTime = nbt.getLong("firstTime");
            lastAccessedPlayer = nbt.getString("lastPlayer");
            lastAccessedTime = nbt.getLong("lastTime");
        }
    }
}
