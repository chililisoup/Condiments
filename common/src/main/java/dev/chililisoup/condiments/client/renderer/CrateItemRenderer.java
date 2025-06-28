package dev.chililisoup.condiments.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chililisoup.condiments.item.component.CrateContents;
import dev.chililisoup.condiments.reg.ModComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CrateItemRenderer extends ItemStackRenderer {
    public CrateItemRenderer() {
    }

    @Override
    public void renderByItem(ItemStack crateStack, ItemDisplayContext transform, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        Item item = crateStack.getItem();
        BlockState state = Block.byItem(item).defaultBlockState();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffer, light, overlay);

        CrateContents crateContents = crateStack.getOrDefault(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY);
        Optional<CrateContents.ItemRecord> itemRecord = crateContents.itemRecord();
        itemRecord.ifPresent(record -> {
            FrontAndTop fat = state.getValue(BlockStateProperties.ORIENTATION);
            Vec3i norm = fat.front().getNormal();
            CrateRenderer.renderItem(null, record.asItemStack(), poseStack, buffer, light, overlay, fat, norm, Minecraft.getInstance().getItemRenderer());
        });
    }
}
