package dev.chililisoup.condiments.client.renderer;

import dev.chililisoup.condiments.item.CrateItem;
import dev.chililisoup.condiments.item.component.CrateContents;
import dev.chililisoup.condiments.reg.ModComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CondimentsHud {
    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().screen != null) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) stack = player.getOffhandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof CrateItem)) return;

        CrateContents crateContents = stack.getOrDefault(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY);
        Optional<CrateContents.ItemRecord> itemRecord = crateContents.itemRecord();
        if (itemRecord.isEmpty()) return;

        ItemStack contentsStack = itemRecord.get().asItemStack();
        Font font = Minecraft.getInstance().font;
        String count = String.format("x%d", crateContents.count());

        int x = 5;
        int y = guiGraphics.guiHeight() - 21;

        TooltipRenderUtil.renderTooltipBackground(guiGraphics, x, y, font.width(count) + 17, 16, 0);
        guiGraphics.renderItem(contentsStack, x, y);
        guiGraphics.renderItemDecorations(font, contentsStack, x, y);
        guiGraphics.drawString(font, count, x + 17, y + 7, 16777215, true);
    }
}
