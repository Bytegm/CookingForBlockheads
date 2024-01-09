package net.blay09.mods.cookingforblockheads.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface FoodStatsProvider {
    float getSaturationModifier(ItemStack itemStack, Player entityPlayer);

    int getNutrition(ItemStack itemStack, Player entityPlayer);
}
