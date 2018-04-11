package yousui115.bonfire.recipe;

import javax.annotation.Nonnull;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import yousui115.bonfire.item.ItemWBottle;
import yousui115.bonfire.util.Utils;

public class RecipesWBottle extends ShapelessOreRecipe
{
    public ItemStack defResult;

    /**
     * ■　コンストラクタ群
     */
    public RecipesWBottle(ResourceLocation group, ItemStack result, Object... recipe)
    {
        super(group, result, recipe);

        defResult = result;
    }

    /**
     * ■マッチン、グー！
     */
    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        if (super.matches(inv, world) == true)
        {
            //■レシピはあってる。
            for (int i = 0; i < inv.getSizeInventory(); ++i)
            {
                ItemStack itemstack = inv.getStackInSlot(i);
                if (Utils.isStackEmpty(itemstack) == false
                    && itemstack.getItem() instanceof ItemWBottle)
                {
                    //■出力の水筒は、入力の水筒のコピー品
                    output = itemstack.copy();
                    output.setItemDamage(0);
                    break;
                }
            }
            return true;
        }

        output = defResult;

        return false;
    }
    /**
     * ■ポットは空にして返すよ。
     */
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack.getItem().hasContainerItem(itemstack))
            {
                nonnulllist.set(i, itemstack.getItem().getContainerItem(itemstack));
            }
        }

        return nonnulllist;

    }
}
