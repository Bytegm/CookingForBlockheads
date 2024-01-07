package net.blay09.mods.cookingforblockheads.api;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Kitchen {
    List<KitchenItemProvider> getItemProviders(@Nullable Player player);
}
