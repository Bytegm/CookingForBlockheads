package net.blay09.mods.cookingforblockheads.client.gui;

import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.api.FoodRecipeWithStatus;
import net.blay09.mods.cookingforblockheads.api.ISortButton;
import net.blay09.mods.cookingforblockheads.menu.comparator.ComparatorHunger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;

public class HungerSortButton implements ISortButton {

    private static final ResourceLocation icon = new ResourceLocation(CookingForBlockheads.MOD_ID, "textures/gui/gui.png");

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public String getTooltip() {
        return "tooltip.cookingforblockheads.sort_by_hunger";
    }

    @Override
    public Comparator<FoodRecipeWithStatus> getComparator(Player player) {
        return new ComparatorHunger(player);
    }

    @Override
    public int getIconTextureX() {
        return 216;
    }

    @Override
    public int getIconTextureY() {
        return 0;
    }

}
