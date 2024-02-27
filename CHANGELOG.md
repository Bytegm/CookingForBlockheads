- Fixed connectors not behaving correctly when connecting with other kitchen blocks

- Fixed cooking table erroring and not opening when right-clicked on NeoForge
- Fixed counters and other supported storage not being recognized as kitchen storage on Fabric
- Fixed counters and other supported storage not being recognized as kitchen storage on NeoForge

- Updated to Minecraft 1.20.4
- Added Dyed Ovens
- Added Kitchen Connector block that behaves like stairs, i.e. can be used as inside and outside corners as well as straight connectors
- Added an item for the Cutting Board block, allowing it to be used even without having Pam's installed
- Added excludedRecipes config option to hide certain recipes from showing up in the recipe book
- Added cookingforblockheads:excluded tag to hide certain recipes from showing up in the recipe book
- Added cookingforblockheads:kitchen_item_providers, cookingforblockheads:kitchen_connectors, cookingforblockheads:water, cookingforblockheads:milk, cookingforblockheads:ingredients, cookingforblockheads:foods, cookingforblockheads:utensils tags, replacing the old datapack jsons for compatibility
- Added support for custom item providers on Fabric
- Improved performance of ingredient lookup, especially in large kitchens
- Modernized the API - this is a breaking change and mods that depended on the old API will need to be updated
- Removed old compatibility datapack loading, as everything can be done through tags now
- Removed Corner and Hanging Corner blocks in favor of new Kitchen Connector block