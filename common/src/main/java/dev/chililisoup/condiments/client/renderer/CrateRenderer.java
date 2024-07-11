package dev.chililisoup.condiments.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chililisoup.condiments.block.CrateBlock;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.joml.Quaternionf;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CrateRenderer implements BlockEntityRenderer<CrateBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final Font font;

    public CrateRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.font = context.getFont();
    }

    @Override
    public void render(CrateBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player == null) return;

        ItemStack item = blockEntity.findFirst();
        if (item.isEmpty()) return;

        double distanceSqr = player.getEyePosition(1).distanceToSqr(blockEntity.getBlockPos().getCenter());
        if (distanceSqr > 1024) return;

        Direction dir = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);
        Vec3i norm = dir.getNormal();
        int light = LevelRenderer.getLightColor(level, blockEntity.getBlockPos().relative(dir, 1));

        if (distanceSqr < 25) renderText(level, player, item, blockEntity, poseStack, buffer, dir, norm, light);
        renderItem(level, item, poseStack, buffer, packedOverlay, dir, norm, light);
    }

    private void renderText(Level level, Entity player, ItemStack item, CrateBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, Direction dir, Vec3i norm, int light) {
        BlockHitResult hitResult = CrateBlock.getHitResult(level, blockEntity.getBlockPos(), player);
        if (!hitResult.getBlockPos().equals(blockEntity.getBlockPos())) return;
        if (hitResult.getDirection() != dir) return;

        Optional<Vec2> hitPos = CrateBlock.getHitPosition(hitResult, dir);

        if (hitPos.isEmpty()) return;
        if (CrateBlock.isNotInBounds(hitPos.get())) return;

        String text = String.format("%d / %d", blockEntity.getCount(), item.getMaxStackSize() * 64);

        poseStack.pushPose();
        poseStack.translate(
                (((double) norm.getX()) / 2) + 0.5,
                (((double) norm.getY()) / 2) + 0.5,
                (((double) norm.getZ()) / 2) + 0.5
        );
        poseStack.mulPose(dir.getRotation());
        poseStack.mulPose(Direction.NORTH.getRotation());
        poseStack.scale(-0.01F, -0.01F, -0.01F);

        this.font.drawInBatch(text, (float)(-this.font.width(text) / 2), -48.0F, 16777215, true, poseStack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, light);

        poseStack.popPose();
    }

    private void renderItem(Level level, ItemStack item, PoseStack poseStack, MultiBufferSource buffer, int packedOverlay, Direction dir, Vec3i norm, int light) {
        boolean is3d = this.itemRenderer.getModel(item, level, null, 0).isGui3d();
        double offset = is3d ? 2.3 : 2.6;

        poseStack.pushPose();
        poseStack.translate(
                (((double) norm.getX()) / offset) + 0.5,
                (((double) norm.getY()) / offset) + 0.5,
                (((double) norm.getZ()) / offset) + 0.5
        );

        poseStack.mulPose(dir.getOpposite().getRotation());
        poseStack.mulPose(Direction.NORTH.getRotation());

        if (dir.getAxis() == Direction.Axis.Y) poseStack.rotateAround(new Quaternionf(0, 0, 1, 0), 0, 0, 0);

        if (is3d) poseStack.last().pose().scale(0.7F, 0.7F, 0.005F);
        else poseStack.scale(0.6F, 0.6F, 0.6F);

        this.itemRenderer.renderStatic(item, ItemDisplayContext.GUI, light, packedOverlay, poseStack, buffer, level, 0);

        poseStack.popPose();
    }
}
