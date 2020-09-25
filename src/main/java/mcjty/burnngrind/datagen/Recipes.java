package mcjty.burnngrind.datagen;

import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.setup.Registration;
import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(FurnacePlusModule.FURNACEPLUS[0].get())
                        .key('P', Blocks.FURNACE)
                        .addCriterion("furnace", hasItem(Items.FURNACE)),
                "iri", "rPr", "iri");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(FurnacePlusModule.FURNACEPLUS[1].get())
                        .key('P', FurnacePlusModule.FURNACEPLUS[0].get())
                        .addCriterion("furnace", hasItem(Items.FURNACE)),
                "iri", "rPr", "iri");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(FurnacePlusModule.FURNACEPLUS[2].get())
                        .key('P', FurnacePlusModule.FURNACEPLUS[1].get())
                        .addCriterion("furnace", hasItem(Items.FURNACE)),
                "iri", "rPr", "iri");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(FurnacePlusModule.FURNACEPLUS[3].get())
                        .key('P', FurnacePlusModule.FURNACEPLUS[2].get())
                        .addCriterion("furnace", hasItem(Items.FURNACE)),
                "iri", "rPr", "iri");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(Registration.BASIC_SPEED_UPGRADE.get())
                        .key('Q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
                        .addCriterion("furnace", hasItem(Items.FURNACE)),
                "QRQ", "RdR", "QRQ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(Registration.SUPER_SPEED_UPGRADE.get())
                        .key('Q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
                        .key('P', Registration.BASIC_SPEED_UPGRADE.get())
                        .addCriterion("furnace", hasItem(Items.FURNACE)),
                "eQe", "QPQ", "eQe");
    }
}
