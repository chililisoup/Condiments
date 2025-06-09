package dev.chililisoup.condiments.item.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Environment(EnvType.CLIENT)
public class ClientCrateTooltip implements ClientTooltipComponent {
    private final ItemStack item;

    public ClientCrateTooltip(CrateTooltip crateTooltip) {
        this.item = crateTooltip.item();
    }

    @Override
    public int getHeight() {
        if (item.is(Items.AIR)) return 0;
        return 16;
    }

    @Override
    public int getWidth(Font font) {
        if (item.is(Items.AIR)) return 0;
        return 18 + font.width(this.item.getHoverName());
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        if (item.is(Items.AIR)) return;

        guiGraphics.renderItem(this.item, x, y - 2);
        guiGraphics.renderItemDecorations(font, this.item, x, y - 2);
        guiGraphics.drawString(font, this.item.getHoverName(), x + 18, y + 2, 16777215, true);
    }
}
