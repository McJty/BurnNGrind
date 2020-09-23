package mcjty.burnngrind.datagen;

import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(FurnacePlusModule.FURNACEPLUS1.get())
//                        .key('P', Blocks.FURNACE)
//                        .addCriterion("furnace", hasItem(Items.FURNACE)),
//                "iri", "rPr", "iri");
    }
}
