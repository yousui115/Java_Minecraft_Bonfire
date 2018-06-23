package yousui115.bonfire.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
//        if (itemstack.getMetadata() != EnumPotState.EMPTY.ordinal())
//        {
//            return new ActionResult(EnumActionResult.PASS, itemstack);
//        }

        //■れいとれーす！(エイム先のブロックやらエンティティを拾ってくる）
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

        //■エイム先がブロック以外ならパス。
        if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return new ActionResult(EnumActionResult.PASS, itemstack);
        }

        //▼ブロックに焦点が当たってる。
        BlockPos targetBlockPos = raytraceresult.getBlockPos();

        //■Modifiable = 変更可能
        if (worldIn.isBlockModifiable(playerIn, targetBlockPos) == false)
        {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
        }
        //■空っぽのポットの場合
        else if (itemstack.getMetadata() == EnumPotState.EMPTY.ordinal())
        {
            //■操作可能ブロックであるか否か
            if (playerIn.canPlayerEdit(targetBlockPos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack) == false)
            {
                return new ActionResult(EnumActionResult.FAIL, itemstack);
            }

            IBlockState iblockstate = worldIn.getBlockState(targetBlockPos);
            Material material = iblockstate.getMaterial();

            //■対象が水源（流水にあらず）
            if (material == Material.WATER && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
            {
                //■置き換え
                worldIn.setBlockState(targetBlockPos, Blocks.AIR.getDefaultState(), 11);

                //■効果音
                playerIn.addStat(StatList.getObjectUseStats(this));
                playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);

                //■ポットに水を満たす。
                ItemStack copy = itemstack.copy();
                copy.setItemDamage(EnumPotState.WATER.ordinal());

                //■結果を返す
                return new ActionResult(EnumActionResult.SUCCESS, copy);
            }
            else
            {
                return new ActionResult(EnumActionResult.FAIL, itemstack);
            }
        }
        //■水が入ってるポットの場合
        else
        {
            boolean canReplacePos = worldIn.getBlockState(targetBlockPos).getBlock().isReplaceable(worldIn, targetBlockPos);
            BlockPos replaceWaterPos = canReplacePos && raytraceresult.sideHit == EnumFacing.UP ? targetBlockPos : targetBlockPos.offset(raytraceresult.sideHit);

            //■水源を設置しようとしてる場所が書き換え禁止
            if (playerIn.canPlayerEdit(replaceWaterPos, raytraceresult.sideHit, itemstack) == false)
            {
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
            }
            else if (this.tryPlaceContainedLiquid(playerIn, worldIn, replaceWaterPos, itemstack))
            {
                if (playerIn instanceof EntityPlayerMP)
                {
                    CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)playerIn, replaceWaterPos, itemstack);
                }

                playerIn.addStat(StatList.getObjectUseStats(this));

                //■ポットに水を満たす。
                ItemStack copy = itemstack.copy();
                copy.setItemDamage(EnumPotState.EMPTY.ordinal());

//                return !playerIn.capabilities.isCreativeMode ? new ActionResult(EnumActionResult.SUCCESS, new ItemStack(Items.BUCKET)) : new ActionResult(EnumActionResult.SUCCESS, itemstack);
                return new ActionResult(EnumActionResult.SUCCESS, copy);
            }
            else
            {
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
            }
        }
//        return new ActionResult(EnumActionResult.PASS, itemstack);
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

    /**
     * ■液体を設置する試み
     */
    public boolean tryPlaceContainedLiquid(@Nullable EntityPlayer player, World worldIn, BlockPos posIn, ItemStack stackIn)
    {
        //■空っぽはいらん
        if (stackIn.getMetadata() == EnumPotState.EMPTY.ordinal())
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(posIn);
            Material material = iblockstate.getMaterial();
            boolean isLiquid = !material.isSolid();
            boolean canReplacePos = iblockstate.getBlock().isReplaceable(worldIn, posIn);

            if (worldIn.isAirBlock(posIn) == false && isLiquid == false && canReplacePos == false)
            {
                return false;
            }
            else
            {
                if (worldIn.provider.doesWaterVaporize() == true)// && this.containedBlock == Blocks.FLOWING_WATER)
                {
                    int l = posIn.getX();
                    int i = posIn.getY();
                    int j = posIn.getZ();
                    worldIn.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                    for (int k = 0; k < 8; ++k)
                    {
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)l + Math.random(), (double)i + Math.random(), (double)j + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                }
                else
                {
                    if (!worldIn.isRemote && (isLiquid || canReplacePos) && !material.isLiquid())
                    {
                        worldIn.destroyBlock(posIn, true);
                    }

                    //■音
//                    SoundEvent soundevent = this.containedBlock == Blocks.FLOWING_LAVA ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
                    SoundEvent soundevent = SoundEvents.ITEM_BUCKET_EMPTY;
                    worldIn.playSound(player, posIn, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    worldIn.setBlockState(posIn, Blocks.FLOWING_WATER.getDefaultState(), 11);
                }

                return true;
            }
        }
    }

    /**
     * ■ポットの状態
     *
     */
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