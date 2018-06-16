package yousui115.bonfire.other;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.api.thirst.WaterType;
import toughasnails.thirst.ThirstHandler;


public class UtilTAN
{
    public static boolean onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        //■喉の渇きハンドラ
        ThirstHandler thirstHandler = (ThirstHandler)ThirstHelper.getThirstData(playerIn);

        //■喉が渇いてる？
        if (thirstHandler.isThirsty())
        {
//            playerIn.setActiveHand(handIn);
            return true;
        }

        return false;
    }

    public static void onItemUseFinish(ItemStack stackIn, World worldIn, EntityPlayer playerIn)
    {
        IThirst thirst = ThirstHelper.getThirstData(playerIn);

        thirst.addStats(WaterType.PURIFIED.getThirst(), WaterType.PURIFIED.getThirst());
    }
}
