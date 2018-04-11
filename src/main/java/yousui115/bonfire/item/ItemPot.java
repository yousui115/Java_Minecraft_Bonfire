package yousui115.bonfire.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPot extends Item
{
    public static final EnumPotState enumPots[] = EnumPotState.values();

    public ItemPot()
    {
        super();

        setContainerItem(this);
    }

    /**
     * ■クリエイティブモードの取り出し
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!this.isInCreativeTab(tab)) { return; }

        for (EnumPotState pot : enumPots)
        {
            items.add(new ItemStack(this, 1, pot.ordinal()));
        }
    }

//    @Override
//    public String getUnlocalizedName(ItemStack stack)
//    {
//        int meta = MathHelper.clamp(stack.getMetadata(), 0, enumPots.length - 1);
//        return super.getUnlocalizedName(stack) + "." + enumPots[meta].itemName;
//    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        int meta = MathHelper.clamp(stack.getMetadata(), 0, enumPots.length - 1);

        tooltip.add("State : " + enumPots[meta].itemName);
    }



    /**
     * ■右クリックしたら呼ばれる。
     *   (ItemBucketを丸p参考にして作成)
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        //■空のポットにしか用が無い。
        if (itemstack.getMetadata() != EnumPotState.EMPTY.ordinal())
        {
            return new ActionResult(EnumActionResult.PASS, itemstack);
        }

        //■れいとれーす！(エイム先のブロックやらエンティティを拾ってくる）
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

        //■
        if (raytraceresult != null &&
            raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            //▼ブロックに焦点が当たってる。
            BlockPos blockpos = raytraceresult.getBlockPos();

            //■
            if (!worldIn.isBlockModifiable(playerIn, blockpos))
            {
                return new ActionResult(EnumActionResult.FAIL, itemstack);
            }

            //■
            if (!playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack))
            {
                return new ActionResult(EnumActionResult.FAIL, itemstack);
            }

            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            Material material = iblockstate.getMaterial();

            if (material == Material.WATER && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
            {
                //■置き換え
                worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 11);

                //■効果音
                playerIn.addStat(StatList.getObjectUseStats(this));
                playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);

                //■結果を返す
                ItemStack copy = itemstack.copy();
                copy.setItemDamage(EnumPotState.WATER.ordinal());
                return new ActionResult(EnumActionResult.SUCCESS, copy);
            }
            else
            {
                return new ActionResult(EnumActionResult.FAIL, itemstack);
            }

        }

        return new ActionResult(EnumActionResult.PASS, itemstack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        //■コンテナ持ち
        if (hasContainerItem(itemStack))
        {
            ItemStack copy = itemStack.copy();
            copy.setItemDamage(EnumPotState.EMPTY.ordinal());
            return copy;
        }

        return ItemStack.EMPTY;
    }

    public enum EnumPotState
    {
        EMPTY(  "empty",  "bf_pot_empty"),
        WATER(  "water",  "bf_pot_water"),
        BOILED( "boiled", "bf_pot_boiled");

        public final String itemName;
        public final String modelName;

        private EnumPotState(String itemNameIn, String modelNameIn)
        {
            itemName  = itemNameIn;
            modelName = modelNameIn;
        }
    }
}