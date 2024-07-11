package dev.chililisoup.condiments.item.Tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record CrateTooltip(ItemStack item) implements TooltipComponent {}
