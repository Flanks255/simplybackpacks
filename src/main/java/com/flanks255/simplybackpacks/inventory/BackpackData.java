package com.flanks255.simplybackpacks.inventory;

import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.nbt.CompoundTag;
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
        return this.optional;
    }

    public IItemHandler getHandler() {
        return this.inventory;
    }

    public FilterItemHandler getFilter() {
        return this.filter;
    }

    public Backpack getTier() {
        return this.tier;
    }

    public void updateAccessRecords(String player, long time) {
        if (this.meta.firstAccessedTime == 0) {
            //new Backpack, set creation data
            this.meta.firstAccessedTime = time;
            this.meta.firstAccessedPlayer = player;
        }

        this.meta.setLastAccessedTime(time);
        this.meta.setLastAccessedPlayer(player);
    }

    public BackpackData(UUID uuid, Backpack tier) {
        this.uuid = uuid;
        this.tier = tier;

        this.inventory = new SBItemHandler(tier.slots);
        this.optional = LazyOptional.of(() -> this.inventory);
    }

    public BackpackData(UUID uuid, CompoundTag incomingNBT) {
        this.uuid = uuid;
        this.tier = Backpack.values()[Math.min(incomingNBT.getInt("Tier"), Backpack.ULTIMATE.ordinal())];

        this.inventory = new SBItemHandler(this.tier.slots);
        this.inventory.deserializeNBT(incomingNBT.getCompound("Inventory"));
        this.filter = new FilterItemHandler();
        this.filter.deserializeNBT(incomingNBT.getCompound("Filter"));
        this.optional = LazyOptional.of(() -> this.inventory);

        if (incomingNBT.contains("Metadata"))
            this.meta.deserializeNBT(incomingNBT.getCompound("Metadata"));
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public static Optional<BackpackData> fromNBT(CompoundTag nbt) {
        if (nbt.contains("UUID")) {
            UUID uuid = nbt.getUUID("UUID");
            return Optional.of(new BackpackData(uuid, nbt));
        }
        return Optional.empty();
    }

    public void upgrade(Backpack newTier) {
        if (newTier.ordinal() > this.tier.ordinal()) {
            this.tier = newTier;
            this.inventory.upgrade(this.tier.slots);
        }
    }

    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putUUID("UUID", this.uuid);
        nbt.putInt("Tier", this.tier.ordinal());

        nbt.put("Inventory", this.inventory.serializeNBT());
        nbt.put("Filter", this.filter.serializeNBT());

        nbt.put("Metadata", this.meta.serializeNBT());

        return nbt;
    }



    public static class Metadata implements INBTSerializable<CompoundTag> {
        private String firstAccessedPlayer = "";

        private long firstAccessedTime = 0;
        private String lastAccessedPlayer = "";
        private long lastAccessedTime = 0;
        public long getLastAccessedTime() {
            return this.lastAccessedTime;
        }

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        public String getLastAccessedPlayer() {
            return this.lastAccessedPlayer;
        }

        public void setLastAccessedPlayer(String lastAccessedPlayer) {
            this.lastAccessedPlayer = lastAccessedPlayer;
        }

        public long getFirstAccessedTime() {
            return this.firstAccessedTime;
        }

        public String getFirstAccessedPlayer() {
            return this.firstAccessedPlayer;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();

            nbt.putString("firstPlayer", this.firstAccessedPlayer);
            nbt.putLong("firstTime", this.firstAccessedTime);
            nbt.putString("lastPlayer", this.lastAccessedPlayer);
            nbt.putLong("lastTime", this.lastAccessedTime);

            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.firstAccessedPlayer = nbt.getString("firstPlayer");
            this.firstAccessedTime = nbt.getLong("firstTime");
            this.lastAccessedPlayer = nbt.getString("lastPlayer");
            this.lastAccessedTime = nbt.getLong("lastTime");
        }
    }
}
