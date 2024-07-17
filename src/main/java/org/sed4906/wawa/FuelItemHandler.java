package org.sed4906.wawa;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import static java.lang.Math.max;

public class FuelItemHandler extends ItemStackHandler {
    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return FurnaceBlockEntity.isFuel(stack);
    }
}
