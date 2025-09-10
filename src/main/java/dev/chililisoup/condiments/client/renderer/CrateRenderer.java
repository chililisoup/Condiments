package dev.chililisoup.condiments.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chililisoup.condiments.block.CrateBlock;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

//? if fabric && < 1.21 {
/*import dev.chililisoup.condiments.CondimentsClient;
import dev.chililisoup.condiments.compat.create.client.CreateRenderHelper;
*///?}

import java.awt.*;
import java.util.List;
import java.util.Optional;

//$ client_only
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
//? if forgeLike || < 1.21
/*@javax.annotation.ParametersAreNonnullByDefault*/
public class CrateRenderer implements BlockEntityRenderer<CrateBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final Font font;

    public CrateRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.font = context.getFont();
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public void render(CrateBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player == null) return;

        ItemStack item = blockEntity.getItemType();
        if (item.isEmpty() && !blockEntity.hasCustomName()) return;

        FrontAndTop fat = blockEntity.getBlockState().getValue(BlockStateProperties.ORIENTATION);
        Vec3i norm = fat.front().getNormal();
        //? if fabric && < 1.21 {
        /*int light = CondimentsClient.CREATE_LOADED && CreateRenderHelper.isLevelVirtual(level) ?
                packedLight :
                LevelRenderer.getLightColor(level, blockEntity.getBlockPos().relative(fat.front(), 1));
        *///?} else
        int light = LevelRenderer.getLightColor(level, blockEntity.getBlockPos().relative(fat.front(), 1));

        renderText(item, blockEntity, poseStack, buffer, fat, norm, light);
        if (!item.isEmpty()) renderItem(level, item, poseStack, buffer, light, packedOverlay, fat, norm, this.itemRenderer);
    }

    private void renderText(ItemStack item, CrateBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, FrontAndTop fat, Vec3i norm, int light) {
        if (!(Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult)) return;
        if (hitResult.getType() == HitResult.Type.MISS) return;
        if (!hitResult.getBlockPos().equals(blockEntity.getBlockPos())) return;
        if (hitResult.getDirection() != fat.front()) return;

        Optional<Vec2> hitPos = CrateBlock.getHitPosition(hitResult, fat.front());

        if (hitPos.isEmpty()) return;
        if (CrateBlock.isNotInBounds(hitPos.get())) return;

        String text = String.format("%d / %d", blockEntity.getCount(), item.getMaxStackSize() * CommonConfig.CRATE_MAX_CONTAINED_STACKS.get());

        poseStack.pushPose();
        poseStack.translate(
                (((double) norm.getX()) / 2) + 0.5,
                (((double) norm.getY()) / 2) + 0.5,
                (((double) norm.getZ()) / 2) + 0.5
        );

        poseStack.mulPose(fat.front().getRotation());
        poseStack.mulPose(Direction.NORTH.getRotation());

        if (fat.front().getAxis() == Direction.Axis.Y) {
            int dir = fat.front().getAxisDirection().getStep();
            int rot = (dir + 1) * 90;
            poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(0, 0, dir, fat.top().toYRot() + rot));
        }

        poseStack.scale(-0.01F, -0.01F, -0.01F);

        if (!item.isEmpty()) {
            this.font.drawInBatch(
                    text,
                    (float) (-this.font.width(text) / 2),
                    -48.0F,
                    16777215,
                    true,
                    poseStack.last().pose(),
                    buffer,
                    Font.DisplayMode.POLYGON_OFFSET,
                    0,
                    light
            );
        }

        Component customName = blockEntity.getCustomName();
        if (customName != null) {
            List<FormattedCharSequence> list = this.font.split(customName, 100);
            FormattedCharSequence clampedWidthName = list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
            this.font.drawInBatch(
                    clampedWidthName,
                    (float) (-this.font.width(clampedWidthName) / 2),
                    40.0F,
                    16777215,
                    true,
                    poseStack.last().pose(),
                    buffer,
                    Font.DisplayMode.POLYGON_OFFSET,
                    0,
                    light
            );
        }

        poseStack.popPose();
    }

    public static void renderItem(@Nullable Level level, ItemStack item, PoseStack poseStack, MultiBufferSource buffer, int light, int packedOverlay, FrontAndTop fat, Vec3i norm, ItemRenderer itemRenderer) {
        boolean is3d = itemRenderer.getModel(item, level, null, 0).isGui3d();
        double offset = is3d ? 2.3 : 2.6;

        poseStack.pushPose();
        poseStack.translate(
                (((double) norm.getX()) / offset) + 0.5,
                (((double) norm.getY()) / offset) + 0.5,
                (((double) norm.getZ()) / offset) + 0.5
        );

        poseStack.mulPose(fat.front().getOpposite().getRotation());
        poseStack.mulPose(Direction.NORTH.getRotation());

        if (fat.front().getAxis() == Direction.Axis.Y) {
            int dir = fat.front().getAxisDirection().getStep();
            int rot = (dir + 1) * 90;
            poseStack.rotateAround(new Quaternionf(0, 0, 1, 0), 0, 0, 0);
            poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(0, 0, -dir, fat.top().toYRot() + rot));
        }

        if (is3d) poseStack.last().pose().scale(0.7F, 0.7F, 0.005F);
        else poseStack.scale(0.6F, 0.6F, 0.6F);

        itemRenderer.renderStatic(item, ItemDisplayContext.GUI, light, packedOverlay, poseStack, buffer, level, 0);

        poseStack.popPose();
    }
}
