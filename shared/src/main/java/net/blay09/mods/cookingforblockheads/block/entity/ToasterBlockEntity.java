package net.blay09.mods.cookingforblockheads.block.entity;

import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.cookingforblockheads.sound.ModSounds;
import net.blay09.mods.cookingforblockheads.block.ModBlocks;
import net.blay09.mods.cookingforblockheads.block.ToasterBlock;
import net.blay09.mods.cookingforblockheads.registry.CookingRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ToasterBlockEntity extends BalmBlockEntity {

    private static final int UPDATE_INTERVAL = 20;
    private static final int TOAST_TICKS = 1200;

    private final DefaultContainer container = new DefaultContainer(2) {
        @Override
        public void setChanged() {
            ToasterBlockEntity.this.setChanged();
            sync();
        }
    };

    private boolean isDirty;
    private int ticksSinceUpdate;
    private boolean active;
    private int toastTicks;

    public ToasterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.toaster.get(), pos, state);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 0) {
            level.playSound(null, worldPosition, ModSounds.toasterStart.get(), SoundSource.BLOCKS, 1f, 1f);
            return true;
        } else if (id == 1) {
            level.playSound(null, worldPosition, ModSounds.toasterStop.get(), SoundSource.BLOCKS, 1f, 1f);
            return true;
        } else if (id == 2) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        container.deserialize(tagCompound.getCompound("ItemHandler"));
        active = tagCompound.getBoolean("Active");
        toastTicks = tagCompound.getInt("ToastTicks");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("ItemHandler", container.serialize());
        tag.putBoolean("Active", active);
        tag.putInt("ToastTicks", toastTicks);
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        saveAdditional(tag);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ToasterBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (active) {
            toastTicks--;
            if (toastTicks <= 0 && !level.isClientSide) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    ItemStack inputStack = container.getItem(i);
                    if (!inputStack.isEmpty()) {
                        final var toastRecipe = CookingRegistry.getToasterHandler(inputStack);
                        ItemStack outputStack = toastRecipe != null ? toastRecipe.getToasterOutput(inputStack) : ItemStack.EMPTY;
                        if (outputStack.isEmpty()) {
                            outputStack = inputStack;
                        } else {
                            outputStack = outputStack.copy();
                        }
                        ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX() + 0.5f, worldPosition.getY() + 0.75f, worldPosition.getZ() + 0.5f, outputStack);
                        itemEntity.setDeltaMovement(0f, 0.1f, 0f);
                        level.addFreshEntity(itemEntity);
                        container.setItem(i, ItemStack.EMPTY);
                    }
                }
                setActive(false);
            }
            isDirty = true;
        }

        ticksSinceUpdate++;
        if (isDirty && ticksSinceUpdate > UPDATE_INTERVAL) {
            sync();
            ticksSinceUpdate = 0;
            isDirty = false;
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            toastTicks = TOAST_TICKS;
            level.blockEvent(worldPosition, ModBlocks.toaster, 0, 0);
        } else {
            toastTicks = 0;
            level.blockEvent(worldPosition, ModBlocks.toaster, 1, 0);
        }

        level.blockEvent(worldPosition, ModBlocks.toaster, 2, 0);

        BlockState state = level.getBlockState(worldPosition);
        level.setBlockAndUpdate(worldPosition, state.setValue(ToasterBlock.ACTIVE, active));
        isDirty = true;
        setChanged();
    }

    public boolean isActive() {
        return active;
    }

    public float getToastProgress() {
        return 1f - toastTicks / (float) TOAST_TICKS;
    }

    public Container getContainer() {
        return container;
    }

    public boolean isBurningToast() {
        CompoundTag firstTag = container.getItem(0).getTag();
        CompoundTag secondTag = container.getItem(1).getTag();
        return firstTag != null && firstTag.getBoolean("CookingForBlockheadsToasted") || secondTag != null && secondTag.getBoolean("CookingForBlockheadsToasted");
    }
}
