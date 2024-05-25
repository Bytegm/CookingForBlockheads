package net.blay09.mods.cookingforblockheads.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.api.fluid.FluidTank;
import net.blay09.mods.cookingforblockheads.block.BaseKitchenBlock;
import net.blay09.mods.cookingforblockheads.client.ModModels;
import net.blay09.mods.cookingforblockheads.block.entity.MilkJarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class MilkJarRenderer<T extends MilkJarBlockEntity> implements BlockEntityRenderer<T> {

    private static final RandomSource random = RandomSource.create();

    public MilkJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MilkJarBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        FluidTank fluidTank = blockEntity.getFluidTank();
        float milkAmount = fluidTank.getAmount();
        if (milkAmount > 0) {
            poseStack.pushPose();
            RenderUtils.applyBlockAngle(poseStack, blockEntity.getBlockState(), 0f);
            poseStack.translate(-0.5, 0, -0.5);
            poseStack.scale(1f, milkAmount / fluidTank.getCapacity(), 1f);
            dispatcher.getModelRenderer().tesselateBlock(level, getLiquidModel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack, buffer.getBuffer(RenderType.solid()), false, random, 0, 0);
            poseStack.popPose();
        }
    }

    protected BakedModel getLiquidModel() {
        return ModModels.milkJarLiquid.get();
    }

}
