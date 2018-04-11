package yousui115.bonfire.util;

import net.minecraft.item.ItemStack;

public class Utils
{
    public static boolean isStackEmpty(ItemStack stackIn)
    {
        return stackIn == null ? true : stackIn.isEmpty() ? true : false;
    }
}
