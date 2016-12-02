package yousui115.bonfire.item;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import yousui115.bonfire.Bonfire;

public class RecipesWBottle  implements IRecipe
{

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        ItemStack stackBoiledPot = null;
        ItemStack stackWBottle   = null;
        boolean isDisturb = false;

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack targetStack = inv.getStackInSlot(i);

            if (targetStack == null) { continue; }

            if (targetStack.getItem() instanceof ItemPot &&
                targetStack.getMetadata() == 2 &&
                stackBoiledPot == null)
            {
                stackBoiledPot = targetStack;
            }
            else if (targetStack.getItem() instanceof ItemWBottle &&
                    stackWBottle == null)
            {
                stackWBottle = targetStack;
            }
            else
            {
                return false;
            }
        }

        return stackBoiledPot != null && stackWBottle != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return new ItemStack(Bonfire.itemWBottle, 1);
    }

    @Override
    public int getRecipeSize() { return 2; }

    @Override
    public ItemStack getRecipeOutput() { return null; }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null && itemstack.getItem() instanceof ItemPot)
            {
                aitemstack[i] = new ItemStack(Bonfire.itemPot, 1, 0);
            }
        }

        return aitemstack;
    }

}
