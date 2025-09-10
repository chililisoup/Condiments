package dev.chililisoup.condiments.client.renderer;

import dev.chililisoup.condiments.config.ClientConfig;
import dev.chililisoup.condiments.item.CrateItem;
import dev.chililisoup.condiments.block.entity.CrateContents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//? if >= 1.21
import net.minecraft.client.DeltaTracker;

//$ client_only
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CondimentsHud {
    public static void render(
            GuiGraphics guiGraphics,
            //? if < 1.21 {
            /*float delta
            *///?} else
            DeltaTracker deltaTracker
    ) {
        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().screen != null) return;

        if (!ClientConfig.SHOW_CRATE_HUD.get()) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) stack = player.getOffhandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof CrateItem)) return;

        CrateContents crateContents = CrateContents.fromCrateItem(stack);
        CrateContents.ItemRecord itemRecord = crateContents.itemRecord();
        if (itemRecord == null) return;

        ItemStack contentsStack = itemRecord.asItemStack();
        Font font = Minecraft.getInstance().font;
        String count = String.format("x%d", crateContents.count());

        double xAlign = ClientConfig.CRATE_HUD_X_ALIGNMENT.get();
        double yAlign = ClientConfig.CRATE_HUD_Y_ALIGNMENT.get();

        int width = font.width(count) + 17;

        int x = ClientConfig.CRATE_HUD_X_OFFSET.get() + 4 - (int) (xAlign * (width + 8)) + (int) (guiGraphics.guiWidth() * xAlign);
        int y = ClientConfig.CRATE_HUD_Y_OFFSET.get() + 4 - (int) (yAlign * 24) + (int) (guiGraphics.guiHeight() * yAlign);

        TooltipRenderUtil.renderTooltipBackground(guiGraphics, x, y, width, 16, 0);
        guiGraphics.renderItem(contentsStack, x, y);
        guiGraphics.renderItemDecorations(font, contentsStack, x, y);
        guiGraphics.drawString(font, count, x + 17, y + 7, 16777215, true);
    }
}
