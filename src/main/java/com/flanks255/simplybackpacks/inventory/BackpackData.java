package com.flanks255.simplybackpacks.inventory;

import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;
import java.util.UUID;

public class BackpackData {
    private final UUID uuid;
    private Backpack tier;
    private final SBItemHandler inventory;
    private final LazyOptional<IItemHandler> optional;

    public LazyOptional<IItemHandler> getOptional() {
        return optional;
    }

    public IItemHandler getHandler() {
        return inventory;
    }

    public Backpack getTier() {
        return tier;
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
        optional = LazyOptional.of(() -> inventory);
    }

    public UUID getUuid() {
        return uuid;
    }

    public static Optional<BackpackData> fromNBT(CompoundNBT nbt) {
        if (nbt.contains("UUID")) {
            UUID uuid = nbt.getUniqueId("UUID");
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

        nbt.putUniqueId("UUID", uuid);
        nbt.putInt("Tier", tier.ordinal());

        nbt.put("Inventory", inventory.serializeNBT());

        return nbt;
    }
}
