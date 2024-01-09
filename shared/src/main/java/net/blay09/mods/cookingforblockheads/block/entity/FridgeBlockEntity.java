package net.blay09.mods.cookingforblockheads.block.entity;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.block.entity.CustomRenderBoundingBox;
import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.CombinedContainer;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.cookingforblockheads.api.IngredientToken;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProcessor;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.blay09.mods.cookingforblockheads.kitchen.CombinedKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.kitchen.ConditionalKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.kitchen.ContainerKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.sound.ModSounds;
import net.blay09.mods.cookingforblockheads.block.FridgeBlock;
import net.blay09.mods.cookingforblockheads.menu.FridgeMenu;
import net.blay09.mods.cookingforblockheads.block.entity.util.DoorAnimator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class FridgeBlockEntity extends BalmBlockEntity implements BalmMenuProvider, IMutableNameable, BalmContainerProvider, CustomRenderBoundingBox {

    private final DefaultContainer container = new DefaultContainer(27) {
        @Override
        public void setChanged() {
            isDirty = true;
            FridgeBlockEntity.this.setChanged();
        }
    };

    public record IceUnitIngredientToken(ItemStack itemStack) implements IngredientToken {
        @Override
        public ItemStack peek() {
            return itemStack;
        }

        @Override
        public ItemStack consume() {
            return itemStack;
        }

        @Override
        public ItemStack restore(ItemStack itemStack) {
            return ItemStack.EMPTY;
        }
    }

    private final KitchenItemProvider iceUnitItemProvider = new KitchenItemProvider() {
        private final Set<ItemStack> providedItems = Set.of(new ItemStack(Items.SNOW), new ItemStack(Items.ICE));

        @Override
        public IngredientToken findIngredient(Ingredient ingredient, Collection<IngredientToken> ingredientTokens) {
            for (final var providedItem : providedItems) {
                if (ingredient.test(providedItem)) {
                    return new IceUnitIngredientToken(providedItem);
                }
            }
            return null;
        }

        @Override
        public IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens) {
            for (final var providedItem : providedItems) {
                if (ItemStack.isSameItem(providedItem, itemStack)) {
                    return new IceUnitIngredientToken(providedItem);
                }
            }
            return null;
        }
    };

    private final ContainerKitchenItemProvider conservingItemProvider = new ContainerKitchenItemProvider(container) {
        @Override
        protected int getUsesLeft(int slot, ItemStack slotStack, Collection<IngredientToken> ingredientTokens) {
            return super.getUsesLeft(slot, slotStack, ingredientTokens) - 1;
        }
    };

    private final ContainerKitchenItemProvider containerItemProvider = new ContainerKitchenItemProvider(container);

    private final KitchenItemProvider itemProvider = new CombinedKitchenItemProvider(List.of(
            new ConditionalKitchenItemProvider(this::hasIceUpgrade, iceUnitItemProvider),
            new ConditionalKitchenItemProvider(this::hasPreservationUpgrade, conservingItemProvider, containerItemProvider)));

    private final DoorAnimator doorAnimator = new DoorAnimator(this, 1, 2);

    private Component customName;

    private boolean isDirty;
    public boolean hasIceUpgrade;
    public boolean hasPreservationUpgrade;

    public FridgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.fridge.get(), pos, state);
        doorAnimator.setOpenRadius(2);
        doorAnimator.setSoundEventOpen(ModSounds.fridgeOpen.get());
        doorAnimator.setSoundEventClose(ModSounds.fridgeClose.get());
    }

    public boolean hasIceUpgrade() {
        return hasIceUpgrade;
    }

    public void setHasIceUpgrade(boolean hasIceUpgrade) {
        this.hasIceUpgrade = hasIceUpgrade;
        markDirtyAndUpdate();
    }

    public boolean hasPreservationUpgrade() {
        return hasPreservationUpgrade;
    }

    public void setHasPreservationUpgrade(boolean hasPreservationUpgrade) {
        this.hasPreservationUpgrade = hasPreservationUpgrade;
        markDirtyAndUpdate();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, FridgeBlockEntity blockEntity) {
        blockEntity.clientTick(level, pos, state);
    }

    public void clientTick(Level level, BlockPos pos, BlockState state) {
        doorAnimator.update();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FridgeBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (isDirty) {
            sync();
            isDirty = false;
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        return doorAnimator.receiveClientEvent(id, type) || super.triggerEvent(id, type);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        container.deserialize(tag.getCompound("ItemHandler"));
        hasIceUpgrade = tag.getBoolean("HasIceUpgrade");
        hasPreservationUpgrade = tag.getBoolean("HasPreservationUpgrade");

        if (tag.contains("CustomName", Tag.TAG_STRING)) {
            customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        }

        if (tag.contains("IsForcedOpen", Tag.TAG_BYTE)) {
            doorAnimator.setForcedOpen(tag.getBoolean("IsForcedOpen"));
        }

        if (tag.contains("NumPlayersUsing", Tag.TAG_BYTE)) {
            doorAnimator.setNumPlayersUsing(tag.getByte("NumPlayersUsing"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("ItemHandler", container.serialize());
        tag.putBoolean("HasIceUpgrade", hasIceUpgrade);
        tag.putBoolean("HasPreservationUpgrade", hasPreservationUpgrade);

        if (customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(customName));
        }
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        saveAdditional(tag);
        tag.putBoolean("IsForcedOpen", doorAnimator.isForcedOpen());
        tag.putByte("NumPlayersUsing", (byte) doorAnimator.getNumPlayersUsing());
    }

    @Nullable
    public FridgeBlockEntity findNeighbourFridge() {
        if (level.getBlockState(worldPosition.above()).getBlock() instanceof FridgeBlock) {
            return (FridgeBlockEntity) level.getBlockEntity(worldPosition.above());
        } else if (level.getBlockState(worldPosition.below()).getBlock() instanceof FridgeBlock) {
            return (FridgeBlockEntity) level.getBlockEntity(worldPosition.below());
        }

        return null;
    }

    public FridgeBlockEntity getBaseFridge() {
        if (!hasLevel()) {
            return this;
        }

        if (level.getBlockState(worldPosition.below()).getBlock() instanceof FridgeBlock) {
            FridgeBlockEntity baseFridge = (FridgeBlockEntity) level.getBlockEntity(worldPosition.below());
            if (baseFridge != null) {
                return baseFridge;
            }
        }

        return this;
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(new BalmProvider<>(KitchenItemProvider.class, itemProvider));
    }

    public DoorAnimator getDoorAnimator() {
        return doorAnimator;
    }

    public Container getCombinedContainer() {
        FridgeBlockEntity baseFridge = getBaseFridge();
        FridgeBlockEntity neighbourFridge;
        if (baseFridge == this) {
            neighbourFridge = findNeighbourFridge();
        } else {
            neighbourFridge = this;
        }

        if (neighbourFridge != null) {
            return new CombinedContainer(neighbourFridge.container, baseFridge.container);
        }

        return container;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-1, 0, -1).getCenter(), worldPosition.offset(2, 2, 2).getCenter());
    }

    public void markDirtyAndUpdate() {
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 3);
        setChanged();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new FridgeMenu(i, playerInventory, this);
    }

    @Override
    public Component getName() {
        return customName != null ? customName : getDefaultName();
    }

    @Override
    public void setCustomName(Component customName) {
        this.customName = customName;
        setChanged();
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("container.cookingforblockheads.fridge");
    }

    @Override
    public Container getContainer() {
        return container;
    }
}
