package net.blay09.mods.cookingforblockheads.block.entity;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.fluid.BalmFluidTankProvider;
import net.blay09.mods.balm.api.fluid.FluidTank;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.cookingforblockheads.api.IngredientToken;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.compat.Compat;
import net.blay09.mods.cookingforblockheads.tag.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.List;

public class MilkJarBlockEntity extends BalmBlockEntity implements BalmFluidTankProvider {

    protected static final int MILK_CAPACITY = 32000;

    private record MilkJarIngredientToken(MilkJarBlockEntity milkJar, ItemStack itemStack) implements IngredientToken {
        @Override
        public ItemStack peek() {
            final var drained = milkJar.getFluidTank().drain(Compat.getMilkFluid(), 1000, true);
            return drained >= 1000 ? itemStack : ItemStack.EMPTY;
        }

        @Override
        public ItemStack consume() {
            final var drained = milkJar.getFluidTank().drain(Compat.getMilkFluid(), 1000, false);
            return drained >= 1000 ? itemStack : ItemStack.EMPTY;
        }

        @Override
        public ItemStack restore(ItemStack itemStack) {
            milkJar.getFluidTank().fill(Compat.getMilkFluid(), 1000, false);
            return ItemStack.EMPTY;
        }
    }

    private record MilkJarItemProvider(MilkJarBlockEntity milkJar) implements KitchenItemProvider {
        @Override
        public IngredientToken findIngredient(Ingredient ingredient, Collection<IngredientToken> ingredientTokens) {
            for (final var itemStack : ingredient.getItems()) {
                final var found = findIngredient(itemStack, ingredientTokens);
                if (found != null) {
                    return found;
                }
            }

            return null;
        }

        @Override
        public IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens) {
            if (!itemStack.is(ModItemTags.MILK)) {
                return null;
            }

            final var milkUnitsUsed = ingredientTokens.size();
            final var milkUnitsAvailable = milkJar.getFluidTank().getAmount() / 1000 - milkUnitsUsed;
            if (milkUnitsAvailable > 1) {
                return new MilkJarIngredientToken(milkJar, itemStack);
            } else {
                return null;
            }
        }
    }

    private final MilkJarItemProvider itemProvider = new MilkJarItemProvider(this);
    protected final FluidTank milkTank = new FluidTank(MILK_CAPACITY) {
        @Override
        public boolean canFill(Fluid fluid) {
            return fluid.isSame(Compat.getMilkFluid()) && super.canFill(fluid);
        }

        @Override
        public void setChanged() {
            MilkJarBlockEntity.this.setChanged();
            sync();
        }
    };

    public MilkJarBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.milkJar.get(), pos, state);
    }

    protected MilkJarBlockEntity(BlockEntityType<? extends MilkJarBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("FluidTank", milkTank.serialize());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        milkTank.deserialize(tag.getCompound("FluidTank"));
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        saveAdditional(tag);
    }

    @Override
    public FluidTank getFluidTank() {
        return milkTank;
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(new BalmProvider<>(KitchenItemProvider.class, itemProvider));
    }

}
