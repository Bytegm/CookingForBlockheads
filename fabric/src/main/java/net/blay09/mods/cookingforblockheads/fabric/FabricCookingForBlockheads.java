package net.blay09.mods.cookingforblockheads.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.fluid.FluidTank;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.balm.fabric.provider.FabricBalmProviders;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProcessor;
import net.blay09.mods.cookingforblockheads.block.entity.ModBlockEntities;
import net.blay09.mods.cookingforblockheads.kitchen.ContainerKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.tag.ModBlockTags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FabricCookingForBlockheads implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(CookingForBlockheads.MOD_ID, CookingForBlockheads::initialize);

        registerProvider("kitchen_item_provider", KitchenItemProvider.class,
                ModBlockEntities.milkJar.get(), ModBlockEntities.cowJar.get(), ModBlockEntities.fridge.get(),
                ModBlockEntities.sink.get(), ModBlockEntities.cuttingBoard.get());

        registerProvider("kitchen_item_processor", KitchenItemProcessor.class, ModBlockEntities.oven.get());

        registerLookup(new ResourceLocation("balm", "fluid_tank"), FluidTank.class,
                ModBlockEntities.sink.get(), ModBlockEntities.milkJar.get(), ModBlockEntities.cowJar.get());

        var itemProviderLookup = BlockApiLookup.get(new ResourceLocation(CookingForBlockheads.MOD_ID, "kitchen_item_provider"),
                KitchenItemProvider.class,
                Void.class);
        itemProviderLookup.registerFallback((level, pos, state, blockEntity, context) -> {
            if (state.is(ModBlockTags.KITCHEN_ITEM_PROVIDERS)) {
                if (blockEntity instanceof Container container) {
                    return new ContainerKitchenItemProvider(container);
                } else if(blockEntity instanceof BalmContainerProvider containerProvider) {
                    return new ContainerKitchenItemProvider(containerProvider.getContainer());
                }
            }
            return null;
        });
    }

    private <T> void registerProvider(String name, Class<T> clazz, BlockEntityType<?>... blockEntities) {
        var providers = ((FabricBalmProviders) Balm.getProviders());
        ResourceLocation identifier = new ResourceLocation(CookingForBlockheads.MOD_ID, name);
        providers.registerProvider(identifier, clazz);
        registerLookup(identifier, clazz, blockEntities);
    }

    private <T> void registerLookup(ResourceLocation identifier, Class<T> clazz, BlockEntityType<?>... blockEntities) {
        var lookup = BlockApiLookup.get(identifier, clazz, Void.class);
        lookup.registerForBlockEntities((blockEntity, context) -> ((BalmBlockEntity) blockEntity).getProvider(clazz), blockEntities);
    }
}
