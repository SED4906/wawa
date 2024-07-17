package org.sed4906.wawa;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;

public class BasicGenerator extends BlockEntity {
    public FuelItemHandler item_handler = new FuelItemHandler() {
        protected void onContentsChanged(int slot) {
            BasicGenerator.super.setChanged();
        }
    };

    public BasicGenerator(BlockPos pPos, BlockState pBlockState) {
        super(Wawa.BASIC_GENERATOR.get(), pPos, pBlockState);
    }

    private int ENERGY = 0;
    private int MAX_ENERGY = 256;

    public static final BlockCapability<IItemHandler, Void> ITEM_HANDLER_BLOCK =
            BlockCapability.createVoid(
                    // Provide a name to uniquely identify the capability.
                    ResourceLocation.fromNamespaceAndPath("wawa", "basic_generator_item_handler"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    IItemHandler.class);

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("energy", ENERGY);
        tag.put("fuel", item_handler.serializeNBT(registries));
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ENERGY = tag.getInt("energy");
        item_handler.deserializeNBT(registries, tag.getCompound("fuel"));
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BasicGenerator blockEntity) {
        if (check_overcharge(blockEntity)) return;
        ItemStack fuelStack = blockEntity.item_handler.extractItem(0,1,false);
        if (fuelStack.isEmpty()) return;
        fuel(blockEntity, fuelStack);
    }

    private static boolean check_overcharge(BasicGenerator blockEntity) {
        if (blockEntity.ENERGY >= blockEntity.MAX_ENERGY) {
            blockEntity.ENERGY = blockEntity.MAX_ENERGY;
            return true;
        }
        return false;
    }

    public static void fuel(BasicGenerator blockEntity, ItemStack with) {
        int amount = with.getBurnTime(null)/200;
        blockEntity.ENERGY += amount;
        check_overcharge(blockEntity);
        //Wawa.LOGGER.debug("energy increased by {} (now {})", amount, blockEntity.ENERGY);
        blockEntity.setChanged();
    }

    public int energy() {
        return this.ENERGY;
    }
    public int maxEnergy() {
        return this.MAX_ENERGY;
    }
}
