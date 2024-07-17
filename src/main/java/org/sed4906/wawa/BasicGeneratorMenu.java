package org.sed4906.wawa;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class BasicGeneratorMenu extends AbstractContainerMenu {
    private ContainerLevelAccess access;

    public ContainerData energyInfo;

    protected BasicGeneratorMenu(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    // Client menu constructor
    public BasicGeneratorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new FuelItemHandler(), new SimpleContainerData(2), ContainerLevelAccess.NULL);
    }

    public BasicGeneratorMenu(int containerId, Inventory playerInventory, BasicGenerator be) {
        this(containerId, playerInventory, be.item_handler, new ContainerData() {
            @Override
            public int get(int pIndex) {
                if (pIndex == 0) {
                    return be.energy();
                }
                else {
                    return be.maxEnergy();
                }
            }

            @Override
            public void set(int pIndex, int pValue) {

            }

            @Override
            public int getCount() {
                return 2;
            }
        }, ContainerLevelAccess.NULL);
    }

    // Server menu constructor
    public BasicGeneratorMenu(int containerId, Inventory playerInventory, IItemHandler dataInventory, ContainerData energyInfo, ContainerLevelAccess access) {
        super(Wawa.BASIC_GENERATOR_MENU.get(), containerId);
        // Check if the data inventory size is some fixed value
        // Then, add slots for data inventory

        this.addSlot(new SlotItemHandler(dataInventory, 0, 8 + 4 * 18, 35));

        // Add slots for player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.energyInfo = energyInfo;

        // Add data slots for handled integers
        this.addDataSlots(this.energyInfo);

        this.access = access;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack quickMovedStack = ItemStack.EMPTY;
        Slot quickMovedSlot = this.slots.get(pIndex);
        if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
            ItemStack rawStack = quickMovedSlot.getItem();
            quickMovedStack = rawStack.copy();
            if (pIndex == 0) {
                // Try to move the result slot into the player inventory/hotbar
                if (!this.moveItemStackTo(rawStack, 1, 37, true)) {
                    // If cannot move, no longer quick move
                    return ItemStack.EMPTY;
                }

                // Perform logic on result slot quick move
                quickMovedSlot.onQuickCraft(rawStack, quickMovedStack);
            }
            else if (pIndex >= 1 && pIndex < 37) {
                // Try to move the inventory/hotbar slot into the data inventory input slots
                if (!this.moveItemStackTo(rawStack, 0, 1, false)) {
                    // If cannot move and in player inventory slot, try to move to hotbar
                    if (pIndex < 32) {
                        if (!this.moveItemStackTo(rawStack, 28, 37, false)) {
                            // If cannot move, no longer quick move
                            return ItemStack.EMPTY;
                        }
                    }
                    // Else try to move hotbar into player inventory slot
                    else if (!this.moveItemStackTo(rawStack, 1, 28, false)) {
                        // If cannot move, no longer quick move
                        return ItemStack.EMPTY;
                    }
                }
            }
            if (rawStack.isEmpty()) {
                // If the raw stack has completely moved out of the slot, set the slot to the empty stack
                quickMovedSlot.set(ItemStack.EMPTY);
            } else {
                // Otherwise, notify the slot that that the stack count has changed
                quickMovedSlot.setChanged();
            }

            /*
            The following if statement and Slot#onTake call can be removed if the
            menu does not represent a container that can transform stacks (e.g.
            chests).
            */
            if (rawStack.getCount() == quickMovedStack.getCount()) {
                // If the raw stack was not able to be moved to another slot, no longer quick move
                return ItemStack.EMPTY;
            }
            // Execute logic on what to do post move with the remaining stack
            quickMovedSlot.onTake(pPlayer, rawStack);
        }
        return quickMovedStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return AbstractContainerMenu.stillValid(this.access, pPlayer, Wawa.BASIC_GENERATOR_BLOCK.get());
    }
}
