package yousui115.bonfire.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemLightBlock extends ItemBlock
{
    public ItemLightBlock(Block block)
    {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        //int i = MathHelper.clamp_int(stack.getMetadata(), 0, 1);
        return super.getUnlocalizedName();// + ".bf_blocklight";
    }
}
