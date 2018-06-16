package yousui115.bonfire.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yousui115.bonfire.Bonfire;
import yousui115.bonfire.other.UtilTAN;

public class ItemWBottle extends Item
{
    /**
     * ■
     */
    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    /**
     * ■
     */
    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    /**
     * ■
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        //■指定のHandに持っているアイテムを取得
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        //■
        if (itemstack.getItemDamage() < this.getMaxDamage() - 1)
        {
            boolean isThirsty = true;

            if (Bonfire.isTAN == true)
            {
                isThirsty = UtilTAN.onItemRightClick(worldIn, playerIn, handIn);
            }

            if (isThirsty == true)
            {
                playerIn.setActiveHand(handIn);
                return new ActionResult(EnumActionResult.SUCCESS, itemstack);
            }
        }

        return new ActionResult(EnumActionResult.FAIL, itemstack);
    }

    /**
     * ■
     */
    @Override
    public ItemStack onItemUseFinish(ItemStack stackIn, World worldIn, EntityLivingBase entityLiving)
    {
        if (!worldIn.isRemote && entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entityLiving;

            if (Bonfire.isTAN == true)
            {
                UtilTAN.onItemUseFinish(stackIn, worldIn, player);
            }

            int damage = MathHelper.clamp(stackIn.getItemDamage() + 10, 0, this.getMaxDamage() - 1);
            stackIn.setItemDamage(damage);
        }

        return stackIn;
    }


}