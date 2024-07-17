package org.sed4906.wawa;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID;

public class BasicGeneratorScreen extends AbstractContainerScreen<BasicGeneratorMenu> {
    public BasicGeneratorScreen(BasicGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    private static final ResourceLocation BACKGROUND_LOCATION = ResourceLocation.fromNamespaceAndPath(Wawa.MODID, "textures/gui/container/basic_generator.png");

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background is typically rendered first
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        // Render things here before widgets (background textures)

        // Then the widgets if this is a direct child of the Screen
        super.render(graphics, mouseX, mouseY, partialTick);

    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        // Assume we have some Component 'label'
        // 'label' is drawn at 'labelX' and 'labelY'
        graphics.drawString(this.font, String.format("Energy: %d/%d", this.menu.energyInfo.get(0), this.menu.energyInfo.get(1)), 8, 16, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BACKGROUND_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
