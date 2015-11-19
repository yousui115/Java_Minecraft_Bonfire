package yousui115.bonfire.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yousui115.bonfire.Bonfire;

public class RecipesStandard
{
    public static void addRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(Bonfire.itemBonfire),
                "##",
                "##",
                '#', Items.stick
        );
    }
}
