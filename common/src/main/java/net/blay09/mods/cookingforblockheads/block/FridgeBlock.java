package net.blay09.mods.cookingforblockheads.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.api.Balm;

import net.blay09.mods.cookingforblockheads.util.ItemUtils;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.blay09.mods.cookingforblockheads.block.entity.FridgeBlockEntity;
import net.blay09.mods.cookingforblockheads.block.entity.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FridgeBlock extends BaseKitchenBlock {

    public static final MapCodec<FridgeBlock> CODEC = simpleCodec(FridgeBlock::new);

    public enum FridgeModelType implements StringRepresentable {
        SMALL,
        LARGE_LOWER,
        LARGE_UPPER;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public static final EnumProperty<FridgeModelType> MODEL_TYPE = EnumProperty.create("model", FridgeModelType.class);
    public static final BooleanProperty PRESERVATION_CHAMBER = BooleanProperty.create("preservation_chamber");
    public static final BooleanProperty ICE_UNIT = BooleanProperty.create("ice_unit");

    public FridgeBlock(Properties properties) {
        super(properties.pushReaction(PushReaction.BLOCK).sound(SoundType.METAL).strength(5f, 10f));
        registerDefaultState(getStateDefinition().any().setValue(PRESERVATION_CHAMBER, false).setValue(ICE_UNIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODEL_TYPE, FLIPPED, PRESERVATION_CHAMBER, ICE_UNIT, COLOR, HAS_COLOR);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FridgeBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (itemStack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        final var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FridgeBlockEntity fridge)) {
            return ItemInteractionResult.FAIL;
        }

        if (itemStack.getItem() == ModItems.preservationChamber || itemStack.getItem() == ModItems.iceUnit) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        if (tryRecolorBlock(state, itemStack, level, pos, player, blockHitResult)) {
            return ItemInteractionResult.SUCCESS;
        }

        Direction frontFace = state.getValue(FACING);
        if (blockHitResult.getDirection() == frontFace) {
            if (fridge.getBaseFridge().getDoorAnimator().isForcedOpen()) {
                itemStack = fridge.insertItemStacked(itemStack, false);
                player.setItemInHand(hand, itemStack);
                return ItemInteractionResult.SUCCESS;
            }
        }

        if (Block.byItem(itemStack.getItem()) instanceof FridgeBlock && blockHitResult.getDirection() != frontFace) {
            return ItemInteractionResult.FAIL;
        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, blockHitResult);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        final var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FridgeBlockEntity fridge)) {
            return InteractionResult.FAIL;
        }

        Direction frontFace = state.getValue(FACING);
        if (blockHitResult.getDirection() == frontFace) {
            if (player.isShiftKeyDown()) {
                fridge.getBaseFridge().getDoorAnimator().toggleForcedOpen();
                return InteractionResult.SUCCESS;
            }
        }

        if (!level.isClientSide) {
            Balm.getNetworking().openGui(player, fridge);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        boolean below = level.getBlockState(pos.below()).getBlock() instanceof FridgeBlock;
        boolean above = level.getBlockState(pos.above()).getBlock() instanceof FridgeBlock;
        return !(below && above)
                && !(below && level.getBlockState(pos.below(2)).getBlock() instanceof FridgeBlock)
                && !(above && level.getBlockState(pos.above(2)).getBlock() instanceof FridgeBlock)
                && super.canSurvive(state, level, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(FLIPPED, shouldBePlacedFlipped(context, state.getValue(FACING)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        BlockPos posBelow = currentPos.below();
        BlockState stateBelow = world.getBlockState(posBelow);
        BlockPos posAbove = currentPos.above();
        BlockState stateAbove = world.getBlockState(posAbove);
        if (stateBelow.getBlock() == ModBlocks.fridge && stateBelow.getValue(MODEL_TYPE) == FridgeModelType.SMALL) {
            world.setBlock(posBelow, stateBelow.setValue(MODEL_TYPE, FridgeModelType.LARGE_LOWER).setValue(FACING, state.getValue(FACING)), 3);
            return state.setValue(MODEL_TYPE, FridgeModelType.LARGE_UPPER);
        } else if (stateAbove.getBlock() == ModBlocks.fridge && stateAbove.getValue(MODEL_TYPE) == FridgeModelType.SMALL) {
            world.setBlock(posAbove, stateAbove.setValue(MODEL_TYPE, FridgeModelType.LARGE_UPPER).setValue(FACING, state.getValue(FACING)), 3);
            return state.setValue(MODEL_TYPE, FridgeModelType.LARGE_LOWER);
        }

        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof FridgeBlockEntity) {
                if (((FridgeBlockEntity) blockEntity).hasIceUpgrade()) {
                    ItemUtils.spawnItemStack(level, pos.getX() + 0.5f, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.iceUnit));
                }

                if (((FridgeBlockEntity) blockEntity).hasPreservationUpgrade()) {
                    ItemUtils.spawnItemStack(level, pos.getX() + 0.5f, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.preservationChamber));
                }
            }

            BlockPos posAbove = pos.above();
            BlockState stateAbove = level.getBlockState(posAbove);
            BlockPos posBelow = pos.below();
            BlockState stateBelow = level.getBlockState(posBelow);
            if (stateAbove.getBlock() == ModBlocks.fridge && stateAbove.getValue(MODEL_TYPE) == FridgeModelType.LARGE_UPPER) {
                level.setBlock(posAbove, stateAbove.setValue(MODEL_TYPE, FridgeModelType.SMALL), 3);
            } else if (stateBelow.getBlock() == ModBlocks.fridge && stateBelow.getValue(MODEL_TYPE) == FridgeModelType.LARGE_LOWER) {
                level.setBlock(posBelow, stateBelow.setValue(MODEL_TYPE, FridgeModelType.SMALL), 3);
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide
                ? createTickerHelper(type, ModBlockEntities.fridge.get(), FridgeBlockEntity::clientTick)
                : createTickerHelper(type, ModBlockEntities.fridge.get(), FridgeBlockEntity::serverTick);
    }

    @Override
    protected boolean recolorBlock(BlockState state, LevelAccessor world, BlockPos pos, Direction facing, DyeColor color) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FridgeBlockEntity fridge) {
            BlockPos bottomPos = fridge.getBaseFridge().getBlockPos();
            BlockPos topPos = bottomPos.above();
            return super.recolorBlock(world.getBlockState(bottomPos), world, bottomPos, facing, color) && super.recolorBlock(world.getBlockState(topPos),
                    world,
                    topPos,
                    facing,
                    color);
        }

        return false;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void appendHoverDescriptionText(ItemStack itemStack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.cookingforblockheads.fridge.description").withStyle(ChatFormatting.GRAY));
    }
}
