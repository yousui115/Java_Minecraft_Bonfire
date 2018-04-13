package yousui115.bonfire.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
        int posY = pos.getY() + 1;

        //■リプレイス可能なブロック
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block.isReplaceable(worldIn, pos))
        {
            //■「リプレイス可能なブロック（全方位）」
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            posY -= 1;
        }
        else if(facing == EnumFacing.UP && block.isReplaceable(worldIn, pos.up()))
        {
            //■「リプレイス不可なブロック（上面）」＋「一つ上のブロックがリプレイス可能」
            worldIn.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
        }
        else
        {
            return EnumActionResult.FAIL;
        }

        //■Entity生成
        EntityBonfire eb = new EntityBonfire(worldIn, pos.getX(), posY, pos.getZ());

        //■生存可能条件の確認
        if(eb.canStay() == false) { return EnumActionResult.FAIL; }

        if(!worldIn.isRemote)
        {
            //■顕現
            worldIn.spawnEntity(eb);
        }

        //■プレイヤーが持ってるItemBonfireを一つ減らす。
        ItemStack stack = player.getHeldItem(hand);
        stack.shrink(1);

        return EnumActionResult.SUCCESS;
    }

}
