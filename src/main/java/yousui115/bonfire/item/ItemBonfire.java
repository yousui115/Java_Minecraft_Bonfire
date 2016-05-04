package yousui115.bonfire.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.bonfire.entity.EntityBonfire;

public class ItemBonfire extends Item
{
    public ItemBonfire()
    {
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    /**
     * ■このアイテムを持って、ブロックに対して右クリック
     */
    @Override
//    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        //■ブロックの上面が対象
        if(facing != EnumFacing.UP) { return EnumActionResult.FAIL; }

        //■Entity生成
        EntityBonfire eb = new EntityBonfire(worldIn, pos.getX(), pos.getY()+1, pos.getZ());

        //■生存可能条件の確認
        if(eb.canStay() == false) { return EnumActionResult.FAIL; }

        if(!worldIn.isRemote)
        {
            //■顕現
            worldIn.spawnEntityInWorld(eb);
        }

        stack.stackSize--;

        return EnumActionResult.SUCCESS;
    }

    /**
     * ■allows items to add custom lines of information to the mouseover description
     *
     * @param tooltip All lines to display in the Item's tooltip. This is a List of Strings.
     * @param advanced Whether the setting "Advanced tooltips" is enabled
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
    {

    }

    /**
     * ■
     */
    public String getItemStackDisplayName(ItemStack stack)
    {
        return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
    }

}
