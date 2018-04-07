package yousui115.bonfire.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yousui115.bonfire.entity.EntityBonfire;

public class ItemBonfire extends Item
{
    /**
     * ■このアイテムを持って、ブロックに対して右クリック
     */
    @Override
//    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
//    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {

        ItemStack stack = player.getHeldItem(hand);

        //■ブロックの上面が対象
        if(facing != EnumFacing.UP) { return EnumActionResult.FAIL; }

        //■Entity生成
        EntityBonfire eb = new EntityBonfire(worldIn, pos.getX(), pos.getY()+1, pos.getZ());

        //■生存可能条件の確認
//        if(eb.canStay() == false) { return EnumActionResult.FAIL; }

        if(!worldIn.isRemote)
        {
            //■顕現
            worldIn.spawnEntity(eb);
        }

        stack.shrink(1);

        return EnumActionResult.SUCCESS;
    }

}
