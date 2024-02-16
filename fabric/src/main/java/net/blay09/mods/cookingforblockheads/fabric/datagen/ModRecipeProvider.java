package net.blay09.mods.cookingforblockheads.fabric.datagen;

import net.blay09.mods.balm.api.tag.BalmItemTags;
import net.blay09.mods.cookingforblockheads.block.ModBlocks;
import net.blay09.mods.cookingforblockheads.block.OvenBlock;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.blay09.mods.cookingforblockheads.tag.ModBlockTags;
import net.blay09.mods.cookingforblockheads.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;
import static net.minecraft.data.recipes.SimpleCookingRecipeBuilder.smelting;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        shaped(RecipeCategory.DECORATIONS, ModBlocks.cookingTable)
                .pattern("SSS")
                .pattern("CBC")
                .pattern("CCC")
                .define('S', BalmItemTags.STONES)
                .define('C', Blocks.TERRACOTTA)
                .define('B', ModItems.craftingBook)
                .unlockedBy("has_crafting_book", has(ModItems.craftingBook))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.fruitBasket)
                .pattern("SPS")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('P', ItemTags.WOODEN_PRESSURE_PLATES)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.ovens[DyeColor.WHITE.ordinal()])
                .pattern("GGG")
                .pattern("IFI")
                .pattern("III")
                .define('I', BalmItemTags.IRON_INGOTS)
                .define('G', Blocks.BLACK_STAINED_GLASS)
                .define('F', Blocks.FURNACE)
                .unlockedBy("has_furnace", has(Blocks.FURNACE))
                .save(exporter);

        @SuppressWarnings("unchecked") final TagKey<Item>[] dyeItemTags = new TagKey[]{
                BalmItemTags.WHITE_DYES,
                BalmItemTags.ORANGE_DYES,
                BalmItemTags.MAGENTA_DYES,
                BalmItemTags.LIGHT_BLUE_DYES,
                BalmItemTags.YELLOW_DYES,
                BalmItemTags.LIME_DYES,
                BalmItemTags.PINK_DYES,
                BalmItemTags.GRAY_DYES,
                BalmItemTags.LIGHT_GRAY_DYES,
                BalmItemTags.CYAN_DYES,
                BalmItemTags.PURPLE_DYES,
                BalmItemTags.BLUE_DYES,
                BalmItemTags.BROWN_DYES,
                BalmItemTags.GREEN_DYES,
                BalmItemTags.RED_DYES,
                BalmItemTags.BLACK_DYES
        };

        for (final var oven : ModBlocks.ovens) {
            final var color = oven.getColor();
            shapeless(RecipeCategory.DECORATIONS, oven)
                    .requires(ModItemTags.OVENS)
                    .requires(dyeItemTags[color.ordinal()])
                    .unlockedBy("has_oven", has(ModBlocks.ovens[DyeColor.WHITE.ordinal()]))
                    .save(exporter, "dye_" + color.getSerializedName() + "_oven");
        }

        shapeless(RecipeCategory.DECORATIONS, ModBlocks.fridge)
                .requires(BalmItemTags.WOODEN_CHESTS)
                .requires(Blocks.IRON_DOOR)
                .unlockedBy("has_iron_ingot", has(BalmItemTags.IRON_INGOTS))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.milkJar)
                .pattern("GPG")
                .pattern("GMG")
                .pattern("GGG")
                .define('G', Blocks.GLASS)
                .define('P', ItemTags.PLANKS)
                .define('M', Items.MILK_BUCKET)
                .unlockedBy("has_milk_bucket", has(Items.MILK_BUCKET))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.hangingCorner)
                .pattern("CCC")
                .pattern("C C")
                .define('C', Blocks.TERRACOTTA)
                .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.corner)
                .pattern("SSS")
                .pattern("CCC")
                .pattern("CCC")
                .define('S', BalmItemTags.STONES)
                .define('C', Blocks.TERRACOTTA)
                .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.counter)
                .pattern("SSS")
                .pattern("CBC")
                .pattern("CCC")
                .define('S', BalmItemTags.STONES)
                .define('C', Blocks.TERRACOTTA)
                .define('B', BalmItemTags.WOODEN_CHESTS)
                .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.sink)
                .pattern("III")
                .pattern("CBC")
                .pattern("CCC")
                .define('I', BalmItemTags.IRON_INGOTS)
                .define('C', Blocks.TERRACOTTA)
                .define('B', Items.WATER_BUCKET)
                .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.cabinet)
                .pattern("CCC")
                .pattern("CBC")
                .define('C', Blocks.TERRACOTTA)
                .define('B', BalmItemTags.WOODEN_CHESTS)
                .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.toolRack)
                .pattern("SSS")
                .pattern("I I")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('I', BalmItemTags.IRON_NUGGETS)
                .unlockedBy("has_iron_ingot", has(BalmItemTags.IRON_INGOTS))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.spiceRack)
                .pattern("SS")
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.toaster)
                .pattern("  B")
                .pattern("IDI")
                .pattern("ILI")
                .define('I', BalmItemTags.IRON_INGOTS)
                .define('B', Blocks.STONE_BUTTON)
                .define('D', Blocks.IRON_TRAPDOOR)
                .define('L', Items.LAVA_BUCKET)
                .unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
                .save(exporter);

        shaped(RecipeCategory.MISC, ModItems.craftingBook)
                .pattern(" D ")
                .pattern("CBC")
                .pattern(" D ")
                .define('D', BalmItemTags.DIAMONDS)
                .define('C', Blocks.CRAFTING_TABLE)
                .define('B', ModItems.recipeBook)
                .unlockedBy("has_recipe_book", has(ModItems.recipeBook))
                .save(exporter);

        shapeless(RecipeCategory.MISC, ModItems.noFilterBook)
                .requires(ModItems.recipeBook)
                .unlockedBy("has_recipe_book", has(ModItems.recipeBook))
                .save(exporter);

        shaped(RecipeCategory.MISC, ModItems.heatingUnit)
                .pattern("NNN")
                .pattern("ICI")
                .define('N', BalmItemTags.IRON_NUGGETS)
                .define('I', BalmItemTags.IRON_INGOTS)
                .define('C', Blocks.COMPARATOR)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(exporter);

        shaped(RecipeCategory.MISC, ModItems.preservationChamber)
                .pattern("RRR")
                .pattern("ICI")
                .define('R', Items.REDSTONE)
                .define('I', BalmItemTags.IRON_INGOTS)
                .define('C', Blocks.COMPARATOR)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(exporter);

        shaped(RecipeCategory.MISC, ModItems.iceUnit)
                .pattern("SSS")
                .pattern("ICI")
                .define('S', Items.SNOWBALL)
                .define('I', BalmItemTags.IRON_INGOTS)
                .define('C', Blocks.COMPARATOR)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.kitchenFloors[DyeColor.WHITE.ordinal()], 12)
                .pattern("BW")
                .pattern("WB")
                .define('B', Blocks.COAL_BLOCK)
                .define('W', Blocks.QUARTZ_BLOCK)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.kitchenFloors[DyeColor.WHITE.ordinal()], 4)
                .pattern("BW")
                .pattern("WB")
                .define('B', Blocks.WHITE_CONCRETE)
                .define('W', Blocks.BLACK_CONCRETE)
                .unlockedBy("has_white_concrete", has(Items.WHITE_CONCRETE))
                .save(exporter, "kitchen_floor_from_concrete");

        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.ORANGE.ordinal()], BalmItemTags.ORANGE_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.MAGENTA.ordinal()], BalmItemTags.MAGENTA_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.LIGHT_BLUE.ordinal()], BalmItemTags.LIGHT_BLUE_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.YELLOW.ordinal()], BalmItemTags.YELLOW_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.LIME.ordinal()], BalmItemTags.LIME_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.PINK.ordinal()], BalmItemTags.PINK_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.GRAY.ordinal()], BalmItemTags.GRAY_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.LIGHT_GRAY.ordinal()], BalmItemTags.LIGHT_GRAY_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.CYAN.ordinal()], BalmItemTags.CYAN_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.PURPLE.ordinal()], BalmItemTags.PURPLE_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.BLUE.ordinal()], BalmItemTags.BLUE_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.BROWN.ordinal()], BalmItemTags.BROWN_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.GREEN.ordinal()], BalmItemTags.GREEN_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.RED.ordinal()], BalmItemTags.RED_DYES).save(exporter);
        dyedKitchenFloorRecipe(ModBlocks.kitchenFloors[DyeColor.BLACK.ordinal()], BalmItemTags.BLACK_DYES).save(exporter);

        smelting(Ingredient.of(Items.BOOK), RecipeCategory.MISC, ModItems.recipeBook, 0.15f, 200).unlockedBy("has_book", has(Items.BOOK)).save(exporter);
        smelting(Ingredient.of(ModItems.noFilterBook), RecipeCategory.MISC, ModItems.recipeBook, 0f, 200).unlockedBy("has_no_filter_edition",
                has(ModItems.noFilterBook)).save(exporter, "recipe_book_from_smelting_no_filter_edition");
    }

    private static ShapedRecipeBuilder dyedKitchenFloorRecipe(Block kitchenFloor, TagKey<Item> dyeTag) {
        final var whiteKitchenFloor = ModBlocks.kitchenFloors[DyeColor.WHITE.ordinal()];
        return shaped(RecipeCategory.DECORATIONS, kitchenFloor, 8)
                .pattern("FFF")
                .pattern("FDF")
                .pattern("FFF")
                .define('F', whiteKitchenFloor)
                .define('D', dyeTag)
                .unlockedBy("has_kitchen_floor", has(whiteKitchenFloor));
    }
}
